package com.dangjia.acg.api.web.matter;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.RenovationManual;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 15:28
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台装修指南接口", description = "后台装修指南接口")
public interface WebRenovationManualAPI {

    @PostMapping("web/renovationManual/queryRenovationManual")
    @ApiOperation(value = "查询所有装修指南", notes = "查询所有装修指南")
    ServerResponse queryRenovationManual(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                         @RequestParam("workerTypeId") String workerTypeId,
                                         @RequestParam("name") String name);

    @PostMapping("web/renovationManual/addRenovationManual")
    @ApiOperation(value = "新增装修指南", notes = "新增装修指南")
    ServerResponse addRenovationManual(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("renovationManual") RenovationManual renovationManual);

    @PostMapping("web/renovationManual/updateRenovationManual")
    @ApiOperation(value = "修改装修指南", notes = "修改装修指南")
    ServerResponse updateRenovationManual(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("renovationManual") RenovationManual renovationManual);

    @PostMapping("web/renovationManual/deleteRenovationManual")
    @ApiOperation(value = "删除装修指南", notes = "删除装修指南")
    ServerResponse deleteRenovationManual(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("id") String id);

    @PostMapping("web/renovationManual/getRenovationManualById")
    @ApiOperation(value = "根据id查询装修指南对象", notes = "根据id查询装修指南对象")
    ServerResponse getRenovationManualById(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("id") String id);
}
