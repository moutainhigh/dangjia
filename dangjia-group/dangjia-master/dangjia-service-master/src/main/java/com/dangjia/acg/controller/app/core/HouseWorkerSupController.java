package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseWorkerSupAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.HouseWorkerSupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 11:31
 */
@RestController
public class HouseWorkerSupController implements HouseWorkerSupAPI {
    @Autowired
    private HouseWorkerSupService houseWorkerSupService;

    /**
     * 大管家-首页
     * @param request
     * @param pageDTO
     * @param userToken 大管家TOKEN
     * @param type 订单分类：0:装修单，1:体验单，2，维修单
     * @param houseType 工地状态：1=超期施工
     * @param startTime 开工：1:今日开工，2，本周新开工
     * @param isPlanWeek 周计划：1=未做周计划 暂无其他
     * @param isPatrol 巡查：1=巡查未完成  暂无其他
     * @return
     */
    public ServerResponse getHouseOrderList(HttpServletRequest request, PageDTO pageDTO, String userToken,String nameKey, Integer type, Integer houseType, Integer startTime, Integer isPlanWeek, Integer isPatrol){
        return houseWorkerSupService.getHouseOrderList( request,  pageDTO,  userToken, nameKey,type,houseType,startTime,isPlanWeek,isPatrol);
    }

    @Override
    @ApiMethod
    public ServerResponse surplusList(String userToken, String houseFlowApplyId){
        return houseWorkerSupService.surplusList(houseFlowApplyId);
    }

//    @Override
//    @ApiMethod
//    public ServerResponse auditApply(String houseFlowApplyId,Integer memberCheck){
//        return houseWorkerSupService.auditApply(houseFlowApplyId,memberCheck);
//    }

    @Override
    @ApiMethod
    public ServerResponse tingGongPage(String userToken,String houseFlowApplyId){
        return houseWorkerSupService.tingGongPage(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse applyShutdown(String userToken, String houseFlowId, String applyDec, String startDate, String endDate){
        return houseWorkerSupService.applyShutdown(userToken,houseFlowId,applyDec,startDate,endDate);
    }

    /**
     * 管家停工选择影响顺延的工序列表
     */
    @Override
    @ApiMethod
    public ServerResponse getShutdownWorkerType(String houseId){
        return houseWorkerSupService.getShutdownWorkerType(houseId);
    }
}
