package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 11:29
 */
@FeignClient("dangjia-service-master")
@Api(value = "施工业务补充", description = "施工业务补充")
public interface HouseWorkerSupAPI {

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
     ServerResponse getShutdownWorkerType(String houseId);
}
