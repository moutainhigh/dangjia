package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * chenyufeng  2019-11.1  店铺运费规则管理
 */
@Api(description = "店铺运费规则表")
@FeignClient("dangjia-service-shop")
public interface StorefrontRuleConfigAPI {

    @PostMapping("/web/queryStorefrontRuleConfigByIdAndKey")
    @ApiOperation(value = "通过用户id和关键值查询店铺运费规则", notes = "通过用户id和关键值查询店铺运费规则")
    ServerResponse queryStorefrontRuleConfigByIdAndKey(@RequestParam("userId") String userId, @RequestParam("storefrontKey") String storefrontKey);


}
