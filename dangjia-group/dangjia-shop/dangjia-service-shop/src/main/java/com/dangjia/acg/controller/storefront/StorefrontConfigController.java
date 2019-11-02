package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.storefront.StorefrontConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorefrontConfigController implements StorefrontConfigAPI {

    @Autowired
    private StorefrontConfigService storefrontConfigService;

    @Override
    @ApiMethod
    public Double getFreightPrice(String storefrontId, Double totalPrice) {
        return storefrontConfigService.getFreightPrice(storefrontId, totalPrice);
    }

}
