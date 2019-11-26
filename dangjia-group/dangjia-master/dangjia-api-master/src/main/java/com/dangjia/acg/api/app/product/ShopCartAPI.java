package com.dangjia.acg.api.app.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

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

    @PostMapping("app/product/shopCart/addCart")
    @ApiOperation(value = "购物车-->商品加入购物车", notes = "购物车-->商品加入购物车")
    ServerResponse addCart(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("userToken") String userToken,
                           @RequestParam("cityId") String cityId,
                           @RequestParam("productId") String productId,
                           @RequestParam("shopCount") Integer shopCount);

    @PostMapping("app/product/shopCart/queryCartList")
    @ApiOperation(value = "购物车-->查询购物车列表接口", notes = "购物车-->查询购物车列表接口")
    ServerResponse queryCartList(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId);

    @PostMapping("app/product/shopCart/num")
    @ApiOperation(value = "购物车-->查询购物车列表接口", notes = "购物车-->查询购物车商品数量")
    ServerResponse getCartNum(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userToken") String userToken);

    @PostMapping("app/product/shopCart/delCar")
    @ApiOperation(value = "购物车-->清空购物车", notes = "购物车-->清空删除购物车")
    ServerResponse delCar(@RequestParam("request") HttpServletRequest request,
                          @RequestParam("userToken") String userToken);

    @PostMapping("app/shopping/updateCart")
    @ApiOperation(value = "购物车-->设置购物车商品数量", notes = "购物车-->设置购物车商品数量")
    ServerResponse updateCar(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("shopCartId") String shopCartId,
                             @RequestParam("shopCount") Integer shopCount);

    @PostMapping("app/shopping/delCheckCart")
    @ApiOperation(value = "购物车-->删除勾选商品", notes = "购物车-->删除勾选商品")
    ServerResponse delCheckCart(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("shopCartIds") String shopCartIds);

    @PostMapping("app/shopping/cartSettle")
    @ApiOperation(value = "购物车-->结算", notes = "购物车-->结算")
    ServerResponse cartSettle(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userToken") String userToken);

    @PostMapping("app/shopping/replaceShoppingCart")
    @ApiOperation(value = "更换购物车商品", notes = "更换购物车商品")
    ServerResponse replaceShoppingCart(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("shoppingCartId") String shoppingCartId,
                                       @RequestParam("productId") String productId);


    @PostMapping("app/shopping/insertToCollect")
    @ApiOperation(value = "购物车移入收藏", notes = "购物车移入收藏")
    ServerResponse insertToCollect(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("jsonStr") String jsonStr);
}
