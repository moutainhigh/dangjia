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

//    @PostMapping("app/supervisor/addDjBasicsPatrolRecord")
//    @ApiOperation(value = "新建巡检", notes = "新建巡检")
//    ServerResponse addDjBasicsPatrolRecord(@RequestParam("request") HttpServletRequest request,
//            @RequestParam("userToken") String userToken,
//            @RequestParam("houseId") String houseId,
//            @RequestParam("images") String images,
//            @RequestParam("content") String content
//    );

}
