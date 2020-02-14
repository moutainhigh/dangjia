package com.dangjia.acg.service.account;


import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.storefront.Storefront;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MasterAccountFlowRecordService {
    protected static final Logger logger = LoggerFactory.getLogger(MasterAccountFlowRecordService.class);
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;

    /**
     * 根据店铺ID,修改店铺的钱，记录对应的流水信息
     * @param storefrontId 店铺ID
     * @param orderId 订单ID
     * @param money 钱数，若为扣减，则为负数
     * @param state 0订单收入,1提现,2自定义增加金额,3自定义减少金额
     * @param orderId 订单ID
     * @param remark 流水记录说明（如：退货退款，自动扣减或店铺同意退货，扣减）
     * @param userId 当前操用人（若系统自动生成，则为SYSTEM);
     * @return
     */
    public ServerResponse updateStoreAccountMoney(String storefrontId,String houseId,Integer state,String orderId,Double money,String remark,String  userId){
        Storefront storefront=iMasterStorefrontMapper.selectByPrimaryKey(storefrontId);
        //1.修改店铺的账户总额
        //1.扣减店铺的总额和可提现余额
        Double totalAccount=storefront.getTotalAccount();//账户总额
        Double surplusMoney = storefront.getSurplusMoney();//可提现余额
        //Double retentionMoney = storefront.getRetentionMoney();//账户滞留金额
        //storefront.setRetentionMoney(MathUtil.add(retentionMoney,money));
        storefront.setTotalAccount(MathUtil.add(totalAccount,money));
        if(MathUtil.add(surplusMoney,money)<0){//如果小于0，则可提现余额改为0
            storefront.setSurplusMoney(0d);
        }else{
            storefront.setSurplusMoney(MathUtil.add(surplusMoney,money));
        }
        storefront.setModifyDate(new Date());
        iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
        //2.记录账户流水
        saveBillAccountFlowRecore(houseId,money,state,storefrontId,remark,orderId,totalAccount,storefront.getTotalAccount(),userId);
        return ServerResponse.createBySuccess("更新成功");
    }

    //存储账号流水记录
    private void saveBillAccountFlowRecore(String houseId,Double money,Integer state,String storefrontId,String remark,String orderId,Double beforeMoney, Double afterMoney,String userId){
        AccountFlowRecord accountFlowRecord=new AccountFlowRecord();
        accountFlowRecord.setFlowType("1");//1店铺，2供应商
        accountFlowRecord.setHouseId(houseId);//房子ID
        accountFlowRecord.setMoney(money);//金额，扣减为负数
        accountFlowRecord.setState(state);
        accountFlowRecord.setDefinedAccountId(storefrontId);
        accountFlowRecord.setDefinedName(remark);
        accountFlowRecord.setHouseOrderId(orderId);
        accountFlowRecord.setAmountBeforeMoney(beforeMoney);//更新前总额
        accountFlowRecord.setAmountAfterMoney(afterMoney);//更新后总额
        accountFlowRecord.setCreateBy(userId);
        accountFlowRecord.setUpdateBy(userId);
        iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
    }
}
