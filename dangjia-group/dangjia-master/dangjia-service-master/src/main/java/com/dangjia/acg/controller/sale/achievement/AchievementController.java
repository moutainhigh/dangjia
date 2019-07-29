package com.dangjia.acg.controller.sale.achievement;

import com.dangjia.acg.api.sale.achievement.AchievementAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sale.achievement.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param request
     * @param storeId
     * @param userId
     * @param time
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryLeaderAchievementData(HttpServletRequest request, String storeId , String userId, Date time) {
        return achievementService.queryLeaderAchievementData(storeId,userId,time);
    }


    @Override
    @ApiMethod
    public ServerResponse queryUserAchievementData(HttpServletRequest request, Integer visitState , String userId, Date time) {
        return achievementService.queryUserAchievementData(visitState,userId,time);
    }

}
