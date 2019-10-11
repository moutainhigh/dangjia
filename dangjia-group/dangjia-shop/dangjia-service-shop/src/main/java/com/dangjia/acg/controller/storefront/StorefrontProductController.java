package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.storefront.StorefrontProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StorefrontController
 * @Description: 店铺商品管理接口类
 * @author: chenyufeng
 * @date: 2019-10-10
 */
@RestController
public class StorefrontProductController implements StorefrontProductAPI {

    @Autowired
    private StorefrontProductService storefrontProductService;


    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductByType(String userToken, String type) {
        return storefrontProductService.queryStorefrontProductByType(userToken, type);
    }

    @Override
    @ApiMethod
    public ServerResponse setSpStatusById(String userToken, String id, String isShelfStatus) {
        return storefrontProductService.setSpStatusById(userToken, id, isShelfStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse setAllStoreProductByIsShelfStatus(String userToken, String isShelfStatus) {
        return storefrontProductService.setAllStoreProductByIsShelfStatus(userToken, isShelfStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse delStorefrontProductById(String userToken, String id) {

        return storefrontProductService.delStorefrontProductById(userToken, id);
    }

    @Override
    @ApiMethod
    public ServerResponse updateStorefrontProductById(String userToken, String id) {

        return storefrontProductService.updateStorefrontProductById(userToken, id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductBykey(String userToken, String key) {

        return storefrontProductService.queryStorefrontProductBykey(userToken, key);
    }
}
