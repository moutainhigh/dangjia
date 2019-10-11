package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.storefront.Storefront;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "店铺商品表")
@FeignClient("dangjia-service-shop")
public interface StorefrontProductAPI {

    @PostMapping("/web/queryStorefrontProductByType")
    @ApiOperation(value = "通过类别查询商品", notes = "通过类别查询商品")
    ServerResponse queryStorefrontProductByType(@RequestParam("userToken") String userToken, Storefront storefront);


}
