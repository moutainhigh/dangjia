package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigAdvertAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigAdvert;
import com.dangjia.acg.service.config.ConfigAdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@RestController
public class ConfigAdvertController implements ConfigAdvertAPI {

    @Autowired
    private ConfigAdvertService configAdvertService;


    /**
     * 获取所有广告
     *
     * @param configAdvert
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigAdverts(HttpServletRequest request, String userToken, ConfigAdvert configAdvert) {
        return configAdvertService.getConfigAdverts(request, userToken, configAdvert);
    }

    /**
     * 删除广告
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delConfigAdvert(HttpServletRequest request, String id) {
        return configAdvertService.delConfigAdvert(request, id);
    }

    /**
     * 修改广告
     *
     * @param configAdvert
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        return configAdvertService.editConfigAdvert(request, configAdvert);
    }

    /**
     * 新增广告
     *
     * @param configAdvert
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        return configAdvertService.addConfigAdvert(request, configAdvert);
    }
}
