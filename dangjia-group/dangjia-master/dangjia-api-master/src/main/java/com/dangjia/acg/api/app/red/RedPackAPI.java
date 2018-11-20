package com.dangjia.acg.api.app.red;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 */
@FeignClient("dangjia-service-master")
@Api(value = "优惠券业务接口", description = "优惠券业务接口")
public interface RedPackAPI {


    @PostMapping("app/pay/red/discountPage")
    @ApiOperation(value = "选择可用优惠券数据", notes = "选择可用优惠券数据")
     ServerResponse discountPage(@RequestParam("request") HttpServletRequest request, @RequestParam("businessOrderNumber") String businessOrderNumber);

    @PostMapping("app/pay/red/submitDiscounts")
    @ApiOperation(value = "提交选择的优惠券，并更新订单金额", notes = "提交选择的优惠券，并更新订单金额")
     ServerResponse submitDiscounts(@RequestParam("request") HttpServletRequest request, @RequestParam("businessOrderNumber") String businessOrderNumber, @RequestParam("redPacketRecordIds") String redPacketRecordIds);
    /**
     * 获取当前优惠券客户使用记录总数目
     * @param activityRedPackRecord
     * @return
     */
    @PostMapping("app/member/red/count")
    @ApiOperation(value = "获取当前优惠券客户使用记录总数目", notes = "获取当前优惠券客户使用记录总数目")
     ServerResponse queryRedPackRecordCount(HttpServletRequest request,ActivityRedPackRecord activityRedPackRecord);
    /**
     * 获取当前优惠券客户使用记录
     * @param activityRedPackRecord
     * @return
     */
    @PostMapping("app/member/red/list")
    @ApiOperation(value = "获取当前优惠券客户使用记录", notes = "获取当前优惠券客户使用记录")
     ServerResponse queryActivityRedPackRecords(@RequestParam("request") HttpServletRequest request,@RequestParam("activityRedPackRecord") ActivityRedPackRecord activityRedPackRecord);

    /**
     * 多用户推送优惠券
     * @param phones 手机号,数组字符串，以逗号分隔
     * @param redPackId 优惠券主表ID
     * @param redPackRuleIds 优惠券规则ID,数组字符串，以逗号分隔
     * @return
     */
    @PostMapping("app/member/red/send")
    @ApiOperation(value = "多用户推送优惠券", notes = "多用户推送优惠券")
    ServerResponse sendMemberPadPackBatch(@RequestParam("phones") String phones,@RequestParam("redPackId") String redPackId,@RequestParam("redPackRuleIds") String redPackRuleIds);
}
