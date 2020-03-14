package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.CartAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.service.deliver.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qyx
 * Date: 2019/04/15 0009
 * Time: 10:55
 */
@RestController
public class CartController implements CartAPI {

    @Autowired
    private CartService cartService;


    /**
     * 设置购物车商品数量
     * @param request
     * @param userToken
     * @param cart
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setCart(HttpServletRequest request, String userToken, Cart cart){
        return cartService.setCart(request,userToken,cart);
    }

    @Override
    @ApiMethod
    public ServerResponse setCart1(HttpServletRequest request, String userToken, String jsonStr, String houseId){
        try {
            return cartService.setCart(userToken,jsonStr,houseId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    /**
     * 清空购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse clearCart(String userToken, Cart cart){
        return cartService.clearCart(userToken,cart);
    }

    /**
     * 查询购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCart(String userToken, Cart cart){
        return cartService.queryCart(userToken,cart);
    }
    /**
     * 查询购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCategoryCart(String userToken, Cart cart){
        return cartService.queryCategoryCart(userToken,cart);
    }

    /**
     * 查询人工购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryWorkerCategoryCart(String userToken, Cart cart){
        return cartService.queryWorkerCategoryCart(userToken,cart);
    }


    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    @Override
    @ApiMethod
    public ServerResponse askAndQuit(HttpServletRequest request, String userToken, PageDTO pageDTO, String houseId, String categoryId, String name){
        return cartService.askAndQuit( request,  userToken,  pageDTO,  houseId,  categoryId,  name);
    }

    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String userToken,String houseId){
        return cartService.queryGoodsCategory( request,  userToken, houseId);
    }
}
