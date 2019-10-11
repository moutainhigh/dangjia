package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.storefront.StorefrontProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.storefront.StorefrontProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 2019.10.10 chenyufeng
 */
@RestController
public class StorefrontProductController implements StorefrontProductAPI {

    @Autowired
    private StorefrontProductService storefrontProductService;

    @Override
    @ApiMethod
    public ServerResponse upperAndLowerRack(String userToken) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse delStorefrontProduct(String userToken) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse editStorefrontProduct(String userToken) {
        return null;
    }
}
