package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.HouseDesignPayAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.HouseDesignPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HouseDesignPayController implements HouseDesignPayAPI {

    @Autowired
    private HouseDesignPayService houseDesignPayService;

    @Override
    @ApiMethod
    public ServerResponse modifyDesign(HttpServletRequest request, String userToken, String houseId) {
        return houseDesignPayService.modifyDesign(userToken, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmDesign(HttpServletRequest request, String userToken, String houseId, int type) {
        return houseDesignPayService.confirmDesign(userToken, houseId, type);
    }
}
