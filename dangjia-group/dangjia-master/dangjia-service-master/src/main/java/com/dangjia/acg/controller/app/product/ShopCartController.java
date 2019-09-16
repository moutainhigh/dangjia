package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.ShopCartAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShopCartController implements ShopCartAPI {

    @Override
    @ApiMethod
    public ServerResponse add(HttpServletRequest request, String userToken, String productId, int num) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse getCartList(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse updateCartNum(HttpServletRequest request, String userToken, String productId, int num) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse checkAll(HttpServletRequest request, String userToken, String checked) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse delCartProduct(HttpServletRequest request, String userToken, String productId) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse delCart(HttpServletRequest request, String userToken) {
        return null;
    }
}
