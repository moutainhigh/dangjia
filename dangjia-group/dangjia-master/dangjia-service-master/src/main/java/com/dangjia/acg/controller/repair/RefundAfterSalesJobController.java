package com.dangjia.acg.controller.repair;

import com.dangjia.acg.api.web.repair.RefundAfterSalesJobAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.repair.RefundAfterSalesJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefundAfterSalesJobController implements RefundAfterSalesJobAPI {
    @Autowired
    private RefundAfterSalesJobService refundAfterSalesJobService;
    /**
     * value = "店铺申请等待商家处理（到期自动处理)
     */
    @Override
    @ApiMethod
    public void returnMechantProcessTime(){
        refundAfterSalesJobService.returnMechantProcessTime();
    }

    /**
     * 店铺拒绝退货，等待申请平台介入(到期自动处理)
     */
    @Override
    @ApiMethod
    public void returnPlatformInterventionTime(){
        refundAfterSalesJobService.returnPlatformInterventionTime();
    }

     /**
     * 业主申诉后，等待平台处理(到期自动处理）
     */
    @Override
    @ApiMethod
    public void returnPlatformProcessTime(){
        refundAfterSalesJobService.returnPlatformProcessTime();
    }

    /**
     * 当家贝商城--待收货时间
     */
    @Override
    @ApiMethod
    public void homeShellOrderReceiveTime(){
        refundAfterSalesJobService.homeShellOrderReceiveTime();
    }

    /**
     * 当家贝商城--待退款时间
     */
    @Override
    @ApiMethod
    public void homeShellOrderRefundTime(){
        refundAfterSalesJobService.homeShellOrderRefundTime();
    }
}
