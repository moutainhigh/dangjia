package com.dangjia.acg.api.web.activity;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:17
 */
@FeignClient("dangjia-service-master")
@Api(value = "活动配置", description = "活动配置")
public interface DjStoreActivityAPI {

    @PostMapping("web/activity/addActivities")
    @ApiOperation(value = "添加活动", notes = "添加活动")
    ServerResponse addActivities(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity);

    @PostMapping("web/activity/getSession")
    @ApiOperation(value = "场次", notes = "场次")
    ServerResponse getSession(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity);

    @PostMapping("web/activity/queryActivities")
    @ApiOperation(value = "查询活动", notes = "查询活动")
    ServerResponse queryActivities(@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("web/activity/setActivities")
    @ApiOperation(value = "编辑活动", notes = "编辑活动")
    ServerResponse setActivities(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity);

    @PostMapping("web/activity/queryActivitiesById")
    @ApiOperation(value = "查询活动详情", notes = "查询活动详情")
    ServerResponse queryActivitiesById(@RequestParam("id") String id);

    @PostMapping("web/activity/queryActivitiesByStorefront")
    @ApiOperation(value = "查询活动(店参加)", notes = "查询活动(店参加)")
    ServerResponse queryActivitiesByStorefront(@RequestParam("userId") String userId,
                                               @RequestParam("cityId") String cityId,
                                               @RequestParam("activityType") Integer activityType);

    @PostMapping("web/activity/queryActivitiesSessionByStorefront")
    @ApiOperation(value = "查询活动场次(店参加)", notes = "查询活动场次(店参加)")
    ServerResponse queryActivitiesSessionByStorefront(@RequestParam("userId") String userId,
                                                      @RequestParam("cityId") String cityId,
                                                      @RequestParam("id") String id);

    @PostMapping("web/activity/setStoreParticipateActivities")
    @ApiOperation(value = "店铺参加活动", notes = "店铺参加活动")
    ServerResponse setStoreParticipateActivities(@RequestParam("userId") String userId,
                                                 @RequestParam("cityId") String cityId,
                                                 @RequestParam("storeActivityId") String storeActivityId,
                                                 @RequestParam("activitySessionId") String activitySessionId,
                                                 @RequestParam("activityType") Integer activityType);

    @PostMapping("web/activity/queryWaitingSelectionProduct")
    @ApiOperation(value = "店铺活动商品待选列表", notes = "店铺活动商品待选列表")
    ServerResponse queryWaitingSelectionProduct(@RequestParam("userId") String userId,
                                                @RequestParam("cityId") String cityId,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("storeActivityId") String storeActivityId,
                                                @RequestParam("activitySessionId") String activitySessionId);

    @PostMapping("web/activity/querySelectedWaitingSelectionCount")
    @ApiOperation(value = "已选/待选 数量", notes = "已选/待选 数量")
    ServerResponse querySelectedWaitingSelectionCount(@RequestParam("userId") String userId,
                                                      @RequestParam("cityId") String cityId,
                                                      @RequestParam("storeActivityId") String storeActivityId,
                                                      @RequestParam("activitySessionId") String activitySessionId);
}
