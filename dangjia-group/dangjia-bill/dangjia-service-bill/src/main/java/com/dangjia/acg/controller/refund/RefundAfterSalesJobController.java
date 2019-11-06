package com.dangjia.acg.controller.refund;

import com.dangjia.acg.api.refund.RefundAfterSalesJobAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.refund.RefundAfterSalesJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/10/2019
 * Time: 下午 3:56
 */
@RestController
public class RefundAfterSalesJobController implements RefundAfterSalesJobAPI {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesJobController.class);

    @Autowired
    private RefundAfterSalesJobService refundAfterSalesJobService;


    /**
     * 店铺申请等待商家处理（到期自动处理)
     */
    @Override
    @ApiMethod
    public void returnMechantProcessTime() {
        try{
            refundAfterSalesJobService.returnMechantProcessTime();
        }catch (Exception e) {
            logger.error("returnMechantProcessTime 店铺申请等待商家处理异常： ", e);
        }

    }

    /**
     * 店铺拒绝退货，等待申请平台介入(到期自动处理）
     */
    @Override
    @ApiMethod
    public void returnPlatformInterventionTime() {
        try{
            refundAfterSalesJobService.returnPlatformInterventionTime();
        }catch (Exception e){
            logger.error("returnPlatformInterventionTime 店铺拒绝退货，等待申请平台介入异常： ",e);
        }

    }

    /**
     * 业主申诉后，等待平台处理(到期自动处理）
     */
    @Override
    @ApiMethod
    public void returnPlatformProcessTime() {
        try{
            refundAfterSalesJobService.returnPlatformProcessTime();
        }catch (Exception e){
            logger.error("returnPlatformProcessTime 业主申诉后，等待平台处理异常： ",e);
        }

    }
}
