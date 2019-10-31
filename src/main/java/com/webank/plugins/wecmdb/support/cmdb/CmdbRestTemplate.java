package com.webank.plugins.wecmdb.support.cmdb;

import static org.springframework.http.HttpMethod.GET;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.webank.plugins.wecmdb.support.cmdb.dto.CmdbResponse;

@Component
public class CmdbRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(CmdbRestTemplate.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public <D, R extends CmdbResponse> D get(String targetUrl, Class<R> responseType) {
        log.info("About to call {} ", targetUrl);
        R cmdbResponse = restTemplate.getForObject(targetUrl, responseType);
        log.info("CMDB response: {} ", cmdbResponse);
        validateCmdbResponse(cmdbResponse);
        return (D) cmdbResponse.getData();
    }

    public <D, R extends CmdbResponse> D postForResponse(String targetUrl, Class<R> responseType) {
        return postForResponse(targetUrl, null, responseType);
    }

    @SuppressWarnings("unchecked")
    public <D, R extends CmdbResponse> D postForResponse(String targetUrl, Object postObject, Class<R> responseType) {
        log.info("About to POST {} with postObject {}", targetUrl, postObject);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        R cmdbResponse = restTemplate.postForObject(targetUrl, new HttpEntity<>(postObject, headers), responseType);
        log.info("CMDB response: {} ", cmdbResponse);
        validateCmdbResponse(cmdbResponse);
        return (D) cmdbResponse.getData();
    }

    public <D, R extends CmdbResponse> D uploadSingleFile(String targetUrl, String fileProperty, InputStreamSource inputStreamSource, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(fileProperty, inputStreamSource);

        return postForResponse(targetUrl, new HttpEntity<>(body, headers), responseType);
    }

    public ResponseEntity<byte[]> downloadSingleFile(String targetUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        ResponseEntity<byte[]> response = restTemplate.exchange(targetUrl, GET, new HttpEntity<>(headers), byte[].class);
        validateCmdbResponse(response);
        return response;
    }

    private void validateCmdbResponse(CmdbResponse cmdbResponse) {
        validateCmdbResponse(cmdbResponse, true);
    }

    private void validateCmdbResponse(CmdbResponse cmdbResponse, boolean dataRequired) {
        if (cmdbResponse == null) {
            throw new CmdbRemoteCallException("CMDB call failure due to no response.");
        }
        if (!CmdbResponse.STATUS_CODE_OK.equalsIgnoreCase(cmdbResponse.getStatusCode())) {
            throw new CmdbRemoteCallException("CMDB Error: " + cmdbResponse.getStatusMessage(), cmdbResponse);
        }
        if (dataRequired && cmdbResponse.getData() == null) {
            throw new CmdbRemoteCallException("CMDB call failure due to unexpected empty response.", cmdbResponse);
        }
    }

    private void validateCmdbResponse(ResponseEntity cmdbResponse) {
        validateCmdbResponse(cmdbResponse, true);
    }

    private void validateCmdbResponse(ResponseEntity cmdbResponse, boolean dataRequired) {
        if (cmdbResponse == null) {
            throw new CmdbRemoteCallException("CMDB call failure due to no response.");
        }
        if (!HttpStatus.OK.equals(cmdbResponse.getStatusCode())) {
            throw new CmdbRemoteCallException("CMDB call failure due to unexpected response - " + cmdbResponse);
        }
        if (dataRequired && cmdbResponse.getBody() == null) {
            throw new CmdbRemoteCallException("CMDB call failure due to unexpected empty response.");
        }
    }
}
