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
     * 获取广告
     *
     * @param configAdvert APP端需要的控制逻辑
     * @return 需要展示的数据
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigAdverts(HttpServletRequest request, ConfigAdvert configAdvert) {
        return configAdvertService.getConfigAdverts(request, configAdvert);
    }

    /**
     * 获取所有广告
     *
     * @return 广告List
     */
    @Override
    @ApiMethod
    public ServerResponse getAllConfigAdverts() {
        return configAdvertService.getAllConfigAdverts();
    }

    /**
     * 删除广告
     *
     * @param id 待删除的ID
     * @return 状态说明
     */
    @Override
    @ApiMethod
    public ServerResponse delConfigAdvert(HttpServletRequest request, String id) {
        return configAdvertService.delConfigAdvert(id);
    }

    /**
     * 修改广告
     *
     * @param configAdvert 需要修改的广告数据
     * @return 状态说明
     */
    @Override
    @ApiMethod
    public ServerResponse editConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        return configAdvertService.editConfigAdvert(configAdvert);
    }

    /**
     * 新增广告
     *
     * @param configAdvert 临时数据
     * @return 状态说明
     */
    @Override
    @ApiMethod
    public ServerResponse addConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        return configAdvertService.addConfigAdvert(configAdvert);
    }
}
