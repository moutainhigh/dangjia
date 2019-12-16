package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebWithdrawDTO;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.sql.config.DruidConfig;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class WebWithdrawDepositService {
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWithdrawDepositMapper iWithdrawDepositMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMasterSupplierMapper iMaterSupplierMapper;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;
    private Logger logger = LoggerFactory.getLogger(WebWithdrawDepositService.class);
    /**
     * 查询所有提现申请
     *
     * @param pageDTO   分页码
     * @param searchKey 模糊查询：名称，手机号
     * @param state     状态
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public ServerResponse getAllWithdraw(PageDTO pageDTO,String cityId, String searchKey, Integer state, String beginDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                beginDate = beginDate + " " + "00:00:00";
                endDate = endDate + " " + "23:59:59";
            }
            List<WebWithdrawDTO> withdrawDTOList = iWithdrawDepositMapper.getWebWithdrawList(state,cityId, searchKey, beginDate, endDate);
            PageInfo pageResult = new PageInfo(withdrawDTOList);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (WebWithdrawDTO webWithdrawDTO : withdrawDTOList) {
                if (!CommonUtil.isEmpty(webWithdrawDTO.getImage())) {
                    webWithdrawDTO.setImage(imageAddress + webWithdrawDTO.getImage());
                }
            }
            pageResult.setList(withdrawDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改提现申请 状态
     * * state  0未处理,1同意 2不同意(驳回)
     * *   reason 不同意理由
     * *  memo 附言
     * image 回执单图片
     *
     * @param withdrawDeposit
     * @return
     */
    public ServerResponse setWithdraw(WithdrawDeposit withdrawDeposit) {
        try {
//            LOG.info("setWithdraw :" + withdrawDeposit);
            AppType appType; //zx =当家装修  ，     gj =当家工匠
            if (!StringUtils.isNoneBlank(withdrawDeposit.getId()))
                return ServerResponse.createByErrorMessage("Id 不能为null");
            WithdrawDeposit srcWithdrawDeposit = iWithdrawDepositMapper.selectByPrimaryKey(withdrawDeposit.getId());
            if (srcWithdrawDeposit == null)
                return ServerResponse.createByErrorMessage("无该提醒申请单");
            if (withdrawDeposit.getState() != -1) {//0未处理,1同意 2不同意(驳回)
                if (srcWithdrawDeposit.getRoleType() == 1) //1：业主端  2 大管家 3：工匠端
                    appType = AppType.ZHUANGXIU; // appType ：  zx =当家装修  ，     gj =当家工匠
                else
                    appType = AppType.GONGJIANG;
                if (withdrawDeposit.getState() == 2) {//2不同意
                    srcWithdrawDeposit.setState(2);
                    srcWithdrawDeposit.setReason(withdrawDeposit.getReason());
                    if (StringUtils.isNoneBlank(withdrawDeposit.getMemo()))
                        srcWithdrawDeposit.setMemo(withdrawDeposit.getMemo());
                    Member worker = iMemberMapper.selectByPrimaryKey(srcWithdrawDeposit.getWorkerId());
                    if(worker!=null) {
                        BigDecimal money = srcWithdrawDeposit.getMoney();
                        BigDecimal haveMoney = worker.getHaveMoney().add(money);
                        BigDecimal surplusMoney = worker.getSurplusMoney().add(money);
                        //记录流水
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName("提现驳回");
                        workerDetail.setWorkerId(worker.getId());
                        workerDetail.setWorkerName(worker.getName());
                        workerDetail.setMoney(money);
                        workerDetail.setDefinedName("提现驳回到余额");
                        workerDetail.setState(8);//8提现驳回到余额
                        workerDetail.setWalletMoney(surplusMoney);
                        iWorkerDetailMapper.insert(workerDetail);
                        //把钱 转到 余额上面
                        worker.setHaveMoney(haveMoney);//更新已有钱
                        worker.setSurplusMoney(surplusMoney);
                        worker.setModifyDate(new Date());
                        iMemberMapper.updateByPrimaryKeySelective(worker);
                        //提现失败推送
                        configMessageService.addConfigMessage(null, appType,
                                withdrawDeposit.getWorkerId(),
                                "0", "提现结果",
                                DjConstants.PushMessage.WITHDRAW_CASH_ERROR, "");
                    }
                    if(srcWithdrawDeposit.getRoleType()==4 || srcWithdrawDeposit.getRoleType()==5){
                        //拒绝供应商/店铺提现
                        setRefusedWithdraw(srcWithdrawDeposit.getRoleType(),
                                srcWithdrawDeposit.getSourceId(),
                                srcWithdrawDeposit.getMoney());
                    }
                }
                if (withdrawDeposit.getState() == 1) {//1同意
                    srcWithdrawDeposit.setState(1);
                    srcWithdrawDeposit.setImage(withdrawDeposit.getImage());//回执单
                    if (StringUtils.isNoneBlank(withdrawDeposit.getMemo()))
                        srcWithdrawDeposit.setMemo(withdrawDeposit.getMemo());
                    //提现成功推送
                    configMessageService.addConfigMessage(null, appType,
                            withdrawDeposit.getWorkerId(),
                            "0", "提现结果",
                            DjConstants.PushMessage.WITHDRAW_CASH_SUCCESS, "");

                    if(srcWithdrawDeposit.getRoleType()==4 ||srcWithdrawDeposit.getRoleType() ==5 ){
                        //同意供应商/店铺提现
                        setAgreeWithdraw(srcWithdrawDeposit.getRoleType(),
                                srcWithdrawDeposit.getSourceId(),
                                srcWithdrawDeposit.getWorkerId(),
                                srcWithdrawDeposit.getMoney(),
                                srcWithdrawDeposit.getId());

                    }
                }
                srcWithdrawDeposit.setModifyDate(new Date());
                srcWithdrawDeposit.setProcessingDate(new Date());
                iWithdrawDepositMapper.updateByPrimaryKey(srcWithdrawDeposit);

            }

            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            logger.info("查询失败",e);
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 同意供应商/店铺提现
     */
    public void setAgreeWithdraw(Integer roleType,
                                 String sourceId,
                                 String workerId,
                                 BigDecimal money,
                                 String withdrawDepositId){
        try {
            AccountFlowRecord accountFlowRecord=new AccountFlowRecord();
            //供应商提现
            if(roleType==4){
                DjSupplier djSupplier = iMaterSupplierMapper.selectByPrimaryKey(sourceId);
                accountFlowRecord.setFlowType("2");
                accountFlowRecord.setDefinedAccountId(djSupplier.getId());
                accountFlowRecord.setDefinedName("供应商提现："+money);
                accountFlowRecord.setAmountBeforeMoney(djSupplier.getTotalAccount());
                accountFlowRecord.setAmountAfterMoney(djSupplier.getTotalAccount()+money.doubleValue());
            }else if(roleType==5){//店铺提现
                Storefront storefront = iMasterStorefrontMapper.selectByPrimaryKey(sourceId);
                accountFlowRecord.setFlowType("1");
                accountFlowRecord.setDefinedAccountId(storefront.getId());
                accountFlowRecord.setDefinedName("店铺提现："+money);
                accountFlowRecord.setAmountBeforeMoney(storefront.getTotalAccount());
                accountFlowRecord.setAmountAfterMoney(storefront.getTotalAccount()+money.doubleValue());
            }
            accountFlowRecord.setState(1);
            accountFlowRecord.setMoney(money.doubleValue());
            accountFlowRecord.setHouseOrderId(withdrawDepositId);
            accountFlowRecord.setCreateBy(workerId);
            accountFlowRecord.setDataStatus(0);
            iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拒绝供应商/店铺提现
     */
    public void setRefusedWithdraw(Integer roleType,String sourceId,BigDecimal money){
        try {
            //拒绝供应商提现
            if(roleType==4){
                DjSupplier djSupplier = iMaterSupplierMapper.selectByPrimaryKey(sourceId);
                djSupplier.setTotalAccount(djSupplier.getTotalAccount()+money.doubleValue());
                djSupplier.setSurplusMoney(djSupplier.getSurplusMoney()+money.doubleValue());
                iMaterSupplierMapper.updateByPrimaryKeySelective(djSupplier);
            }else if(roleType==5){//拒绝店铺提现
                Storefront storefront = iMasterStorefrontMapper.selectByPrimaryKey(sourceId);
                storefront.setTotalAccount(storefront.getTotalAccount()+money.doubleValue());
                storefront.setSurplusMoney(storefront.getSurplusMoney()+money.doubleValue());
                iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
