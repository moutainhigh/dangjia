package com.dangjia.acg.api.sale.achievement;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * 业绩模块 API
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售端业绩接口", description = "销售端业绩接口")
public interface AchievementAPI {


    @PostMapping(value = "sale/achievement/queryLeaderAchievementData")
    @ApiOperation(value = "客户查询业绩", notes = "客户查询业绩")
    ServerResponse queryLeaderAchievementData(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("userToken")String userToken,
                                              @RequestParam("storeId") String storeId,
                                              @RequestParam("time") Date time);


    @PostMapping(value = "sale/achievement/queryUserAchievementData")
    @ApiOperation(value = "查询员工业绩", notes = "查询员工业绩")
    ServerResponse queryUserAchievementData(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("userToken")String userToken,
                                            @RequestParam("visitState") Integer visitState,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("time") Date time);
}
