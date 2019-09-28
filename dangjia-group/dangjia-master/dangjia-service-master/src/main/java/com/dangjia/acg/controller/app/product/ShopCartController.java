package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.ShopCartAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.service.product.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShopCartController implements ShopCartAPI {

    @Autowired
    private ShopCartService shopCartservice;

    @Override
    @ApiMethod
    public ServerResponse queryCartList(String userToken, ShoppingCart shoppingCart) {
        return shopCartservice.queryCartList(userToken,shoppingCart);
    }

    @Override
    @ApiMethod
    public ServerResponse delCar(String userToken, ShoppingCart shoppingCart) {
        return shopCartservice.delCar(userToken,shoppingCart);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCar(HttpServletRequest request, String userToken, ShoppingCart shoppingCart) {
        return shopCartservice.updateCart(request,userToken,shoppingCart);
    }

    @Override
    @ApiMethod
    public ServerResponse addCart(String userToken, String cityId,
                                  String memberId,String productId,
                                  String productSn,String productName,
                                  String price,String shopCount,
                                  String unitName,String categoryId,
                                  String productType,String seller) {
        return shopCartservice.addCart(userToken, cityId,memberId,productId,productSn,productName,price,shopCount,unitName,categoryId,productType,seller);
    }



    @Override
    @ApiMethod
    public ServerResponse delCheckCart(String userToken,String productId) {
        return shopCartservice.delCheckCart(userToken,productId);
    }

    @Override
    @ApiMethod
    public ServerResponse cartSettle(String userToken) {
        return shopCartservice.cartSettle(userToken);
    }
}
