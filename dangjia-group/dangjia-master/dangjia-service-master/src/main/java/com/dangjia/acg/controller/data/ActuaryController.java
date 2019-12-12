package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ActuaryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.data.ActuaryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ServerResponse getActuaryBudgetOk(HttpServletRequest request,PageDTO pageDTO,String name,String budgetOk, String workerKey,String userId,String budgetStatus,
                                            String decorationType){
        return actuaryService.getActuaryAll(request,pageDTO,name,budgetOk,  workerKey,userId,budgetStatus,decorationType);

    }

    /**
     * 精算设计--查询默认配置的设计商品
     */
    @Override
    @ApiMethod
    public ServerResponse searchActuarialProductList(HttpServletRequest request,String cityId){
        return actuaryService.searchActuarialProductList(cityId);
    }

    /**
     * 查询精算的订单详情
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetOrderDetail(String cityId,String houseId){
        return actuaryService.getBudgetOrderDetail(cityId,houseId);
    }
    /**
     * 返回待业主支付精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryWaitPay(HttpServletRequest request,PageDTO pageDTO,String name,
                                            String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"5",workerKey,null,null,null);

    }

    /**
     * 返回待提交精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryCommit(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){

        return actuaryService.getActuaryAll(request,pageDTO,name,"1",workerKey,null,null,null);
    }

    /**
     * 返回待业主确认精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryConfirm(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"2",workerKey,null,null,null);
    }

    /**
     * 返回已完成精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryComplete(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"3",workerKey,null,null,null);
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
