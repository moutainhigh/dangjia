package com.dangjia.acg.controller.sale.achievement;

import com.dangjia.acg.api.sale.achievement.AchievementAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sale.achievement.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * 业绩模块
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@RestController
public class AchievementController implements AchievementAPI {

    @Autowired
    private AchievementService achievementService;

    /**
     * 根据月份 查询店长业绩
     *
     * @param request
     * @param storeId
     * @param time
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryLeaderAchievementData(HttpServletRequest request, String userToken,
                                                     String storeId, Date time) {
        return achievementService.queryLeaderAchievementData(userToken, storeId, time);
    }


    @Override
    @ApiMethod
    public ServerResponse queryUserAchievementData(HttpServletRequest request,String userToken, Integer visitState, String userId, Date time,String villageId,String building) {
        return achievementService.queryUserAchievementData(userToken,visitState, userId, time,villageId,building);
    }

    @Override
    @ApiMethod
    public ServerResponse volume(HttpServletRequest request, String userToken, Integer visitState, String userId, Date time,String building,String villageId) {
        return achievementService.volume(userToken,visitState,userId,time,building,villageId);
    }

    @Override
    @ApiMethod
    public ServerResponse performanceQueryConditions(HttpServletRequest request, String userToken, String userId) {
        return achievementService.performanceQueryConditions(userToken,userId);
    }

}
