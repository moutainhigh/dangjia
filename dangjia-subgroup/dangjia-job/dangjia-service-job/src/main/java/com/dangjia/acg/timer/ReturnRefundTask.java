package com.dangjia.acg.timer;

import com.dangjia.acg.api.app.core.HouseFlowAPI;
import com.dangjia.acg.api.app.core.HouseFlowApplyAPI;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.config.ConfigMessageAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.api.refund.RefundAfterSalesJobAPI;
import com.dangjia.acg.api.sale.rob.RobAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigMessage;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 退款、退货退款售后
 *
 * @author fzh
 * @date 2019/11/05
 */
@Component
public class ReturnRefundTask {
    @Autowired
    private RefundAfterSalesJobAPI refundAfterSalesJobAPI;



    private Logger log = LoggerFactory.getLogger(ReturnRefundTask.class);
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 定时审核完工申请
     */
    @Scheduled(cron = "0 0/1 * * * ?")//1分钟执行一次
    public void couponApply() {
        log.info(format.format(new Date()) + "开始执行店铺申请等待商家处理任务...");
        refundAfterSalesJobAPI.returnMechantProcessTime();//店铺申请等待商家处理（到期自动处理)
        log.info(format.format(new Date()) + "结束执行店铺申请等待商家处理任务...");

        log.info(format.format(new Date()) + "开始执行店铺拒绝退货，等待申请平台介入任务...");
        refundAfterSalesJobAPI.returnPlatformInterventionTime();//店铺拒绝退货，等待申请平台介入(到期自动处理）
        log.info(format.format(new Date()) + "结束执行店铺拒绝退货，等待申请平台介入任务...");


        log.info(format.format(new Date()) + "开始执行业主申诉后，等待平台处理任务...");
        refundAfterSalesJobAPI.returnPlatformProcessTime();//业主申诉后，等待平台处理(到期自动处理）
        log.info(format.format(new Date()) + "结束执行业主申诉后，等待平台处理任务...");



    }

}
