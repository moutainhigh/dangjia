package com.dangjia.acg.controller.web.red;

import com.dangjia.acg.api.web.red.ActivityParticipantAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.ActivityParticipant;
import com.dangjia.acg.service.activity.ActivityParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * author: qiyuxaing
 */
@RestController
public class ActivityParticipantController implements ActivityParticipantAPI {

    @Autowired
    private ActivityParticipantService activityParticipantService;

    /**
     * 报名
     * @param activityParticipant
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addParticipant(HttpServletRequest request, String userToken, ActivityParticipant activityParticipant){
        return activityParticipantService.addParticipant(request,userToken,activityParticipant);
    }
    /**
     * 添加排除用户
     * @param activityParticipant
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addCleanParticipant(HttpServletRequest request, ActivityParticipant activityParticipant){
        return activityParticipantService.addCleanParticipant(request,activityParticipant);
    }
    /**
     * 清空
     * @param activityParticipant
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse cleanParticipant(HttpServletRequest request, ActivityParticipant activityParticipant){
        return activityParticipantService.cleanParticipant(request,activityParticipant);
    }
    /**
     * 修改
     * @param activityParticipant
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editParticipant(HttpServletRequest request, ActivityParticipant activityParticipant){
        return activityParticipantService.editParticipant(request,activityParticipant);
    }

    /**
     * 获取所有活动
     * @param activityParticipant
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryParticipant(HttpServletRequest request, PageDTO pageDTO, ActivityParticipant activityParticipant, Date startTime, Date endTime){
        return activityParticipantService.queryParticipant(request,pageDTO,activityParticipant,startTime,endTime);
    }

    @Override
    @ApiMethod
    public ServerResponse getParticipant(HttpServletRequest request,String userToken){
        return activityParticipantService.getParticipant(request,userToken);
    }
}
