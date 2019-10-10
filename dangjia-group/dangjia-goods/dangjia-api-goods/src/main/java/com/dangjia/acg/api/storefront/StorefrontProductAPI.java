package com.dangjia.acg.api.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @类 名： StorefrontProductController
 * @功能描述：
 * @作者信息： chenyufeng
 * @创建时间： 2019.10.10
 */
@Api(description = "店铺商品表接口")
@FeignClient("dangjia-service-goods")
public interface StorefrontProductAPI {

    @PostMapping("/web/upperAndLowerRack")
    @ApiOperation(value = "店铺商品上下架", notes = "店铺商品上下架")
    ServerResponse upperAndLowerRack(@RequestParam("userToken") String userToken);

    @PostMapping("/web/delStorefrontProduct")
    @ApiOperation(value = "店铺商品删除", notes = "店铺商品删除")
    ServerResponse delStorefrontProduct(@RequestParam("userToken") String userToken);

    @PostMapping("/web/editStorefrontProduct")
    @ApiOperation(value = "编辑店铺商品", notes = "编辑店铺商品")
    ServerResponse editStorefrontProduct(@RequestParam("userToken") String userToken);

}
