package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigAppLogoAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigAppLogo;
import com.dangjia.acg.service.config.ConfigAppLogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@RestController
public class ConfigAppLogoController implements ConfigAppLogoAPI {

    @Autowired
    private ConfigAppLogoService configAppLogoService;

    /**
     * 获取当前配置logo
     * @param configAppLogo
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigAppLogo(HttpServletRequest request, ConfigAppLogo configAppLogo){
        return configAppLogoService.getConfigAppLogo(request,configAppLogo);
    }
    /**
     * 获取所有应用LOGO
     * @param configAppLogo
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigAppLogos(HttpServletRequest request, ConfigAppLogo configAppLogo) {
        return configAppLogoService.getConfigAppLogos(request,configAppLogo);
    }
    /**
     * 删除应用LOGO
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delConfigAppLogo(HttpServletRequest request, String id) {
        return configAppLogoService.delConfigAppLogo(request,id);
    }

    /**
     * 修改应用LOGO
     * @param configAppLogo
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editConfigAppLogo(HttpServletRequest request, ConfigAppLogo configAppLogo) {
        return configAppLogoService.editConfigAppLogo(request,configAppLogo);
    }
    /**
     * 新增应用LOGO
     * @param configAppLogo
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addConfigAppLogo(HttpServletRequest request,ConfigAppLogo configAppLogo) {
        return configAppLogoService.addConfigAppLogo(request,configAppLogo);
    }
}
