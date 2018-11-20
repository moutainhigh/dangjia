package com.dangjia.acg.service.activity;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.dto.activity.RedPageResult;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRuleMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    private RedPackService redPackService;
    @Autowired
    private RedisClient redisClient;

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
        ActivityRedPackRecord activityRedPackRecord=new ActivityRedPackRecord();
        activityRedPackRecord.setModifyDate(new Date());
        activityRedPackRecord.setMemberId(businessOrder.getMemberId());
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
        String houseFlowId = businessOrder.getHouseflowIds();
        ServerResponse serverResponse=budgetMaterialAPI.queryBudgetMaterialByHouseFlowId(houseFlowId);
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
            if (redPacetResult.getRedPack().getType() == 0) {
                redPacetResult.setNameType("满减券");
            }else if(redPacetResult.getRedPack().getType() == 1){
                redPacetResult.setNameType("折扣券");
            }else{
                redPacetResult.setNameType("代金券");
            }
            redPacetResult.setValidTime(
                    "有效期:"
                    +new SimpleDateFormat("yyyy-MM-dd").format(redPacetResult.getRedPack().getStartDate())
                    +"至"
                    +new SimpleDateFormat("yyyy-MM-dd").format(redPacetResult.getRedPack().getEndDate())
            );
            if (redPacetResult.getRedPack().getIsShare() == 0) {
                redPacetResult.setShare("可与其他优惠券共同使用");
            }else{
                redPacetResult.setShare("不可与其他优惠券共同使用");
            }
            redPacetResult.setName(redPacetResult.getRedPack().getName());

            //上一次选中优惠券标记
            if(!StringUtils.isEmpty(redPacetResult.getBusinessOrderNumber())){
                redPacetResult.setSelected("1");
            }else{
                redPacetResult.setSelected("0");
            }
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
                String houseFlowId = businessOrder.getHouseflowIds();
                ServerResponse serverResponse=budgetMaterialAPI.queryBudgetMaterialByHouseFlowId(houseFlowId);
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
