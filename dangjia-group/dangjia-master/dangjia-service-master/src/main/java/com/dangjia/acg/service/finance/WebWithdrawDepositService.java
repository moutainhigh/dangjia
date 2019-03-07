package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.finance.WebWithdrawDTO;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
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


    private static Logger LOG = LoggerFactory.getLogger(WebWithdrawDepositService.class);

    /**
     * 查询所有提现申请
     */
    public ServerResponse getAllWithdraw(PageDTO pageDTO, Integer state, String beginDate, String endDate) {
        try {
//            state = 0;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WithdrawDeposit> list = iWithdrawDepositMapper.getAllWithdraw(state, beginDate, endDate);
            PageInfo pageResult = new PageInfo(list);
            LOG.info(" getAllWithdraw state:" + state + " " + beginDate + " " + endDate);
            String strBeginDate = DateUtil.dateToString(DateUtil.getMondayOfThisWeek(), "yyyy-MM-dd HH:mm:ss");
            String strEndDate = DateUtil.dateToString(DateUtil.getSundayOfThisWeek(), "yyyy-MM-dd HH:mm:ss");
//            LOG.info("本周一 ：" + strBeginDate + "本周日 ：" + strEndDate);
            //state  0未处理,1同意（成功） 2不同意(驳回)
            Example example=new Example(WithdrawDeposit.class);
            example.createCriteria().andBetween(WithdrawDeposit.CREATE_DATE, strBeginDate, strEndDate);
            int curWeekAddSize = iWithdrawDepositMapper.selectCountByExample(example);
            example=new Example(WithdrawDeposit.class);
            example.createCriteria().andBetween(WithdrawDeposit.CREATE_DATE, strBeginDate, strEndDate)
                    .andEqualTo(WithdrawDeposit.STATE, 1);
            int curWeekSuccessSize = iWithdrawDepositMapper.selectCountByExample(example);

            example=new Example(WithdrawDeposit.class);
            example.createCriteria()
                    .andBetween(WithdrawDeposit.CREATE_DATE, strBeginDate, strEndDate)
                    .andEqualTo(WithdrawDeposit.STATE, 0);
            int curWeekNoHandleSize = iWithdrawDepositMapper.selectCountByExample(example);


            example=new Example(WithdrawDeposit.class);
            example.createCriteria()
                    .andEqualTo(WithdrawDeposit.STATE, 0);
            int allNoHandleSize = iWithdrawDepositMapper.selectCountByExample(example);
            LOG.info("getAllWithdraw size：" + curWeekAddSize + " " + curWeekSuccessSize +
                    " " + curWeekNoHandleSize + " " + allNoHandleSize);
            List<WebWithdrawDTO> webWithdrawDTOlist = new ArrayList<>();
            for (WithdrawDeposit withdrawDeposit : list) {
                WebWithdrawDTO webWithdrawDTO = new WebWithdrawDTO();
                webWithdrawDTO.setId(withdrawDeposit.getId());
                webWithdrawDTO.setBankName(withdrawDeposit.getBankName());
                Member member = iMemberMapper.selectByPrimaryKey(withdrawDeposit.getWorkerId());
                webWithdrawDTO.setMobile(member.getMobile());
                webWithdrawDTO.setCardNumber(withdrawDeposit.getCardNumber());
                webWithdrawDTO.setCreateDate(withdrawDeposit.getCreateDate());
                webWithdrawDTO.setModifyDate(withdrawDeposit.getModifyDate());
                webWithdrawDTO.setMoney(withdrawDeposit.getMoney());
                webWithdrawDTO.setName(withdrawDeposit.getName());
                webWithdrawDTO.setProcessingDate(withdrawDeposit.getProcessingDate());
                webWithdrawDTO.setState(withdrawDeposit.getState());
                webWithdrawDTO.setWorkerId(withdrawDeposit.getWorkerId());
                /*****************************统计********************************/
                webWithdrawDTO.setCurWeekAddNum(curWeekAddSize);//本周新增
                webWithdrawDTO.setCurWeekSuccessNum(curWeekSuccessSize);//本周 成功的
                webWithdrawDTO.setCurWeekNoHandleNum(curWeekNoHandleSize);//本周 待处理的
                webWithdrawDTO.setAllNoHandleNum(allNoHandleSize);//所有待处理的
                webWithdrawDTOlist.add(webWithdrawDTO);
            }
            pageResult.setList(webWithdrawDTOlist);
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
            String appType = "zx"; //zx =当家装修  ，     gj =当家工匠
            if (!StringUtils.isNoneBlank(withdrawDeposit.getId()))
                return ServerResponse.createByErrorMessage("Id 不能为null");
            WithdrawDeposit srcWithdrawDeposit = iWithdrawDepositMapper.selectByPrimaryKey(withdrawDeposit.getId());
            if (srcWithdrawDeposit == null)
                return ServerResponse.createByErrorMessage("无该提醒申请单");

            if (withdrawDeposit.getState() != -1) {//0未处理,1同意 2不同意(驳回)

                if (srcWithdrawDeposit.getRoleType() == 1) //1：业主端  2 大管家 3：工匠端
                    appType = "zx"; // appType ：  zx =当家装修  ，     gj =当家工匠
                else
                    appType = "gj";

                if (withdrawDeposit.getState() == 2) {//2不同意
                    srcWithdrawDeposit.setState(2);
                    srcWithdrawDeposit.setReason(withdrawDeposit.getReason());
                    if (StringUtils.isNoneBlank(withdrawDeposit.getMemo()))
                        srcWithdrawDeposit.setMemo(withdrawDeposit.getMemo());

                    Member worker = iMemberMapper.selectByPrimaryKey(srcWithdrawDeposit.getWorkerId());
                    BigDecimal money = srcWithdrawDeposit.getMoney();

                    //记录流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("提现驳回");
                    workerDetail.setWorkerId(worker.getId());
                    workerDetail.setWorkerName(worker.getName());
                    workerDetail.setMoney(money);
                    workerDetail.setDefinedName("提现驳回到余额");
                    workerDetail.setState(7);//7提现驳回到余额
                    workerDetail.setWalletMoney(worker.getHaveMoney());
                    iWorkerDetailMapper.insert(workerDetail);

                    //把钱 转到 余额上面
                    worker.setHaveMoney(worker.getHaveMoney().add(money));//更新已有钱
                    worker.setSurplusMoney(worker.getSurplusMoney().add(money));
                    iMemberMapper.updateByPrimaryKeySelective(worker);

                    //提现失败推送
                    configMessageService.addConfigMessage(null, appType,
                            withdrawDeposit.getWorkerId(),
                            "0", "提现结果",
                            DjConstants.PushMessage.WITHDRAW_CASH_ERROR, "");
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
                }

                iWithdrawDepositMapper.updateByPrimaryKey(srcWithdrawDeposit);
            }

            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
