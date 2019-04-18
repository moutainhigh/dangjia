package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.Cart;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qyx
 * Date: 2019/04/15 0009
 * Time: 10:55
 */
@FeignClient("dangjia-service-master")
@Api(value = "购物车及要货操作", description = "购物车及要货操作")
public interface CartAPI {

    @PostMapping("app/cart/setCart")
    @ApiOperation(value = "设置购物车商品数量", notes = "设置购物车商品数量")
     ServerResponse setCart(HttpServletRequest request, String userToken, Cart cart);
 
    
    @PostMapping("app/cart/clearCart")
    @ApiOperation(value = "清空购物车商品", notes = "清空购物车商品")
    ServerResponse clearCart(String userToken, Cart cart);

    
    @PostMapping("app/cart/queryCart")
    @ApiOperation(value = "查询购物车商品", notes = "查询购物车商品")
    ServerResponse queryCart(String userToken, Cart cart);

    @PostMapping("app/cart/category")
    @ApiOperation(value = "查询商品分类", notes = "查询商品分类")
    ServerResponse queryGoodsCategory(HttpServletRequest request, String userToken,String houseId);

    @PostMapping("app/cart/askAndQuit")
    @ApiOperation(value = "要退查询仓库", notes = "要退查询仓库")
    ServerResponse askAndQuit(HttpServletRequest request, String userToken, PageDTO pageDTO, String houseId, String categoryId, String name);
}
