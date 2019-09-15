package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.ShopCartAPI;
import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShopCartController implements ShopCartAPI {

    @Override
    public ServerResponse add(HttpServletRequest request, String userToken, String productId, int num) {
        return null;
    }

    @Override
    public ServerResponse getCartList(HttpServletRequest request, String userToken) {
        return null;
    }

    @Override
    public ServerResponse updateCartNum(HttpServletRequest request, String userToken, String productId, int num) {
        return null;
    }

    @Override
    public ServerResponse checkAll(HttpServletRequest request, String userToken, String checked) {
        return null;
    }

    @Override
    public ServerResponse delCartProduct(HttpServletRequest request, String userToken, String productId) {
        return null;
    }

    @Override
    public ServerResponse delCart(HttpServletRequest request, String userToken) {
        return null;
    }
}
