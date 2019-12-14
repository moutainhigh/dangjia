package com.dangjia.acg.api.web.reason;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * @description ljl
 */
@FeignClient("dangjia-service-master")
@Api(value = "工匠更换原因配置", description = "工匠更换原因配置")
public interface ReasonMatchAPI {

    @PostMapping("app/reason/addReasonInFo")
    @ApiOperation(value = "新增工匠更换原因", notes = "新增工匠更换原因")
    ServerResponse addReasonInFo(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("remark") String remark);

    @PostMapping("app/reason/queryReasonInFo")
    @ApiOperation(value = "查询工匠更换原因", notes = "查询工匠更换原因")
    ServerResponse queryReasonInFo(
            @RequestParam("request") HttpServletRequest request);

    @PostMapping("app/reason/deleteReasonInFo")
    @ApiOperation(value = "删除工匠更换原因", notes = "删除匠更换原因")
    ServerResponse deleteReasonInFo(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("id") String id);

    @PostMapping("app/reason/upDateReasonInFo")
    @ApiOperation(value = "修改工匠更换原因", notes = "修改工匠更换原因")
    ServerResponse upDateReasonInFo(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("id") String id,
            @RequestParam("remark") String remark);
}
