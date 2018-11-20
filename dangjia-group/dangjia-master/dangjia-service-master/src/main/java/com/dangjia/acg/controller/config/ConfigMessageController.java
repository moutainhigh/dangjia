package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigMessageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigMessage;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@RestController
public class ConfigMessageController implements ConfigMessageAPI {

    @Autowired
    private ConfigMessageService configMessageService;


    /**
     * 获取所有公告消息
     * @param configMessage
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getConfigMessages(HttpServletRequest request, PageDTO pageDTO, ConfigMessage configMessage) {
        return configMessageService.getConfigMessages(request, pageDTO,configMessage);
    }

    /**
     * 新增公告消息
     * @param configMessage
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addConfigMessage(HttpServletRequest request,ConfigMessage configMessage) {
        return configMessageService.addConfigMessage(request,configMessage);
    }
}
