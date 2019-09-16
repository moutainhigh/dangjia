package com.dangjia.acg.api.app.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ChenYufeng
 * @Description 购物车
 * @Date 2019/9/15
 * @Time 上午9:51
 * @Version V2.0.0
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版商品购物车接口", description = "新版商品购物车接口")
public interface ShopCartAPI {

    @PostMapping("/shopcart/add")
    @ApiOperation(value = "加入购物车", notes = "加入购物车")
    ServerResponse add(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken,String productId,int num);


    @PostMapping("/shopcart/getCartList")
    @ApiOperation(value = "获取购物车列表", notes = "获取购物车列表")
    ServerResponse getCartList(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken);


    @PostMapping("/shopcart/updateCartNum")
    @ApiOperation(value = "更新购物车数量", notes = "更新购物车数量")
    ServerResponse updateCartNum(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken,String productId,int num);

    @PostMapping("/shopcart/checkAll")
    @ApiOperation(value = "全选购物车", notes = "全选购物车")
    ServerResponse checkAll(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken,String checked);


    @PostMapping("/shopcart/delCartProduct")
    @ApiOperation(value = "删除勾选商品", notes = "删除勾选商品")
    ServerResponse delCartProduct(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken,String productId);

    @PostMapping("/shopcart/delCart")
    @ApiOperation(value = "删除购物车", notes = "删除购物车")
    ServerResponse delCart(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken);

}
