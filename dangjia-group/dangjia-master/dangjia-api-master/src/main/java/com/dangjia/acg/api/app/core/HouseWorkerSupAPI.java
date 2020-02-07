package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 11:29
 */
@FeignClient("dangjia-service-master")
@Api(value = "施工业务补充", description = "施工业务补充")
public interface HouseWorkerSupAPI {


    /**
     * 大管家-首页
     * @param request
     * @param pageDTO
     * @param userToken 大管家TOKEN
     * @param nameKey 搜索地址
     * @param type 订单分类：0:装修单，1:体验单，2，维修单
     * @param houseType 工地状态：1=超期施工
     * @param startTime 开工：1:今日开工，2，本周新开工
     * @param isPlanWeek 周计划：1=未做周计划 暂无其他
     * @param isPatrol 巡查：1=巡查未完成  暂无其他
     * @return orderTakingTime
     */
    @PostMapping("app/core/house/order")
    @ApiOperation(value = "大管家-首页", notes = "大管家-首页")
    ServerResponse getHouseOrderList(@RequestParam("request")HttpServletRequest request,
                                     @RequestParam("pageDTO")PageDTO pageDTO,
                                     @RequestParam("userToken")String userToken,
                                     @RequestParam("nameKey")String nameKey,
                                     @RequestParam("type")Integer type,
                                     @RequestParam("orderTakingTime")Integer orderTakingTime,
                                     @RequestParam("houseType")Integer houseType,
                                     @RequestParam("startTime")Integer startTime,
                                     @RequestParam("isPlanWeek")Integer isPlanWeek,
                                     @RequestParam("isPatrol")Integer isPatrol);

    @PostMapping("app/core/houseWorkerSup/surplusList")
    @ApiOperation(value = "材料列表", notes = "材料列表")
    ServerResponse surplusList(@RequestParam("userToken")String userToken,@RequestParam("houseFlowApplyId")String houseFlowApplyId);

//    @PostMapping("app/core/houseWorkerSup/auditApply")
//    @ApiOperation(value = "审核停工", notes = "审核停工")
//    ServerResponse auditApply(@RequestParam("houseFlowApplyId")String houseFlowApplyId,
//                              @RequestParam("memberCheck")Integer memberCheck);

    @PostMapping("app/core/houseWorkerSup/tingGongPage")
    @ApiOperation(value = "停工申请内容", notes = "停工申请内容")
    ServerResponse tingGongPage(@RequestParam("userToken")String userToken,
                                @RequestParam("houseFlowApplyId")String houseFlowApplyId);

    @PostMapping("app/core/houseWorkerSup/applyShutdown")
    @ApiOperation(value = "工匠申请停工", notes = "工匠申请停工")
    ServerResponse applyShutdown(@RequestParam("userToken")String userToken, @RequestParam("houseFlowId")String houseFlowId, @RequestParam("applyDec")String applyDec,
                                 @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate);

    /**
     * 管家停工选择影响顺延的工序列表
     */
    @PostMapping("app/core/houseWorkerSup/getShutdownWorkerType")
    @ApiOperation(value = "管家停工选择影响顺延的工序列表", notes = "管家停工选择影响顺延的工序列表")
     ServerResponse getShutdownWorkerType(@RequestParam("houseId")String houseId);


}
