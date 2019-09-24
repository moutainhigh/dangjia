package com.dangjia.acg.api.app.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.CartDTO;
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

    @PostMapping("/product/shopcart/add")
    @ApiOperation(value = "加入购物车", notes = "加入购物车")
    ServerResponse add(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken, @RequestParam("cartDTO") CartDTO cartDTO);


    @PostMapping("/product/shopcart/getCartList")
    @ApiOperation(value = "获取购物车列表", notes = "获取购物车列表")
    ServerResponse getCartList(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken);


    @PostMapping("/product/shopcart/updateCartNum")
    @ApiOperation(value = "更新购物车数量", notes = "更新购物车数量")
    ServerResponse updateCartNum(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken, String productId, int num);

    @PostMapping("/product/shopcart/checkAll")
    @ApiOperation(value = "全选购物车", notes = "全选购物车")
    ServerResponse checkAll(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken, String checked);


    @PostMapping("/product/shopcart/delCartProduct")
    @ApiOperation(value = "删除勾选商品", notes = "删除勾选商品")
    ServerResponse delCartProduct(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken, String productId);

    @PostMapping("/product/shopcart/delCart")
    @ApiOperation(value = "删除购物车", notes = "删除购物车")
    ServerResponse delCart(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken);


    @PostMapping("/product/shopcart/updateGood")
    @ApiOperation(value = "购物车-商品明细（更换商品）", notes = "购物车-商品明细（更换商品）")
    ServerResponse replaceGood(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken,@RequestParam("oldProductId") String oldProductId,@RequestParam("cartDTO") CartDTO cartDTO);

    @PostMapping("/product/shopcart/settleMent")
    @ApiOperation(value = "购物车-商品结算", notes = "购物车-商品结算")
    ServerResponse settleMent(@RequestParam("request") HttpServletRequest request, @RequestParam("userToken") String userToken);

    @PostMapping("/product/shopcart/setPaying")
    @ApiOperation(value = "待付款提前付款", notes = "待付款提前付款")
    ServerResponse setPaying(@RequestParam("userToken") String userToken, @RequestParam("productId") String productId);


    /**
     * 获取微信签名信息  复用
     */

    /**
     * 获取支付宝签名信息  复用
     */

    /**
     * 支付成功回调  复用
     */

    /**
     * 支付页面接口(通用)
     */
}
