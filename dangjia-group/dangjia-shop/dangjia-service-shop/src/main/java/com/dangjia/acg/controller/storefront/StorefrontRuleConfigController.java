package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontRuleConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.storefront.StorefrontRuleConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorefrontRuleConfigController implements StorefrontRuleConfigAPI {

    @Autowired
    private StorefrontRuleConfigService storefrontRuleConfigService;

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontRuleConfigByIdAndKey(String userId, String storefrontKey) {
        return storefrontRuleConfigService.queryStorefrontRuleConfigByIdAndKey(userId, storefrontKey);
    }
}
