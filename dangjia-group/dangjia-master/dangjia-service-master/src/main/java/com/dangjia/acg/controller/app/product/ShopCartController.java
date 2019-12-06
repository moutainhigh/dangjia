package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.ShopCartAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShopCartController implements ShopCartAPI {

    @Autowired
    private ShopCartService shopCartservice;

    @Override
    @ApiMethod
    public ServerResponse queryCartList(HttpServletRequest request, PageDTO pageDTO,String userToken, String cityId) {
        return shopCartservice.queryCartList(pageDTO,userToken,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCartNum(HttpServletRequest request,String userToken) {
        return shopCartservice.getCartNum(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse delCar(HttpServletRequest request,String userToken) {
        return shopCartservice.delCar(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCar(HttpServletRequest request, String shopCartId, Double shopCount) {
        return shopCartservice.updateCart(shopCartId,shopCount);
    }

    @Override
    @ApiMethod
    public ServerResponse addCart(HttpServletRequest request, String userToken, String cityId, String productId,Double shopCount, String addedProductIds) {
        return shopCartservice.addCart(userToken, cityId,productId,shopCount,  addedProductIds);
    }


    /**
     * 审核加入购物车是否达到条件
     * @param productId
     * @return 0=直接通过,无提示； 1=有房无精算(业主无房时)   2=有房有精算(业主无房无精算时) 3=有房有精算(业主有房无精算时)   4=有房有精算(业主有房无精算时)  5=人工商品
     */
    @Override
    @ApiMethod
    public ServerResponse checkCart(String userToken, String productId){
        return shopCartservice.checkCart(userToken,productId);
    }
    @Override
    @ApiMethod
    public ServerResponse delCheckCart(HttpServletRequest request,String shopCartIds) {
        return shopCartservice.delCheckCart(shopCartIds);
    }


    @Override
    @ApiMethod
    public ServerResponse replaceShoppingCart(HttpServletRequest request, String shoppingCartId, String productId, Double shopCount,String addedProductIds) {
        return shopCartservice.replaceShoppingCart(shoppingCartId, productId, shopCount, addedProductIds);
    }

    @Override
    @ApiMethod
    public ServerResponse insertToCollect(HttpServletRequest request, String userToken, String jsonStr) {
        return shopCartservice.insertToCollect(userToken,jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse addCartBuyAgain(HttpServletRequest request, String userToken, String cityId, String jsonStr) {
        return shopCartservice.addCartBuyAgain(userToken,cityId,jsonStr);
    }
}
