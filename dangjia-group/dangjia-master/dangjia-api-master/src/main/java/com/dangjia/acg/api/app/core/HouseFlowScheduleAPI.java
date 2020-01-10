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
     *
     * @param houseId 房子ID
     * @return
     */
    @PostMapping("app/core/schedule/getHouseFlows")
    @ApiOperation(value = "工程日历工序列表", notes = "工程日历工序列表")
    ServerResponse getHouseFlows(@RequestParam("houseId") String houseId);

    /**
     * 生成工程日历
     *
     * @param houseId 房子ID
     * @param constructionDate 开工日期
     * @return
     */
    @PostMapping("app/core/schedule/makeCalendar")
    @ApiOperation(value = "生成工程日历", notes = "生成工程日历")
    ServerResponse makeCalendar(@RequestParam("constructionDate") Date constructionDate,@RequestParam("isWeekend") Boolean isWeekend,@RequestParam("houseId") String houseId);


    @PostMapping("app/core/schedule/viewCalendar")
    @ApiOperation(value = "查看日历", notes = "查看日历")
    ServerResponse viewCalendar(@RequestParam("houseId") String houseId, @RequestParam("day") Date day);
}
