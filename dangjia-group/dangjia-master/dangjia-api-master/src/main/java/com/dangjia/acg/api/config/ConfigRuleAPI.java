package com.dangjia.acg.api.config;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019-12-14
 */
@FeignClient("dangjia-service-master")
@Api(value = "配置规则相关接口", description = "配置规则相关接口")
public interface ConfigRuleAPI {


    @PostMapping("/config/rule/rank/search")
    @ApiOperation(value = "等级配置查看", notes = "等级配置查看")
    ServerResponse searchConfigRuleRank();

    @PostMapping("/config/rule/rank/edit")
    @ApiOperation(value = "等级配置修改", notes = "等级配置修改")
    ServerResponse editConfigRuleRank(@RequestParam("request") String rankIds,@RequestParam("scoreStarts") String scoreStarts,@RequestParam("scoreEnds") String scoreEnds);

    @PostMapping("/config/rule/module/search")
    @ApiOperation(value = "规则配置列表", notes = "规则配置列表")
    ServerResponse searchConfigRuleModule(@RequestParam("type") String type);

    @PostMapping("/config/rule/module/get")
    @ApiOperation(value = "规则配置明细", notes = "规则配置明细")
    ServerResponse getConfigRuleModule(@RequestParam("moduleId") String moduleId,@RequestParam("typeId") String typeId,@RequestParam("batchCode") String batchCode);

    @PostMapping("/config/rule/module/set")
    @ApiOperation(value = "规则配置修改", notes = "规则配置修改")
    ServerResponse setConfigRuleItem(@RequestParam("request") HttpServletRequest request, @RequestParam("moduleId") String moduleId,@RequestParam("itemDataJson")  String itemDataJson);

    @PostMapping("/config/rule/flow/search")
    @ApiOperation(value = "规则配置流水记录", notes = "规则配置流水记录")
    ServerResponse searchConfigRuleFlow(@RequestParam("moduleId") String moduleId);
}
