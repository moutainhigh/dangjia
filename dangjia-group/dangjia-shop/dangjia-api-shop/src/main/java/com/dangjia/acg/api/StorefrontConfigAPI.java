package com.dangjia.acg.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * qiyuxiang  2019-11.2  店铺运费管理
 */
@Api(description = "店铺配置表")
@FeignClient("dangjia-service-shop")
public interface StorefrontConfigAPI {

    @PostMapping("/web/storefront/freight")
    @ApiOperation(value = "通过店铺id和总价，查询返回运费", notes = "通过店铺id和总价，查询返回运费")
    Double getFreightPrice(@RequestParam("storefrontId") String storefrontId, @RequestParam("totalPrice")Double totalPrice);


}
