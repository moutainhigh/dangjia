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
     * 通过 不通过
     * 1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    @Override
    @ApiMethod
    public ServerResponse checkWorkerBackState(String mendOrderId, int state){
        return mendWorkerService.checkWorkerBackState(mendOrderId, state);
    }

    /**
     * 房子id查询退人工
     * workerBackState
     * 0生成中,1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消
     */
    @Override
    @ApiMethod
    public ServerResponse workerBackState(String houseId, Integer pageNum,Integer pageSize){
        return mendWorkerService.workerBackState(houseId,pageNum,pageSize);
    }

    /**
     * 审核补人工
     */
    @Override
    @ApiMethod
    public ServerResponse checkWorkerOrderState(String mendOrderId,int state){
        return mendWorkerService.checkWorkerOrderState(mendOrderId, state);
    }

    /**
     * 人工单明细mendOrderId
     */
    @Override
    @ApiMethod
    public ServerResponse mendWorkerList(String mendOrderId){
        return mendWorkerService.mendWorkerList(mendOrderId);
    }

    /**
     * 普通工匠房子id查询补人工单列表
     * 补人工审核状态
     * 0生成中,1工匠审核中，2工匠不同意，3工匠同意即平台审核中，4平台不同意,5平台同意即待业主支付，6业主已支付，7业主不同意, 8管家取消
     */
    @Override
    @ApiMethod
    public ServerResponse workerOrderState(String houseId,Integer pageNum, Integer pageSize){
        return mendWorkerService.workerOrderState(houseId,pageNum,pageSize);
    }
}
