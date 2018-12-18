package com.dangjia.acg.controller.repair;

import com.dangjia.acg.api.repair.MendWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.FillWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:11
 */
@RestController
public class MendWorkerController implements MendWorkerAPI {

    @Autowired
    private FillWorkerService mendWorkerService;

    @Override
    @ApiMethod
    public ServerResponse repairBudgetWorker(HttpServletRequest request,int type,String workerTypeId, String houseId,String name,
                                        Integer pageNum, Integer pageSize){
        return mendWorkerService.repairBudgetWorker(type,workerTypeId,houseId,name,pageNum,pageSize);
    }
}
