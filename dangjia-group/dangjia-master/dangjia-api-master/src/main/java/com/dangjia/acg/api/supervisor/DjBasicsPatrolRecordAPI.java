package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 巡查记录管理接口
 * author:chenyufeng
 * time:2019.12.11
 */
@Api(description = "巡查记录管理接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsPatrolRecordAPI {

    @PostMapping("app/supervisor/addDjBasicsPatrolRecord")
    @ApiOperation(value = "新建巡检", notes = "新建巡检")
    ServerResponse addDjBasicsPatrolRecord(@RequestParam("request") HttpServletRequest request,
            @RequestParam("userToken") String userToken,
            @RequestParam("houseId") String houseId,
            @RequestParam("images") String images,
            @RequestParam("content") String content
    );


    @PostMapping("app/supervisor/queryDjBasicsPatrolRecord")
    @ApiOperation(value = "查询巡检记录", notes = "查询巡检记录")
    ServerResponse queryDjBasicsPatrolRecord(@RequestParam("request") HttpServletRequest request,@RequestParam("userToken") String userToken );


    @PostMapping("web/supervisor/queryWorkerRewardPunishRecord")
    @ApiOperation(value = "查询督导工作记录", notes = "查询督导工作记录")
    ServerResponse queryWorkerRewardPunishRecord(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("type") String type, @RequestParam("keyWord") String keyWord );


    @PostMapping("web/supervisor/queryPatrolRecordDetail")
    @ApiOperation(value = "巡检详情", notes = "巡检详情")
    ServerResponse queryPatrolRecordDetail(@RequestParam("request") HttpServletRequest request,@RequestParam("rewordPunishCorrelationId") String rewordPunishCorrelationId );


    @PostMapping("web/supervisor/queryRewardPunishRecordDetail")
    @ApiOperation(value = "奖励/惩罚详情", notes = "奖励/惩罚详情")
    ServerResponse queryRewardPunishRecordDetail(@RequestParam("request") HttpServletRequest request,@RequestParam("id") String id );


    @PostMapping("web/supervisor/getSupHomePage")
    @ApiOperation(value = "督导首页", notes = "督导首页")
    ServerResponse getSupHomePage(@RequestParam("request") HttpServletRequest request);



}
