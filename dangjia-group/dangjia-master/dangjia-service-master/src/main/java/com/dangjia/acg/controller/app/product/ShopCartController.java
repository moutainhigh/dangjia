package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.ShopCartAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.ShopCartservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShopCartController implements ShopCartAPI {

    @Autowired
    private ShopCartservice shopCartservice;

    @Override
    @ApiMethod
    public ServerResponse add(HttpServletRequest request, String userToken, String productId, int num) {
        return shopCartservice.add(request,userToken,productId,num);
    }

    @Override
    @ApiMethod
    public ServerResponse getCartList(HttpServletRequest request, String userToken) {
        return shopCartservice.getCartList(request,userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCartNum(HttpServletRequest request, String userToken, String productId, int num) {
        return shopCartservice.updateCartNum(request,userToken,productId,num);
    }

    @Override
    @ApiMethod
    public ServerResponse checkAll(HttpServletRequest request, String userToken, String checked) {
        return shopCartservice.checkAll(request,userToken,checked);
    }

    @Override
    @ApiMethod
    public ServerResponse delCartProduct(HttpServletRequest request, String userToken, String productId) {
        return shopCartservice.delCartProduct(request,userToken,productId);
    }

    @Override
    @ApiMethod
    public ServerResponse delCart(HttpServletRequest request, String userToken) {
        return shopCartservice.delCart(request,userToken);
    }
}
