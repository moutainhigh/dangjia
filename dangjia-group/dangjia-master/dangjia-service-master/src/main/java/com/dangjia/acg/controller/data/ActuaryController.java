package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ActuaryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.data.ActuaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
     * 返回作废的精算列表
     *   budgetOk:
     *          1=待提交精算
     *          2=待业主确认
     *          3=已完成
     *          5=待业主支付
     *          -1=废弃的精算
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryBudgetOk(HttpServletRequest request,PageDTO pageDTO,String name,String budgetOk){
        return actuaryService.getActuaryAll(request,pageDTO,name,budgetOk);

    }
    /**
     * 返回待业主支付精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryWaitPay(HttpServletRequest request,PageDTO pageDTO,String name){
        return actuaryService.getActuaryAll(request,pageDTO,name,"5");

    }

    /**
     * 返回待提交精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryCommit(HttpServletRequest request,PageDTO pageDTO,String name){

        return actuaryService.getActuaryAll(request,pageDTO,name,"1");
    }

    /**
     * 返回待业主确认精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryConfirm(HttpServletRequest request,PageDTO pageDTO,String name){
        return actuaryService.getActuaryAll(request,pageDTO,name,"2");
    }

    /**
     * 返回已完成精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryComplete(HttpServletRequest request,PageDTO pageDTO,String name){
        return actuaryService.getActuaryAll(request,pageDTO,name,"3");
    }

    /**
     * 返回统计列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getStatistics(HttpServletRequest request,PageDTO pageDTO,String name){
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
