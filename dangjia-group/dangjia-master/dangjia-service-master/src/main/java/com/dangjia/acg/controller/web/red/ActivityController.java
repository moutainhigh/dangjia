package com.dangjia.acg.controller.web.red;

import com.dangjia.acg.api.web.red.ActivityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.service.activity.ActivityService;
import com.dangjia.acg.service.activity.RedPackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * author: qiyuxaing
 */
@RestController
public class ActivityController  implements ActivityAPI {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RedPackService redPackService;

    /**
     * 获取所有活动
     * @param activity
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryActivitys(HttpServletRequest request,PageDTO pageDTO, Activity activity,String isEndTime) {
        return activityService.queryActivitys(request,pageDTO,activity,isEndTime);
    }

    @Override
    @ApiMethod
    public ServerResponse getActivity(HttpServletRequest request, ActivityDTO activityDTO) {
        return activityService.getActivity(request,activityDTO);
    }

    /**
     * 修改
     * @param activity
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editActivity(HttpServletRequest request, Activity activity,String discount) {
        return activityService.editActivity(request,activity,discount);
    }
    /**
     * 新增
     * @param activity
     * @return
     */
    @ApiMethod
    public ServerResponse addActivity(HttpServletRequest request,Activity activity,String discount) {
        return activityService.addActivity(request,activity,discount);
    }


    /**
     * 获取所有优惠券
     * @param activityRedPack
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryActivityRedPacks(HttpServletRequest request, PageDTO pageDTO, ActivityRedPack activityRedPack,String isEndTime) {
        return redPackService.queryActivityRedPacks(request,pageDTO,activityRedPack, isEndTime);
    }
    /**
     * 获取所有有效的优惠券
     * @param cityId
     * @param memberId 指定用户是否已经领取该优惠券
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActivityRedPacks(HttpServletRequest request, PageDTO pageDTO,String cityId,String memberId){
        return redPackService.getActivityRedPacks(request,pageDTO,cityId, memberId);
    }
    @Override
    @ApiMethod
    public ServerResponse getActivityRedPack(HttpServletRequest request, ActivityRedPackDTO activityRedPackDTO) {
        return redPackService.getActivityRedPack(request,activityRedPackDTO);
    }
    /**
     * 修改
     * @param activityRedPack
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editActivityRedPack(HttpServletRequest request, ActivityRedPack activityRedPack,  String ruleNum, String ruleMoney, String ruleSatisfyMoney) {
        int[] nums= new int[0];
        BigDecimal[] moneys= new BigDecimal[0];
        BigDecimal[] satisfyMoneys= new BigDecimal[0];
        if(!CommonUtil.isEmpty(ruleNum)){
            String[] n= StringUtils.split(ruleNum,",");
            String[] m= StringUtils.split(ruleMoney,",");
            String[] s= StringUtils.split(ruleSatisfyMoney,",");
            nums= new int[n.length];
            moneys= new BigDecimal[n.length];
            satisfyMoneys= new BigDecimal[n.length];
            for (int i = 0; i <n.length ; i++) {
                nums[i]=Integer.parseInt(n[i]);
                moneys[i]=new BigDecimal(m[i]);
                satisfyMoneys[i]=new BigDecimal(s[i]);
            }
        }
        return redPackService.editActivityRedPack(request,activityRedPack,nums,moneys,satisfyMoneys);
    }
    /**
     * 新增
     * @param activityRedPack
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addActivityRedPack(HttpServletRequest request,ActivityRedPack activityRedPack, String ruleNum, String ruleMoney, String ruleSatisfyMoney) {
        int[] nums= new int[0];
        BigDecimal[] moneys= new BigDecimal[0];
        BigDecimal[] satisfyMoneys= new BigDecimal[0];
        if(!CommonUtil.isEmpty(ruleNum)){
            String[] n= StringUtils.split(ruleNum,",");
            String[] m= StringUtils.split(ruleMoney,",");
            String[] s= StringUtils.split(ruleSatisfyMoney,",");
            nums= new int[n.length];
            moneys= new BigDecimal[n.length];
            satisfyMoneys= new BigDecimal[n.length];
            for (int i = 0; i <n.length ; i++) {
                nums[i]=Integer.parseInt(n[i]);
                moneys[i]=new BigDecimal(m[i]);
                satisfyMoneys[i]=new BigDecimal(s[i]);
            }
        }
        return redPackService.addActivityRedPack(request,activityRedPack,nums,moneys,satisfyMoneys);
    }

    @Override
    @ApiMethod
    public ServerResponse closeActivity(String id){
        return activityService.closeActivity(id);
    }


    @Override
    @ApiMethod
    public ServerResponse closeActivityRedPack(String id){
        return redPackService.closeActivityRedPack(id);
    }


    @Override
    @ApiMethod
    public ServerResponse closeActivityRedPackRecord(String id){
        return redPackService.closeActivityRedPackRecord(id);
    }
    /**
     * 获取所有优惠券客户未使用记录
     * @return
     */
    @Override
    @ApiMethod
    public List<ActivityRedPackRecord> queryRedPackRecord(){
        return redPackService.queryRedPackRecord();
    }
}
