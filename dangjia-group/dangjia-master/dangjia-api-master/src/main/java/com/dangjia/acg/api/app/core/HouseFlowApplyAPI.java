package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:15
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主验收审核", description = "业主验收审核")
public interface HouseFlowApplyAPI {


    @PostMapping("app/core/houseFlowApply/supCouponApply")
    @ApiOperation(value = "自动审核到时管家未审核申请", notes = "自动审核到时管家未审核申请")
    void supCouponApply();

    @PostMapping("app/core/houseFlowApply/couponApply")
    @ApiOperation(value = "自动审核到时业主未审核申请", notes = "自动审核到时业主未审核申请")
    void couponApply();

    @PostMapping("app/core/houseFlowApply/houseRecord")
    @ApiOperation(value = "工匠端工地记录", notes = "工匠端工地记录")
    ServerResponse houseRecord(@RequestParam("userToken") String userToken,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("app/core/houseFlowApply/checkWorker")
    @ApiOperation(value = "每日申请直接审核", notes = "每日申请直接审核")
    ServerResponse checkWorker(@RequestParam("userToken") String userToken,
                               @RequestParam("houseFlowApplyId") String houseFlowApplyId);

    @PostMapping("app/core/houseFlowApply/absenteeism")
    @ApiOperation(value = "自动检测今日旷工的人工，并扣钱", notes = "自动检测今日旷工的人工，并扣钱")
    void absenteeism();

    @PostMapping("app/core/houseFlowApply/checkDetail")
    @ApiOperation(value = "业主验收详情", notes = "业主验收详情")
    ServerResponse checkDetail(@RequestParam("userToken") String userToken,
                               @RequestParam("houseFlowApplyId") String houseFlowApplyId);

    @PostMapping("app/core/houseFlowApply/stewardCheckDetail")
    @ApiOperation(value = "管家验收详情", notes = "管家验收详情")
    ServerResponse stewardCheckDetail(@RequestParam("userToken") String userToken,
                                      @RequestParam("houseFlowApplyId") String houseFlowApplyId);
}
