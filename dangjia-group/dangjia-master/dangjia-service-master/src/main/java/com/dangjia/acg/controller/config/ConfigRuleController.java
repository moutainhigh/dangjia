package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigRuleAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.configRule.ConfigRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019-12-14

 */
@RestController
public class ConfigRuleController  implements ConfigRuleAPI {

    @Autowired
    private ConfigRuleService configRuleService;

    @Override
    @ApiMethod
    public ServerResponse searchConfigRuleRank() {
         return configRuleService.searchConfigRuleRank();
    }
    @Override
    @ApiMethod
    public ServerResponse editConfigRuleRank(HttpServletRequest request,String rankIds,String scoreStarts,String scoreEnds) {
         return configRuleService.editConfigRuleRank(request,rankIds,scoreStarts,scoreEnds);
    }
    @Override
    @ApiMethod
    public ServerResponse searchConfigRuleModule(String type) {
        return configRuleService.searchConfigRuleModule(type);
    }
    @Override
    @ApiMethod
    public ServerResponse getConfigRuleModule(String moduleId,String typeId,String batchCode) {
        return configRuleService.getConfigRuleModule(moduleId,typeId,batchCode);
    }
    @Override
    @ApiMethod
    public ServerResponse setConfigRuleItem(HttpServletRequest request, String moduleId, String itemDataJson) {
        return configRuleService.setConfigRuleItem(request,moduleId,itemDataJson);
    }
    @Override
    @ApiMethod
    public ServerResponse searchConfigRuleFlow(String moduleId) {
        return configRuleService.searchConfigRuleFlow(moduleId);
    }

}
