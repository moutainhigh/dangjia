package com.dangjia.acg.controller.app.red;

import com.dangjia.acg.api.app.red.RedPackAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.service.activity.ActivityService;
import com.dangjia.acg.service.activity.RedPackPayService;
import com.dangjia.acg.service.activity.RedPackService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxaing
 */
@RestController
public class RedPackController  implements RedPackAPI {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RedPackService redPackService;

    @Autowired
    private RedPackPayService redPackPayService;
    /**
     * 可用优惠券数据
     * @param request
     * @param businessOrderNumber 订单号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse discountPage(HttpServletRequest request, String businessOrderNumber){
        return redPackPayService.discountPage(request,businessOrderNumber);
    }
    /**
     * 确定选择
     * @param request
     * @param businessOrderNumber 订单号
     * @param redPacketRecordIds 默认选中的用户优惠券ID，多个以逗号分隔
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse submitDiscounts(HttpServletRequest request, String businessOrderNumber, String redPacketRecordIds){
        return redPackPayService.submitDiscounts(request,businessOrderNumber,redPacketRecordIds);
    }

    /**
     * 获取当前优惠券客户使用记录
     * @param activityRedPackRecord
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryActivityRedPackRecords(HttpServletRequest request, ActivityRedPackRecord activityRedPackRecord, PageDTO pageDTO){
        PageInfo pageResult =redPackService.queryActivityRedPackRecords(request,activityRedPackRecord,pageDTO);
        return ServerResponse.createBySuccess("ok",pageResult);
    }

    /**
     * 获取当前优惠券客户使用记录总数目
     * @param activityRedPackRecord
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRedPackRecordCount(HttpServletRequest request,ActivityRedPackRecord activityRedPackRecord){
        return redPackService.queryRedPackRecordCount(request,activityRedPackRecord);
    }
    /**
     * 多用户推送优惠券
     * @param phones 手机号,数组字符串，以逗号分隔
     * @param redPackId 优惠券主表ID
     * @param redPackRuleIds 优惠券规则ID,数组字符串，以逗号分隔
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse sendMemberPadPackBatch(String phones,String redPackId,String redPackRuleIds){
        return redPackService.sendMemberPadPackBatch(phones,redPackId,redPackRuleIds);
    }
}
