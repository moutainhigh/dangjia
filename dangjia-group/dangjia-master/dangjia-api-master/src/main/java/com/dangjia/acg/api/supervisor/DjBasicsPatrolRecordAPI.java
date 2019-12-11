package com.dangjia.acg.api.supervisor;

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


    //首页-督导
    //工地列表
    //工地详情
}
