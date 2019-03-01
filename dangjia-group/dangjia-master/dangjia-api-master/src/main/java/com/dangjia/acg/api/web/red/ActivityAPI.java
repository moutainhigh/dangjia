package com.dangjia.acg.api.web.red;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/3 0003
 * Time: 16:30
 * web端用户接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "活动优惠券操作接口", description = "活动优惠券操作接口")
public interface ActivityAPI {


    @PostMapping("web/activity/list")
    @ApiOperation(value = "获取活动列表", notes = "获取活动列表")
    ServerResponse queryActivitys(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("activity") Activity activity,
                                  @RequestParam("isEndTime") String isEndTime);

    @PostMapping("web/activity/get")
    @ApiOperation(value = "获取活动明细", notes = "获取活动明细")
    ServerResponse getActivity(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("activity") ActivityDTO activityDTO);

    /**
     * 修改
     *
     * @param activity
     * @return
     */
    @PostMapping("web/activity/edit")
    @ApiOperation(value = "编辑活动", notes = "编辑活动")
    ServerResponse editActivity(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("activity") Activity activity,
                                @RequestParam("discount") String discount);

    /**
     * 新增
     *
     * @param activity
     * @return
     */
    @PostMapping("web/activity/add")
    @ApiOperation(value = "新增活动", notes = "新增活动")
    ServerResponse addActivity(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("activity") Activity activity,
                               @RequestParam("discount") String discount);


    /**
     * 获取所有优惠券
     *
     * @param activityRedPack
     * @return
     */
    @PostMapping("web/activity/red/list")
    @ApiOperation(value = "获取所有优惠券列表", notes = "获取所有优惠券列表")
    ServerResponse queryActivityRedPacks(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                         @RequestParam("activityRedPack") ActivityRedPack activityRedPack,
                                         @RequestParam("isEndTime") String isEndTime);

    /**
     * 获取所有有效的优惠券
     *
     * @param cityId
     * @param memberId 指定用户是否已经领取该优惠券
     * @return
     */
    @PostMapping("web/activity/red/myList")
    @ApiOperation(value = "获取所有有效的优惠券", notes = "获取所有有效的优惠券")
    ServerResponse getActivityRedPacks(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("cityId") String cityId,
                                       @RequestParam("memberId") String memberId);

    @PostMapping("web/activity/red/get")
    @ApiOperation(value = "获取优惠券明细", notes = "获取优惠券明细")
    ServerResponse getActivityRedPack(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("activityRedPackDTO") ActivityRedPackDTO activityRedPackDTO);

    /**
     * 修改
     *
     * @param activityRedPack
     * @return
     */
    @PostMapping("web/activity/red/edit")
    @ApiOperation(value = "修改优惠券", notes = "修改优惠券")
    ServerResponse editActivityRedPack(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("activityRedPack") ActivityRedPack activityRedPack,
                                       @RequestParam("ruleNum") String ruleNum,
                                       @RequestParam("ruleMoney") String ruleMoney,
                                       @RequestParam("ruleSatisfyMoney") String ruleSatisfyMoney);

    /**
     * 新增
     *
     * @param activityRedPack
     * @return
     */
    @PostMapping("web/activity/red/add")
    @ApiOperation(value = "新增优惠券", notes = "新增优惠券")
    ServerResponse addActivityRedPack(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("activityRedPack") ActivityRedPack activityRedPack,
                                      @RequestParam("ruleNum") String ruleNum,
                                      @RequestParam("ruleMoney") String ruleMoney,
                                      @RequestParam("ruleSatisfyMoney") String ruleSatisfyMoney);

    @PostMapping("web/activity/close")
    @ApiOperation(value = "活动设置为关闭", notes = "活动设置为关闭")
    ServerResponse closeActivity(@RequestParam("id") String id);

    @PostMapping("web/activity/red/close")
    @ApiOperation(value = "优惠券设置为关闭", notes = "优惠券设置为关闭")
    ServerResponse closeActivityRedPack(@RequestParam("id") String id);

    @PostMapping("web/activity/redRecord/close")
    @ApiOperation(value = "会员优惠券设置为过期", notes = "会员优惠券设置为过期")
    ServerResponse closeActivityRedPackRecord(@RequestParam("id") String id);

    @PostMapping("web/activity/redRecord/all")
    @ApiOperation(value = "获取所有优惠券客户未使用记录", notes = "获取所有优惠券客户未使用记录")
    List<ActivityRedPackRecord> queryRedPackRecord();
}
