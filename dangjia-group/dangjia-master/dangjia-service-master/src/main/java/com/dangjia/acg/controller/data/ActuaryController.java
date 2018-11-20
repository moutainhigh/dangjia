package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ActuaryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.data.ActuaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:07
 */
@RestController
public class ActuaryController implements ActuaryAPI {

    @Autowired
    private ActuaryService actuaryService;

    /**
     * 返回待业主支付精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryWaitPay(){
        return actuaryService.getActuaryWaitPay();
    }

    /**
     * 返回待提交精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryCommit(){
        return actuaryService.getActuaryCommit();
    }

    /**
     * 返回待业主确认精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryConfirm(){
        return actuaryService.getActuaryConfirm();
    }

    /**
     * 返回已完成精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryComplete(){
        return actuaryService.getActuaryComplete();
    }

    /**
     * 返回统计列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getStatistics(){
        return actuaryService.getStatistics();
    }

    /**
     * 返回按日期统计列表
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getStatisticsByDate(String startDate, String endDate){
        return actuaryService.getStatisticsByDate(startDate,endDate);
    }
}
