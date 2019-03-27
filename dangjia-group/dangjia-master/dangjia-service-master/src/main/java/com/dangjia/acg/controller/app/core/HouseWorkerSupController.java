package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseWorkerSupAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.HouseWorkerSupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 11:31
 */
@RestController
public class HouseWorkerSupController implements HouseWorkerSupAPI {
    @Autowired
    private HouseWorkerSupService houseWorkerSupService;


    @Override
    @ApiMethod
    public ServerResponse applyShutdown(String userToken, String houseId, String applyDec, String startDate, String endDate){
        return houseWorkerSupService.applyShutdown(userToken,houseId,applyDec,startDate,endDate);
    }
}
