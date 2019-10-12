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
    @ApiOperation(value = "供货设置-通过货品或者商品名称查询", notes = "供货设置-通过货品或者商品名称查询")
    ServerResponse queryStorefrontProductByType(@RequestParam("userToken") String userToken, @RequestParam("type") String type);


    @PostMapping("/web/setSpStatusById")
    @ApiOperation(value = "供货设置-设置商品上下架", notes = "设置商品上下架")
    ServerResponse setSpStatusById(@RequestParam("userToken") String userToken, @RequestParam("id") String id, @RequestParam("isShelfStatus") String isShelfStatus);


    @PostMapping("/web/setAllStoreProductByIsShelfStatus")
    @ApiOperation(value = "供货设置-设置商品批量架", notes = "设置商品批量架")
    ServerResponse setAllStoreProductByIsShelfStatus(@RequestParam("userToken") String userToken, @RequestParam("isShelfStatus") String isShelfStatus);


    @PostMapping("/web/delStorefrontProductById")
    @ApiOperation(value = "供货设置-根据主键删除商品", notes = "根据主键删除商品")
    ServerResponse delStorefrontProductById(@RequestParam("userToken") String userToken, @RequestParam("id") String id);


    @PostMapping("/web/updateStorefrontProductById")
    @ApiOperation(value = "供货设置-根据id修改店铺商品", notes = "根据id修改店铺商品")
    ServerResponse updateStorefrontProductById(@RequestParam("userToken") String userToken, @RequestParam("id") String id);


}
