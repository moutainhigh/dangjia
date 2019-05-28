package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:15
 */
@FeignClient("dangjia-service-master")
@Api(value = "工地工程工期", description = "工地工程工期")
public interface HouseFlowScheduleAPI {


    /**
     * 工程日历工序列表
     * @param houseId 房子ID
     * @return
     */
    @PostMapping("app/core/schedule/getHouseFlows")
    @ApiOperation(value = "工程日历工序列表", notes = "工程日历工序列表")
    ServerResponse getHouseFlows(@RequestParam("houseId") String houseId);


    /**
     * 设置指定工序的工期
     * @param houseFlowId 工序ID
     * @param startDate 工期开始时间
     * @param endDate   工期结束时间
     * @return
     */
    @PostMapping("app/core/schedule/setHouseFlowSchedule")
    @ApiOperation(value = "设置指定工序的工期", notes = "设置指定工序的工期")
    ServerResponse   setHouseFlowSchedule(@RequestParam("houseFlowId") String  houseFlowId,
                                          @RequestParam("startDate") Date startDate,
                                          @RequestParam("endDate") Date endDate);

    /**
     * 延长或提前工序的工期
     * @param houseFlowId 工序ID
     * @param extend 延长天数 两者只能其一
     * @param advance 提前天数 两者只能其一
     * @return
     */
    @PostMapping("app/core/schedule/updateFlowSchedule")
    @ApiOperation(value = "延长或提前工序的工期", notes = "延长或提前工序的工期")
    ServerResponse  updateFlowSchedule(@RequestParam("houseFlowId") String  houseFlowId,
                                       @RequestParam("extend") Integer extend,
                                       @RequestParam("advance") Integer advance);

    /**
     * 生成工程日历
     * @param houseId 房子ID
     * @return
     */
    @PostMapping("app/core/schedule/makeCalendar")
    @ApiOperation(value = "生成工程日历", notes = "生成工程日历")
    ServerResponse  makeCalendar(@RequestParam("houseId") String houseId);


    @PostMapping("app/core/schedule/viewCalendar")
    @ApiOperation(value = "查看日历", notes = "查看日历")
    ServerResponse viewCalendar(@RequestParam("houseId") String houseId,@RequestParam("day") Date day);
}
