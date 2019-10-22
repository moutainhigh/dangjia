package com.dangjia.acg.api.app.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.ShoppingCart;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ChenYufeng
 * @Description 购物车
 * @Date 2019/9/26
 * @Time 上午9:51
 * @Version V2.0.0
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版商品购物车接口", description = "新版商品购物车接口")
public interface ShopCartAPI {

    @PostMapping("app/shopping/addCart")
    @ApiOperation(value = "购物车-->商品加入购物车", notes = "购物车-->商品加入购物车")
    ServerResponse addCart(@RequestParam("userToken") String userToken,
                           @RequestParam("cityId") String cityId,
                           @RequestParam("productId") String productId,
                           @RequestParam("productSn") String productSn,
                           @RequestParam("productName") String productName,
                           @RequestParam("price") String price,
                           @RequestParam("shopCount") String shopCount,
                           @RequestParam("unitName") String unitName,
                           @RequestParam("categoryId") String categoryId,
                           @RequestParam("productType") String productType,
                           @RequestParam("storefrontId") String storefrontId
                           );

    @PostMapping("app/shopping/queryCartList")
    @ApiOperation(value = "购物车-->查询购物车列表接口", notes = "购物车-->查询购物车列表接口")
    ServerResponse queryCartList(@RequestParam("userToken") String userToken,@RequestParam("productId")  String productId );

    @PostMapping("app/shopping/delCart")
    @ApiOperation(value = "购物车-->清空购物车", notes = "购物车-->清空删除购物车")
    ServerResponse delCar(@RequestParam("userToken") String userToken);

    @PostMapping("app/shopping/updateCart")
    @ApiOperation(value = "购物车-->设置购物车商品数量", notes = "购物车-->设置购物车商品数量")
    ServerResponse updateCar(HttpServletRequest request, @RequestParam("userToken") String userToken,@RequestParam("productId") String productId,@RequestParam("shopCount") Integer shopCount);

    @PostMapping("app/shopping/delCheckCart")
    @ApiOperation(value = "购物车-->删除勾选商品", notes = "购物车-->删除勾选商品")
    ServerResponse delCheckCart(@RequestParam("userToken") String userToken,String productId);

    @PostMapping("app/shopping/cartSettle")
    @ApiOperation(value = "购物车-->结算", notes = "购物车-->结算")
    ServerResponse cartSettle(@RequestParam("userToken") String userToken);
}
