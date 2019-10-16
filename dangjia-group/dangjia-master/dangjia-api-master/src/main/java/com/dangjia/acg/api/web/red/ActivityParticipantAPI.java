package com.dangjia.acg.api.web.red;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityParticipant;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * author: qiyuxiang
 * web端用户接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "新用户注册领取礼品活动", description = "新用户注册领取礼品活动")
public interface ActivityParticipantAPI {


    @PostMapping("app/activity/participant/add")
    @ApiOperation(value = "报名", notes = "报名")
    ServerResponse addParticipant(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("activityParticipant") ActivityParticipant activityParticipant);


    @PostMapping("app/activity/participant/get")
    @ApiOperation(value = "根据当前用户，检查是否已经领取", notes = "根据当前用户，检查是否已经领取")
    ServerResponse getParticipant(@RequestParam("request")HttpServletRequest request,@RequestParam("userToken")String userToken);

    @PostMapping("web/activity/participant/exclude")
    @ApiOperation(value = "排除用户", notes = "排除用户")
    ServerResponse addCleanParticipant(@RequestParam("request") HttpServletRequest request, @RequestParam("activityParticipant") ActivityParticipant activityParticipant);


    @PostMapping("web/activity/participant/clean")
    @ApiOperation(value = "清空报名用户", notes = "清空报名用户")
    ServerResponse cleanParticipant(@RequestParam("request") HttpServletRequest request, @RequestParam("activityParticipant") ActivityParticipant activityParticipant);

    @PostMapping("web/activity/participant/edit")
    @ApiOperation(value = "中台设置领取状态", notes = "中台设置领取状态")
    ServerResponse editParticipant(@RequestParam("request") HttpServletRequest request, @RequestParam("activityParticipant") ActivityParticipant activityParticipant);


    @PostMapping("web/activity/participant/query")
    @ApiOperation(value = "新用户报名列表", notes = "新用户报名列表")
    ServerResponse queryParticipant(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("pageDTO")PageDTO pageDTO,
                                    @RequestParam("activityParticipant")ActivityParticipant activityParticipant,
                                    @RequestParam("startTime")Date startTime,
                                    @RequestParam("endTime")Date endTime);

}
