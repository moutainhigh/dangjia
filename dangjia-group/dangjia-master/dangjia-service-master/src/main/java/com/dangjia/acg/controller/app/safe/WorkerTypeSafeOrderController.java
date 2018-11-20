package com.dangjia.acg.controller.app.safe;

import com.dangjia.acg.api.app.safe.WorkerTypeSafeOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.safe.WorkerTypeSafeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:51
 */
@RestController
public class WorkerTypeSafeOrderController implements WorkerTypeSafeOrderAPI {

    @Autowired
    private WorkerTypeSafeOrderService workerTypeSafeOrderService;
    @Override
    @ApiMethod
    public  ServerResponse changeSafeType(String userToken,String houseFlowId, String workerTypeSafeId, int selected){
        return workerTypeSafeOrderService.changeSafeType(userToken,houseFlowId,workerTypeSafeId,selected);
    }
}
