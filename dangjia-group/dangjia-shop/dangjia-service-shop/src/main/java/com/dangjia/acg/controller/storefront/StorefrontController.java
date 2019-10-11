package com.dangjia.acg.controller.storefront;


import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;


import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.storefront.StorefrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: StorefrontController
 * @Description: 店铺管理接口类
 * @author: chenyufeng
 * @date: 2018-10-08
 */
@RestController
public class StorefrontController implements BasicsStorefrontAPI {

    @Autowired
    private StorefrontService storefrontService;

    @Override
    @ApiMethod
    public ServerResponse addStorefront(String userToken, String cityId, String storefrontName, String storefrontAddress,
                                        String storefrontDesc, String storefrontLogo, String storekeeperName, String contact, String email) {
        return storefrontService.addStorefront(userToken, cityId, storefrontName, storefrontAddress, storefrontDesc,
                storefrontLogo, storekeeperName, contact, email);
    }

    @Override
    @ApiMethod
    public ServerResponse updateStorefront(String userToken, Storefront storefront) {
        return storefrontService.updateStorefront(userToken,storefront);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierApplicationShopList(HttpServletRequest request, PageDTO pageDTO, String searchKey, String supId, String applicationStatus) {
        return storefrontService.querySupplierApplicationShopList(pageDTO, searchKey, supId, applicationStatus);
    }


}
