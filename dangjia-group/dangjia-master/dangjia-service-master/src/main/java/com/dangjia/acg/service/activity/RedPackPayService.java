package com.dangjia.acg.service.activity;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.dto.activity.RedPageResult;
import com.dangjia.acg.mapper.activity.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.pay.BusinessOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * 优惠券管理
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class RedPackPayService {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private RedPackService redPackService;
    @Autowired
    private IActivityMapper activityMapper;
    @Autowired
    private IActivityDiscountMapper activityDiscountMapper;
    @Autowired
    private IActivityRedPackMapper activityRedPackMapper;
    @Autowired
    private IActivityRedPackRecordMapper activityRedPackRecordMapper;
    @Autowired
    private IActivityRedPackRuleMapper activityRedPackRuleMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private BudgetMaterialAPI budgetMaterialAPI;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseMapper houseMapper;

    /**
     * 可用优惠券数据
     * @param request
     * @param phones 手机号
     * @param activityType 活动类型  0为直推  1为注册 2为邀请
     * @return
     */
    public void checkUpActivity(HttpServletRequest request,String phones,String activityType){
        String cityId = request.getParameter(Constants.CITY_ID);
        if(CommonUtil.isEmpty(cityId)) {
            cityId = (String) request.getAttribute(Constants.CITY_ID);
        }
        if(!CommonUtil.isEmpty(cityId)) {
            //检查是否存在有效活动
            ActivityDTO activity = validActivity(request,cityId, activityType);
            //检查优惠券是否有效
            if(activity!=null&&activity.getDiscounts()!=null&&activity.getDiscounts().size()>0){
                List<ActivityRedPackDTO> discounts=activity.getDiscounts();
                for (ActivityRedPackDTO red:discounts) {
                    //判断优惠券是否过期，或优惠券未关闭
                    if(red.getDeleteState()==0&&red.getEndDate().getTime()>new Date().getTime()){
                        List<String> redPackRuleIds=new ArrayList<>();
                        for (ActivityRedPackRule rule:red.getRedPackRule()) {
                            redPackRuleIds.add(rule.getId());
                        }
                        //开始发送红包
                        if(redPackRuleIds!=null&&redPackRuleIds.size()>0) {
                            redPackService.sendMemberPadPackBatch(phones, red.getId(), StringUtils.join(redPackRuleIds, ","));
                        }
                    }
                }
            }
        }
    }
    public ActivityDTO validActivity(HttpServletRequest request,String cityId,String activityType){
        ActivityDTO activity=new ActivityDTO();
        Example example = new Example(Activity.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo(Activity.ACTIVITY_TYPE,activityType);
        criteria.andEqualTo(Activity.CITY_ID,cityId);
        criteria.andEqualTo(Activity.DELETE_STATE,"0");
        example.orderBy(Activity.MODIFY_DATE).desc();
        List<Activity> list = activityMapper.selectByExample(example);
        if(list!=null&&list.size()>0){
            for (Activity activity1:list) {
                if(activity1.getEndDate().getTime()>new Date().getTime()) {
                    activity.setId(activity1.getId());
                    ServerResponse response = activityService.getActivity(request, activity);
                    activity = (ActivityDTO) response.getResultObj();
                    break;
                }
            }

        }
        return activity;
    }
    /**
     * 可用优惠券数据
     * @param request
     * @param businessOrderNumber 订单号
     * @return
     */
    public ServerResponse discountPage(HttpServletRequest request,String businessOrderNumber){
        RedPageResult redPageResult = new RedPageResult();
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList.size() == 0){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        //满足条件的优惠券记录
        List<ActivityRedPackRecordDTO> redPacetResultList = new ArrayList<>();
        List<ActivityRedPackRecordDTO> redPacetNotList = new ArrayList<>();
        BusinessOrder businessOrder = businessOrderList.get(0);
        House house=houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
        ActivityRedPackRecord activityRedPackRecord=new ActivityRedPackRecord();
        activityRedPackRecord.setModifyDate(new Date());
        activityRedPackRecord.setMemberId(businessOrder.getMemberId());
        activityRedPackRecord.setCityId(house.getCityId());
        activityRedPackRecord.setHaveReceive(0);
        List<ActivityRedPackRecordDTO> redPacketRecordList=activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);

        activityRedPackRecord.setHaveReceive(1);
        activityRedPackRecord.setBusinessOrderNumber(businessOrder.getNumber());
        List<ActivityRedPackRecordDTO> redPacketRecordSelectList=activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);
        if(redPacketRecordSelectList!=null&&redPacketRecordSelectList.size()>0){
            redPacketRecordList.addAll(redPacketRecordSelectList);
        }
        if (redPacketRecordList.size() == 0) {
            return ServerResponse.createByErrorMessage("无可用优惠券");
        }
        String houseFlowId = businessOrder.getTaskId();
        request.setAttribute(Constants.CITY_ID,house.getCityId());
        ServerResponse serverResponse=budgetMaterialAPI.queryBudgetMaterialByHouseFlowId(request,houseFlowId);
        if(serverResponse.getResultObj()!=null){
            List<BudgetMaterial> budgetMaterialList=(List<BudgetMaterial>)serverResponse.getResultObj();

            for (ActivityRedPackRecordDTO redPacketRecord : redPacketRecordList) {
                BigDecimal workerTotal=new BigDecimal(0);
                BigDecimal goodsTotal=new BigDecimal(0);
                BigDecimal productTotal=new BigDecimal(0);
                for (BudgetMaterial budgetMaterial:budgetMaterialList) {
                    //判断工种的优惠券是否匹配
                    if (budgetMaterial.getWorkerTypeId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 0) {
                        workerTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                    }
                    //判断材料优惠券是否匹配
                    if (budgetMaterial.getGoodsId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 1) {
                        goodsTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                    }
                    //判断货品的优惠券是否匹配
                    if (budgetMaterial.getProductId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 2) {
                        productTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                    }
                }
                //判断优惠券类型是否为满减券
                if (redPacketRecord.getRedPack().getType() == 0 ) {
                    ///判断人工金额是否满足优惠上限金额
                    if (redPacketRecord.getRedPack().getFromObjectType() == 0 && workerTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                        redPacetResultList.add(redPacketRecord);
                    }else
                    //判断材料金额是否满足优惠上限金额
                    if (redPacketRecord.getRedPack().getFromObjectType() == 1 && goodsTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                        redPacetResultList.add(redPacketRecord);
                    }else
                    //判断货品金额是否满足优惠上限金额
                    if (redPacketRecord.getRedPack().getFromObjectType() == 2 && productTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                        redPacetResultList.add(redPacketRecord);
                    }else{
                        redPacetNotList.add(redPacketRecord);
                    }
                }
                //判断优惠券类型是否为折扣券或代金券
                if ((redPacketRecord.getRedPack().getType() == 1 ||redPacketRecord.getRedPack().getType() == 2)&&
                        (workerTotal.doubleValue()>0||goodsTotal.doubleValue()>0||productTotal.doubleValue()>0)) {
                    redPacetResultList.add(redPacketRecord);
                }
            }

        }
        if (redPacetResultList.size() == 0) {
            return ServerResponse.createByErrorMessage("无可用优惠券");
        }

        //格式化有效优惠券集合
        for (ActivityRedPackRecordDTO redPacetResult:redPacetResultList) {
            redPacetResult.toConvert();
        }
        redPageResult.setBusinessOrderNumber(businessOrderNumber);
        redPageResult.setRedPacetResultList(redPacetResultList);
        redPageResult.setRedPacetNotList(redPacetNotList);
        return ServerResponse.createBySuccess("ok",redPageResult);
    };

    /**
     * 确定选择
     */
    public ServerResponse submitDiscounts(HttpServletRequest request,String businessOrderNumber,String redPacketRecordIds){
        //得到订单
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList.size() == 0){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        String userToken = request.getParameter(Constants.USER_TOKEY);
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        BusinessOrder businessOrder = businessOrderList.get(0);
        House house=houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
        //还原历史记录的优惠券
        ActivityRedPackRecord activityRedPackRecord=new ActivityRedPackRecord();
        activityRedPackRecord.setMemberId(businessOrder.getMemberId());
        activityRedPackRecord.setBusinessOrderNumber(businessOrder.getNumber());
        List<ActivityRedPackRecordDTO> list = activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);
        if(list!=null&&list.size()>0) {
            for (ActivityRedPackRecordDTO redPacketRecord3 : list) {
                activityRedPackRecord=new ActivityRedPackRecord();
                BeanUtils.beanToBean(redPacketRecord3,activityRedPackRecord);
                activityRedPackRecord.setHaveReceive(0);
                activityRedPackRecord.setBusinessOrderNumber(null);
                activityRedPackRecord.setHouseId(null);
                activityRedPackRecord.setModifyDate(new Date());
                activityRedPackRecord.setPhone(null);
                activityRedPackRecordMapper.updateByPrimaryKeySelective(activityRedPackRecord);
            }
            businessOrder.setDiscountsPrice(new BigDecimal(0));
            businessOrder.setPayPrice(businessOrder.getTotalPrice());
            businessOrderMapper.updateByPrimaryKey(businessOrder);
        }

        //根据选择的优惠券重新计算优惠金额
        if(!CommonUtil.isEmpty(redPacketRecordIds)){
            String[] redPacketRecordIdList = StringUtils.split(redPacketRecordIds,",");
            for (String redPacketRecordId:redPacketRecordIdList) {
                ActivityRedPackRecord redPacketRecord=activityRedPackRecordMapper.selectByPrimaryKey(redPacketRecordId);
                ActivityRedPack redPack=activityRedPackMapper.selectByPrimaryKey(redPacketRecord.getRedPackId());
                ActivityRedPackRule redPackRule=activityRedPackRuleMapper.selectByPrimaryKey(redPacketRecord.getRedPackRuleId());
                String houseFlowId = businessOrder.getTaskId();
                request.setAttribute(Constants.CITY_ID,house.getCityId());
                ServerResponse serverResponse=budgetMaterialAPI.queryBudgetMaterialByHouseFlowId(request,houseFlowId);
                BigDecimal workerTotal=new BigDecimal(0);
                BigDecimal goodsTotal=new BigDecimal(0);
                BigDecimal productTotal=new BigDecimal(0);
                if(serverResponse.getResultObj()!=null){
                    List<BudgetMaterial> budgetMaterialList=(List<BudgetMaterial>)serverResponse.getResultObj();
                    for (BudgetMaterial budgetMaterial:budgetMaterialList) {
                        //判断工种的优惠券是否匹配
                        if (budgetMaterial.getWorkerTypeId().equals(redPack.getFromObject()) && redPack.getFromObjectType() == 0) {
                            workerTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                        }
                        //判断材料优惠券是否匹配
                        if (budgetMaterial.getGoodsId().equals(redPack.getFromObject()) && redPack.getFromObjectType() == 1) {
                            goodsTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                        }
                        //判断货品的优惠券是否匹配
                        if (budgetMaterial.getProductId().equals(redPack.getFromObject()) && redPack.getFromObjectType() == 2) {
                            productTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                        }
                    }
                }
                if (redPack.getType() == 0 || redPack.getType() == 2 ) {
                    BigDecimal money = redPackRule.getMoney();
                    if (businessOrder.getDiscountsPrice()  != null ) {
                        businessOrder.setDiscountsPrice(businessOrder.getDiscountsPrice().add(money));
                    }else{
                        businessOrder.setDiscountsPrice(money);
                    }
                }
                if (redPack.getType() == 1 ) {
                    BigDecimal money = new BigDecimal(0);
                    ///判断人工，折扣金额计算
                    if (redPack.getFromObjectType() == 0 && workerTotal.doubleValue()>0 ) {
                        money = workerTotal.subtract(workerTotal.multiply(redPackRule.getMoney().divide(new BigDecimal(10), 2, BigDecimal.ROUND_DOWN)));
                    }
                    //判断材料，折扣金额计算
                    if (redPack.getFromObjectType() == 1 && goodsTotal.doubleValue()>0 ) {
                        money = goodsTotal.subtract(goodsTotal.multiply(redPackRule.getMoney().divide(new BigDecimal(10), 2, BigDecimal.ROUND_DOWN)));
                    }
                    //判断货品，折扣金额计算
                    if (redPack.getFromObjectType() == 2 && productTotal.doubleValue()>0 ) {
                        money = productTotal.subtract(productTotal.multiply(redPackRule.getMoney().divide(new BigDecimal(10), 2, BigDecimal.ROUND_DOWN)));
                    }
                    if (businessOrder.getDiscountsPrice()  != null ) {
                        businessOrder.setDiscountsPrice(businessOrder.getDiscountsPrice().add(money));
                    }else{
                        businessOrder.setDiscountsPrice(money);
                    }
                }
                //更新使用到的优惠券为已使用
                redPacketRecord.setHaveReceive(1);
                redPacketRecord.setBusinessOrderNumber(businessOrder.getNumber());
                redPacketRecord.setHouseId(businessOrder.getHouseId());
                redPacketRecord.setModifyDate(new Date());
                redPacketRecord.setPhone(accessToken.getPhone());
                activityRedPackRecordMapper.updateByPrimaryKeySelective(redPacketRecord);
            }
            //更新订单实付金额
            businessOrder.setPayPrice(businessOrder.getTotalPrice().subtract(businessOrder.getDiscountsPrice()));
            businessOrderMapper.updateByPrimaryKey(businessOrder);
        }
        return ServerResponse.createBySuccessMessage("ok");
    }


}
