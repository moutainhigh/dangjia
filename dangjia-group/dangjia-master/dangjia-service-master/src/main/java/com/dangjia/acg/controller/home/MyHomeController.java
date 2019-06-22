package com.dangjia.acg.controller.home;

import com.dangjia.acg.api.home.MyHomeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.MyHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyHomeController implements MyHomeAPI {

    @Autowired
    private MyHouseService myHouseService;

    @Override
    @ApiMethod
    public ServerResponse getMyHouse(String userToken, String cityId,String isNew) {
        return myHouseService.getMyHouse(userToken, cityId,isNew);
    }

}
