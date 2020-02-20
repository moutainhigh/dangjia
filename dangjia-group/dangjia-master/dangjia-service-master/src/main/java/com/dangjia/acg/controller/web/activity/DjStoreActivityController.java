package com.dangjia.acg.controller.web.activity;

import com.dangjia.acg.api.web.activity.DjStoreActivityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.service.activity.DjStoreActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:21
 */
@RestController
public class DjStoreActivityController implements DjStoreActivityAPI {

    @Autowired
    private DjStoreActivityService djStoreActivityService;


    @Override
    @ApiMethod
    public ServerResponse addActivities(DjStoreActivity djStoreActivity) {
        try {
            return djStoreActivityService.addActivities(djStoreActivity);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("配置失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse getSession(DjStoreActivity djStoreActivity) {
        return djStoreActivityService.getSession(djStoreActivity);
    }

    @Override
    @ApiMethod
    public ServerResponse queryActivities(PageDTO pageDTO) {
        return djStoreActivityService.queryActivities(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse setActivities(DjStoreActivity djStoreActivity) {
        try {
            return djStoreActivityService.setActivities(djStoreActivity);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse queryActivitiesById(String id) {
        return djStoreActivityService.queryActivitiesById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryActivitiesByStorefront(String userId, String cityId, Integer activityType) {
        return djStoreActivityService.queryActivitiesByStorefront(userId,cityId,activityType);
    }

    @Override
    @ApiMethod
    public ServerResponse queryActivitiesSessionByStorefront(String userId, String cityId, String id) {
        return djStoreActivityService.queryActivitiesSessionByStorefront(userId,cityId,id);
    }

    @Override
    @ApiMethod
    public ServerResponse setStoreParticipateActivities(String userId, String cityId, String storeActivityId, String activitySessionId, Integer activityType) {
        return djStoreActivityService.setStoreParticipateActivities(userId, cityId, storeActivityId, activitySessionId, activityType);
    }

    @Override
    @ApiMethod
    public ServerResponse queryWaitingSelectionProduct(String userId, String cityId, PageDTO pageDTO, String storeActivityId, String activitySessionId) {
        return djStoreActivityService.queryWaitingSelectionProduct(userId,cityId,pageDTO,storeActivityId,activitySessionId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySelectedWaitingSelectionCount(String userId, String cityId, String storeActivityId, String activitySessionId) {
        return djStoreActivityService.querySelectedWaitingSelectionCount(userId,cityId,storeActivityId,activitySessionId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySelectedProduct(String userId, String cityId, PageDTO pageDTO,String storeActivityId, String activitySessionId) {
        return djStoreActivityService.querySelectedProduct(userId,cityId,pageDTO,storeActivityId,activitySessionId);
    }

    @Override
    @ApiMethod
    public ServerResponse setSelectActiveProduct(String userId, String cityId, String storeActivityId, String activitySessionId, String productId, Integer activityType, String storeParticipateActivitiesId) {
        return djStoreActivityService.setSelectActiveProduct(userId,cityId,storeActivityId,activitySessionId,productId,activityType,storeParticipateActivitiesId);
    }

    @Override
    @ApiMethod
    public ServerResponse setCommit(String jsonStr, String storeParticipateActivitiesId) {
        try {
            return djStoreActivityService.setCommit(jsonStr,storeParticipateActivitiesId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse queryAuditstoresParticipateActivities(PageDTO pageDTO) {
        return djStoreActivityService.queryAuditstoresParticipateActivities(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryParticipatingShopsList(PageDTO pageDTO, Integer activityType,
                                                      String id, String activitySessionId) {
        return djStoreActivityService.queryParticipatingShopsList(pageDTO,activityType,id, activitySessionId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryBillGoods(PageDTO pageDTO, String id) {
        return djStoreActivityService.queryBillGoods(pageDTO,id);
    }

    @Override
    @ApiMethod
    public ServerResponse setBillGoods(String id, Integer registrationStatus, String backReason) {
        return djStoreActivityService.setBillGoods(id,registrationStatus,backReason);
    }
}
