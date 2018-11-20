package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "提供精算数据接口", description = "提供精算数据接口")
public interface ActuaryAPI {

    @PostMapping("/data/actuary/getActuaryWaitPay")
    @ApiOperation(value = "返回待业主支付精算列表", notes = "返回待业主支付精算列表")
    ServerResponse getActuaryWaitPay();

    @PostMapping("/data/actuary/getActuaryCommit")
    @ApiOperation(value = "返回待提交精算列表", notes = "返回待提交精算列表")
    ServerResponse getActuaryCommit();

    @PostMapping("/data/actuary/getActuaryConfirm")
    @ApiOperation(value = "返回待业主确认精算列表", notes = "返回待业主确认精算列表")
    ServerResponse getActuaryConfirm();

    @PostMapping("/data/actuary/getActuaryComplete")
    @ApiOperation(value = "返回已完成精算列表", notes = "返回已完成精算列表")
    ServerResponse getActuaryComplete();

    @PostMapping("/data/actuary/getStatistics")
    @ApiOperation(value = "返回统计列表", notes = "返回统计列表")
    ServerResponse getStatistics();

    @PostMapping("/data/actuary/getStatisticsByDate")
    @ApiOperation(value = "返回按日期统计列表", notes = "返回按日期统计列表")
    ServerResponse getStatisticsByDate(@RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate);
}
