package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.MendOrderCheckAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendOrderCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2019/1/18 0018
 * Time: 14:44
 */
@RestController
public class MendOrderCheckController implements MendOrderCheckAPI {
    @Autowired
    private MendOrderCheckService mendOrderCheckService;

    @Override
    @ApiMethod
    public ServerResponse auditSituation(String mendOrderId){
        return mendOrderCheckService.auditSituation(mendOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse checkMendOrder(String userToken, String mendOrderId, String roleType, Integer state){
        return mendOrderCheckService.checkMendOrder(userToken,mendOrderId,roleType,state);
    }
}
