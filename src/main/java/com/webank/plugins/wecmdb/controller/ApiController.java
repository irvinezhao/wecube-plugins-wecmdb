package com.webank.plugins.wecmdb.controller;

import static com.webank.plugins.wecmdb.dto.JsonResponse.okayWithData;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webank.plugins.wecmdb.dto.JsonResponse;
import com.webank.plugins.wecmdb.dto.OperateCiDto;
import com.webank.plugins.wecmdb.support.cmdb.CmdbServiceV2Stub;

@RestController
@RequestMapping("/wecmdb")
public class ApiController {

    @Autowired
    private CmdbServiceV2Stub cmdbServiceV2Stub;

    @PostMapping("/cis/{ci-type-id}/datas/{guid}")
    @ResponseBody
    public JsonResponse retrieveCiData(@PathVariable(value = "ci-type-id") Integer ciTypeId, @PathVariable(value = "guid") String guid) {
        return okayWithData(cmdbServiceV2Stub.getCiDataByGuid(ciTypeId, guid));
    }

    @PostMapping("/cis/{ci-type-id}/datas/{guid}/confirm")
    @ResponseBody
    public JsonResponse implementCiType(@PathVariable(value = "ci-type-id") Integer ciTypeId, @PathVariable(value = "guid") String guid) {
        List<OperateCiDto> operateCiDtos = new ArrayList<>();
        operateCiDtos.add(new OperateCiDto(guid, ciTypeId));
        return okayWithData(cmdbServiceV2Stub.confirmCis(operateCiDtos));
    }
}
