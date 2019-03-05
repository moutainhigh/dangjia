package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.web.repair.WebMendWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:41
 */
@RestController
public class WebMendWorkerController implements WebMendWorkerAPI {

    @Autowired
    private MendWorkerService mendWorkerService;

    /**
     * 房子id查询退人工
     */
    @Override
    @ApiMethod
    public ServerResponse workerBackState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        return mendWorkerService.workerBackState(houseId, pageNum, pageSize, beginDate, endDate, likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse mendWorkerList(String mendOrderId) {
        return mendWorkerService.mendWorkerList(mendOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse workerOrderState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        return mendWorkerService.workerOrderState(houseId, pageNum, pageSize, beginDate, endDate, likeAddress);
    }
}
