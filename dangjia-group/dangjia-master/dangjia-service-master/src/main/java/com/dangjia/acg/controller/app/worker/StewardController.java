package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.StewardAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.worker.StewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/28 0028
 * Time: 14:18
 */
@RestController
public class StewardController implements StewardAPI {

    @Autowired
    private StewardService stewardService;


    @Override
    @ApiMethod
    public ServerResponse scanCode(String userToken,String code,String latitude,String longitude){
        return stewardService.scanCode(userToken,code,latitude,longitude);
    }

    @Override
    @ApiMethod
    public ServerResponse workerQrcode(String userToken,String houseFlowId,String latitude,String longitude){
        return stewardService.workerQrcode(houseFlowId,latitude,longitude);
    }

    @Override
    @ApiMethod
    public ServerResponse passShutWork(String userToken,String houseFlowApplyId,String content,int state){
        return stewardService.passShutWork(houseFlowApplyId,content,state);
    }

    @Override
    @ApiMethod
    public ServerResponse readProjectInfo(String houseFlowId){
        return stewardService.readProjectInfo(houseFlowId);
    }
    @Override
    @ApiMethod
    public ServerResponse confirmProjectInfo(String houseFlowId){
        return stewardService.confirmProjectInfo(houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse tellCode(String userToken, String code){
        return stewardService.tellCode(userToken,code);
    }

    @Override
    @ApiMethod
    public ServerResponse stewardQrcode(String houseFlowId,String disclosureIds){
        return stewardService.stewardQrcode(houseFlowId,disclosureIds);
    }

    @Override
    @ApiMethod
    public ServerResponse getCourse(String houseFlowId){
        return stewardService.getCourse(houseFlowId);
    }
}
