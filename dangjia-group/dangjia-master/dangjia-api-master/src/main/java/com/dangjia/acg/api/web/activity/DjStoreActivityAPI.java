package com.dangjia.acg.api.web.activity;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.activity.DjStoreActivityExplain;
import com.dangjia.acg.modle.activity.DjStoreActivityFractionRate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
    ServerResponse addActivities(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity,
                                 @RequestParam("explains")String explains,
                                 @RequestParam("fractionRates") String fractionRates);

    @PostMapping("web/activity/getSession")
    @ApiOperation(value = "场次", notes = "场次")
    ServerResponse getSession(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity);

    @PostMapping("web/activity/queryActivities")
    @ApiOperation(value = "查询活动", notes = "查询活动")
    ServerResponse queryActivities(@RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("activityType") Integer activityType);

    @PostMapping("web/activity/setActivities")
    @ApiOperation(value = "编辑活动", notes = "编辑活动")
    ServerResponse setActivities(@RequestParam("djStoreActivity") DjStoreActivity djStoreActivity);

    @PostMapping("web/activity/queryActivitiesById")
    @ApiOperation(value = "查询活动详情", notes = "查询活动详情")
    ServerResponse queryActivitiesById(@RequestParam("id") String id);

    @PostMapping("web/activity/queryActivitiesOrSessionById")
    @ApiOperation(value = "查询活动/场次详情", notes = "查询活动/场次详情")
    ServerResponse queryActivitiesOrSessionById(@RequestParam("id") String id,
                                                @RequestParam("activityType") Integer activityType);

    @PostMapping("web/activity/queryActivitiesByStorefront")
    @ApiOperation(value = "查询活动(店铺参加)", notes = "查询活动(店参加)")
    ServerResponse queryActivitiesByStorefront(@RequestParam("userId") String userId,
                                               @RequestParam("cityId") String cityId,
                                               @RequestParam("activityType") Integer activityType);

    @PostMapping("web/activity/queryActivitiesSessionByStorefront")
    @ApiOperation(value = "查询活动场次(店铺参加)", notes = "查询活动场次(店参加)")
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

    @PostMapping("web/activity/querySelectedProduct")
    @ApiOperation(value = "店铺活动商品已选列表", notes = "店铺活动商品已选列表")
    ServerResponse querySelectedProduct(@RequestParam("userId") String userId,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("storeActivityId") String storeActivityId,
                                        @RequestParam("activitySessionId") String activitySessionId);

    @PostMapping("web/activity/deleteSelectedProduct")
    @ApiOperation(value = "删除店铺活动商品已选列表", notes = "删除店铺活动商品已选列表")
    ServerResponse deleteSelectedProduct(@RequestParam("id") String id);

    @PostMapping("web/activity/setSelectActiveProduct")
    @ApiOperation(value = "店铺选择活动商品", notes = "店铺选择活动商品")
    ServerResponse setSelectActiveProduct(@RequestParam("userId") String userId,
                                          @RequestParam("cityId") String cityId,
                                          @RequestParam("storeActivityId") String storeActivityId,
                                          @RequestParam("activitySessionId") String activitySessionId,
                                          @RequestParam("productId") String productId,
                                          @RequestParam("activityType") Integer activityType,
                                          @RequestParam("storeParticipateActivitiesId") String storeParticipateActivitiesId);

    @PostMapping("web/activity/setCommit")
    @ApiOperation(value = "提交", notes = "提交")
    ServerResponse setCommit(@RequestParam("jsonStr") String jsonStr,
                             @RequestParam("storeParticipateActivitiesId") String storeParticipateActivitiesId);

    @PostMapping("web/activity/queryAuditstoresParticipateActivities")
    @ApiOperation(value = "审核店铺参与活动列表", notes = "审核店铺参与活动列表")
    ServerResponse queryAuditstoresParticipateActivities(@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("web/activity/queryParticipatingShopsList")
    @ApiOperation(value = "参与活动店铺列表", notes = "参与活动店铺列表")
    ServerResponse queryParticipatingShopsList(@RequestParam("pageDTO") PageDTO pageDTO,
                                               @RequestParam("activityType") Integer activityType,
                                               @RequestParam("storeActivityId") String storeActivityId,
                                               @RequestParam("activitySessionId") String activitySessionId);

    @PostMapping("web/activity/queryBillGoods")
    @ApiOperation(value = "货品清单", notes = "货品清单")
    ServerResponse queryBillGoods(@RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("id") String id);

    @PostMapping("web/activity/setBillGoods")
    @ApiOperation(value = "审核货品清单", notes = "审核货品清单")
    ServerResponse setBillGoods(@RequestParam("id") String id,
                                @RequestParam("registrationStatus") Integer registrationStatus,
                                @RequestParam("backReason") String backReason);

    @PostMapping("app/activity/queryHomeGroupActivities")
    @ApiOperation(value = "首页拼团活动", notes = "首页拼团活动")
    ServerResponse queryHomeGroupActivities(@RequestParam("limit") Integer limit);

    @PostMapping("app/activity/queryHomeLimitedPurchaseActivities")
    @ApiOperation(value = "首页限时购活动", notes = "首页限时购活动")
    ServerResponse queryHomeLimitedPurchaseActivities(@RequestParam("limit") Integer limit);

    @PostMapping("app/activity/queryBuyMoreLimitedTime")
    @ApiOperation(value = "限时购(更多)", notes = "限时购(更多)")
    ServerResponse queryBuyMoreLimitedTime(@RequestParam("id") String id);

    @PostMapping("app/activity/setwithdraw")
    @ApiOperation(value = "撤回", notes = "撤回")
    ServerResponse setwithdraw(@RequestParam("storeParticipateActivitiesId") String storeParticipateActivitiesId);

    @PostMapping("app/activity/querySpellGroupList")
    @ApiOperation(value = "拼团列表", notes = "拼团列表")
    ServerResponse querySpellGroupList(@RequestParam("storeActivityProductId") String storeActivityProductId);

    @PostMapping("app/activity/setSpellGroup")
    @ApiOperation(value = "参与拼团(未满/已满)", notes = "参与拼团(未满/已满)")
    ServerResponse setSpellGroup(@RequestParam("orderId") String orderId);

    @PostMapping("app/activity/queryActivityPurchaseRotation")
    @ApiOperation(value = "查询限时购/拼团购轮播", notes = "查询限时购/拼团购轮播")
    ServerResponse queryActivityPurchaseRotation(@RequestParam("storeActivityProductId") String storeActivityProductId,
                                                 @RequestParam("activityType") Integer activityType);

    @PostMapping("app/activity/checkGroupPurchaseOrder")
    @ApiOperation(value = "检测拼团购是否拼团失败", notes = "检测拼团购是否拼团失败")
    void checkGroupPurchaseOrder();


    @PostMapping("web/activity/participates/query")
    @ApiOperation(value = "参与集福列表", notes = "参与集福列表")
    ServerResponse queryStoreActivityParticipatesList(@RequestParam("pageDTO") PageDTO pageDTO,  @RequestParam("storeActivityId") String storeActivityId);

    @PostMapping("web/activity/support/query")
    @ApiOperation(value = "参与集福助力列表", notes = "参与集福助力列表")
    ServerResponse queryStoreActivitySupportList(@RequestParam("pageDTO") PageDTO pageDTO,  @RequestParam("participateId") String participateId);

}
