package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigAppAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigApp;
import com.dangjia.acg.service.config.ConfigAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07 
 * Time: 16:16
 */
@RestController
public class ConfigAppController implements ConfigAppAPI {

    @Autowired
    private ConfigAppService configAppService;

    /**
     * 版本检测
     * @param configApp
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse checkConfigApp(HttpServletRequest request, ConfigApp configApp){
        return configAppService.checkConfigApp(request,configApp);
    }
    /**
     * 获取所有版本应用
     * @param configApp
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigApps(HttpServletRequest request, ConfigApp configApp) {
        return configAppService.getConfigApps(request,configApp);
    }
    /**
     * 删除版本应用
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delConfigApp(HttpServletRequest request, String id) {
        return configAppService.delConfigApp(request,id);
    }

    /**
     * 修改版本应用
     * @param configApp
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editConfigApp(HttpServletRequest request, ConfigApp configApp) {
        return configAppService.editConfigApp(request,configApp);
    }
    /**
     * 新增版本应用
     * @param configApp
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addConfigApp(HttpServletRequest request,ConfigApp configApp) {
        return configAppService.addConfigApp(request,configApp);
    }
}
