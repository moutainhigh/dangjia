package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.CartAPI;
import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CartController  implements CartAPI {
    @Override
    public ServerResponse add(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse getCartList(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse updateCartNum(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse checkAll(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse delCartProduct(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse delCart(HttpServletRequest request, String userToken) {
        return null;
    }
}
