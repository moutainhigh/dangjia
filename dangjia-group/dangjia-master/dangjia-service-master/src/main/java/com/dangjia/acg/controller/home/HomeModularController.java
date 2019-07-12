package com.dangjia.acg.controller.home;

import com.dangjia.acg.api.home.HomeModularAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.home.HomeModularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HomeModularController implements HomeModularAPI {

    @Autowired
    private HomeModularService homeModularService;

    @Override
    @ApiMethod
    public ServerResponse getBroadcastList(HttpServletRequest request, String cityId) {
        return homeModularService.getBroadcastList(cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse getStrategyList(HttpServletRequest request, String userToken, PageDTO pageDTO, Integer type) {
        return homeModularService.getStrategyList(userToken, pageDTO, type);
    }
}
