package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.storefront.Storefront;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: StorefrontController
 * @Description: 店铺商品管理接口类
 * @author: chenyufeng
 * @date: 2019-10-10
 */
@RestController
public class StorefrontProductController implements StorefrontProductAPI {

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductByType(String userToken, Storefront storefront) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse setSpStatusById(String userToken, String id, String isShelfStatus) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse setAllStoreProductByIsShelfStatus(String userToken, String id, String isShelfStatus) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse delStorefrontProductById(String userToken, String id) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse updateStorefrontProductById(String userToken, String id) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductBykey(String userToken, String key) {
        return null;
    }
}
