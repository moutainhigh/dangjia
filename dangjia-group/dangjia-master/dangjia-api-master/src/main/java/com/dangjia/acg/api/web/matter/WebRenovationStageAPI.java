package com.dangjia.acg.api.web.matter;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 15:28
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台装修指南阶段配置接口", description = "后台装修指南阶段配置接口")
public interface WebRenovationStageAPI {

    @PostMapping("web/renovationStage/queryRenovationStage")
    @ApiOperation(value = "根据工序id查询所有装修指南阶段配置", notes = "根据工序id查询所有装修指南阶段配置")
    ServerResponse queryRenovationStage();

    @PostMapping("web/renovationStage/addRenovationStage")
    @ApiOperation(value = "新增装修指南阶段配置", notes = "新增装修指南阶段配置")
    ServerResponse addRenovationStage(@RequestParam("name") String name,
                                      @RequestParam("image") String image);

    @PostMapping("web/renovationStage/updateRenovationStage")
    @ApiOperation(value = "修改装修指南阶段配置", notes = "修改装修指南阶段配置")
    ServerResponse updateRenovationStage(@RequestParam("id") String id,
                                         @RequestParam("name") String name,
                                         @RequestParam("image") String image);

    @PostMapping("web/renovationStage/deleteRenovationStage")
    @ApiOperation(value = "删除装修指南阶段配置", notes = "删除装修指南阶段配置")
    ServerResponse deleteRenovationStage(@RequestParam("id") String id);

}
