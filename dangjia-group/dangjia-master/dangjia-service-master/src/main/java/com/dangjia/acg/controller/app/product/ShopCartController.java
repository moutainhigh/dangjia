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
    public ServerResponse queryCartList(String userToken,  String productId) {
        return shopCartservice.queryCartList(userToken,productId);
    }

    @Override
    @ApiMethod
    public ServerResponse delCar(String userToken) {
        return shopCartservice.delCar(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCar(HttpServletRequest request, String userToken, String productId, Integer shopCount) {
        return shopCartservice.updateCart(request,userToken,productId,shopCount);
    }

    @Override
    @ApiMethod
    public ServerResponse addCart(String userToken, String cityId,
                                  String productId,
                                  String productSn,String productName,
                                  String price,String shopCount,
                                  String unitName,String categoryId,
                                  String productType,String storefrontId) {
        return shopCartservice.addCart(userToken, cityId,productId,productSn,productName,price,shopCount,unitName,categoryId,productType,storefrontId);
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
