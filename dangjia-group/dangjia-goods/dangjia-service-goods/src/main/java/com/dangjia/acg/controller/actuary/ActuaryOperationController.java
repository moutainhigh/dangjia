package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuaryOperationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:24
 */
@RestController
public class ActuaryOperationController implements ActuaryOperationAPI {

    @Autowired
    private ActuaryOperationService actuaryOperationService;


    @Override
    @ApiMethod
    public ServerResponse getCommo(String gId, String cityId, int type){
        return actuaryOperationService.getCommo(gId,cityId,type);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmActuaryDetail(String userToken,String houseId,String workerTypeId,int type,String cityId){
        return actuaryOperationService.confirmActuaryDetail(userToken,houseId,workerTypeId,type,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmActuary(String userToken, String houseId,String cityId){
        return actuaryOperationService.confirmActuary(userToken,houseId,cityId);
    }
}
