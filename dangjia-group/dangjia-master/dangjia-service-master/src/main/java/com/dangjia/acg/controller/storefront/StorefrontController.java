package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.storefront.BasicsStorefrontAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;

import com.dangjia.acg.model.storefront.Storefront;
import com.dangjia.acg.service.storefront.StorefrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StorefrontController
 * @Description:  店铺管理接口类
 * @author: chenyufeng
 * @date: 2018-10-08
 */
@RestController
public class StorefrontController implements BasicsStorefrontAPI {


    @Autowired
    private StorefrontService storefrontService;

    @Override
    @ApiMethod
    public ServerResponse addStorefront(String userToken, Storefront storefront) {
        return storefrontService.addStorefront(userToken, storefront);
    }


}
