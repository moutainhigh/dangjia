package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuarialTemplateAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuarialTemplateService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： ActuarialTemplateController
 * @功能描述：
 * @作者信息： lxl
 * @创建时间： 2018-9-20上午13:35:10
 */
@RestController
public class ActuarialTemplateController implements ActuarialTemplateAPI {
    /**
     * service
     */
    @Autowired
    private ActuarialTemplateService actuarialTemplateService;

    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryActuarialTemplate(HttpServletRequest request, PageDTO pageDTO, String workerTypeId, String stateType, String name) {
        return actuarialTemplateService.queryActuarialTemplate(pageDTO, workerTypeId, stateType, name);
    }

    @Override
    @ApiMethod
    public ServerResponse<String> insertActuarialTemplate(HttpServletRequest request, String userId, String name, String styleId, String styleName, String applicableArea,
                                                          Integer stateType, String workerTypeName, Integer workerTypeId) {
        return actuarialTemplateService.insertActuarialTemplate(userId, name, styleId, styleName, applicableArea,
                stateType, workerTypeName, workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse<String> updateActuarialTemplate(HttpServletRequest request, String id, String name, String styleId, String styleName, String applicableArea, Integer stateType) {
        return actuarialTemplateService.updateActuarialTemplate(id, name, styleId, styleName, applicableArea, stateType);
    }

    @Override
    @ApiMethod
    public ServerResponse<String> deleteActuarialTemplate(HttpServletRequest request, String id) {
        return actuarialTemplateService.deleteActuarialTemplate(id);
    }

}

