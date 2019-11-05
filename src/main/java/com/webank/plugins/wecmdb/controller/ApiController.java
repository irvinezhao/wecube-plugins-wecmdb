package com.webank.plugins.wecmdb.controller;

import static com.webank.plugins.wecmdb.dto.JsonResponse.okayWithData;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webank.plugins.wecmdb.dto.JsonResponse;
import com.webank.plugins.wecmdb.dto.OperateCiDto;
import com.webank.plugins.wecmdb.support.cmdb.CmdbServiceV2Stub;

@RestController
public class ApiController {

    @Autowired
    private CmdbServiceV2Stub cmdbServiceV2Stub;

    @GetMapping("/entities/{entity-name}")
    @ResponseBody
    public JsonResponse retrieveCiData(@PathVariable(value = "entity-name") String entityName,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "sorting", required = false) String sorting,
            @RequestParam(value = "select", required = false) String select) {
        return okayWithData(cmdbServiceV2Stub.getCiDataWithConditions(entityName, filter, sorting, select));
    }

    @PostMapping("/data/confirm")
    @ResponseBody
    public JsonResponse confirmBatchCiData(@RequestBody List<OperateCiDto> operateCiDtos) {
        return okayWithData(cmdbServiceV2Stub.confirmBatchCiData(operateCiDtos));
    }

    @GetMapping("/data-model")
    @ResponseBody
    public JsonResponse getDataModel() {
        return okayWithData(cmdbServiceV2Stub.getDataModel());
    }
}