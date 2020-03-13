package com.dangjia.acg.controller.web.red;

import com.dangjia.acg.api.web.red.ActivityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackInfo;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.service.activity.ActivityService;
import com.dangjia.acg.service.activity.RedPackService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * author: qiyuxaing
 */
@RestController
public class ActivityController  implements ActivityAPI {
    private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
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

    /**
     * 中台--新增优惠卷
     *
     * @param activityRedPackInfo 优惠卷对象
     * @param userId 用户ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addNewActivityRedPack(HttpServletRequest request, ActivityRedPackInfo activityRedPackInfo, String userId){
        try{
            return redPackService.addNewActivityRedPack(activityRedPackInfo,userId);
        }catch (Exception e){
            logger.error("新增失败",e);
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }
    /**
     * 业主端--获取所有有效的优惠券
     *
     * @param cityId
     * @param userToken 指定用户是否已经领取该优惠券
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMyActivityRedList(String userToken,Integer sourceType,String cityId){
        return redPackService.queryMyActivityRedList(userToken,sourceType,cityId);
    }

    /**
     * 业主端--获取用户所有失效的优惠券
     *
     * @param cityId
     * @param userToken 指定用户是否已经领取该优惠券
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMyExpireRedList(String userToken,Integer sourceType,String cityId,PageDTO pageDTO){
        return redPackService.queryMyExpireRedList(userToken,sourceType,cityId,pageDTO);
    }

    /**
     * 优惠券状态修改
     * @param redPackId 优惠券ID
     * @param stateStatus 0继续发放,1停止发放
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateRedPackInfo(String redPackId,Integer stateStatus){
        return redPackService.updateRedPackInfo(redPackId,stateStatus);
    }
    /**
     * 中台--优惠卷详情
     * @param request
     * @param redPackId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getNewActivityRedPackDetail(HttpServletRequest request,String  redPackId){
        return redPackService.getNewActivityRedPackDetail(redPackId);
    }

    /**
     * 中台--优惠卷领取列表
     * @param request
     * @param redPackId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActivityRedPackRecordList(HttpServletRequest request,PageDTO pageDTO,String  redPackId){
        return redPackService.getActivityRedPackRecordList(pageDTO,redPackId);
    }

    /**
     * 中台--查询优惠卷列表
     *
     * @param status 优惠卷状态：1发行中，2暂停发放，3已过期，4发送完毕
     * @param sourceType 发行级别：1城市卷，2店铺卷
     * @param userId 用户Id
     * @param cityId 城市Id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryNewActivityRedList(HttpServletRequest request,PageDTO pageDTO,Integer sourceType,String userId,String cityId,String status){
        return redPackService.queryNewActivityRedList(pageDTO,sourceType,userId,cityId,status);
    }
    /**
     *  * 中台--新增优惠卷--查询类别
     * @param request
     * @param sourceType 发行级别：1城市卷，2店铺卷
     * @param userId 用户Id
     * @param cityId 城市Id
     * @param parentId 父类ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCategoryListByType(HttpServletRequest request,Integer sourceType,String userId,String cityId,String parentId){
        return redPackService.queryCategoryListByType(sourceType,userId,cityId,parentId);
    }


    /**
     * 中台--新增优惠卷--查询货品
     * @param request
     * @param sourceType 发行级别：1城市卷，2店铺卷
     * @param userId 用户Id
     * @param cityId 城市Id
     * @param categoryId 类别ID
     * @param pageDTO 分页
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsByType(HttpServletRequest request,Integer sourceType,String userId,String cityId, String categoryId,PageDTO pageDTO,String searchKey){
        return redPackService.queryGoodsByType(sourceType,userId,cityId,categoryId,pageDTO,searchKey);
    }

    /**
     * 中台--新增优惠卷--查询商品
     * @param request
     * @param sourceType 发行级别：1城市卷，2店铺卷
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param categoryId 类别ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryPrductByType(HttpServletRequest request,PageDTO pageDTO,Integer sourceType,String userId,String cityId,String categoryId,String searchKey){
        return redPackService.queryPrductByType(pageDTO,sourceType,userId,cityId,categoryId,searchKey);
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
     * 提前三天检查将过期的优惠券，发通知给到业主
     */
    @Override
    @ApiMethod
    public void couponActivityRedPack(){
         redPackService.couponActivityRedPack();
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
