package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebWithdrawDTO;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.house.ModelingVillageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private IWithdrawDepositMapper iWithdrawDepositMapper;
    @Autowired
    private IHouseWorkerMapper iHouseWorkerMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IWorkerBankCardMapper iWorkerBankCardMapper;
    @Autowired
    private IBankCardMapper iBankCardMapper;
    @Autowired
    private IRewardPunishRecordMapper iRewardPunishRecordMapper;
    @Autowired
    private IRewardPunishConditionMapper iRewardPunishConditionMapper;
    @Autowired
    private ISmsMapper iSmsMapper;

    @Autowired
    private UserMapper userMapper;

    private static Logger LOG = LoggerFactory.getLogger(WebWithdrawDepositService.class);

    /**
     * 查询所有提现申请
     */
    public ServerResponse getAllWithdraw(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WithdrawDeposit> list = iWithdrawDepositMapper.getAllWithdraw();
            LOG.info(" getAllWithdraw list:" + list);

            List<WebWithdrawDTO> webWithdrawDTOlist = new ArrayList<>();

            for (WithdrawDeposit withdrawDeposit : list) {
                LOG.info("withdrawDeposit:" + withdrawDeposit);
                WebWithdrawDTO webWithdrawDTO = new WebWithdrawDTO();
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
                webWithdrawDTOlist.add(webWithdrawDTO);
// MainUser mainUser = userMapper.selectByPrimaryKey(customer.getUserId());
//                mcDTO.setUserName(mainUser.getUsername());
            }

//            logger.info(" mcDTOList getMemberNickName:" + mcDTOList.get(0).getMemberNickName());
//            logger.info("mcDTOList size:" + mcDTOList.size() +" mcDTOListOrderBy:"+ mcDTOListOrderBy.size() + " list:"+ list.size());
            PageInfo pageResult = new PageInfo(list);
            pageResult.setList(webWithdrawDTOlist);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 查询所有提现申请
     */
    public ServerResponse updateWithdraw(String workerId) {
        try {
//            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//            List<WithdrawDeposit> list = iWithdrawDepositMapper.getAllWithdraw();
////            logger.info(" mcDTOList getMemberNickName:" + mcDTOList.get(0).getMemberNickName());
////            logger.info("mcDTOList size:" + mcDTOList.size() +" mcDTOListOrderBy:"+ mcDTOListOrderBy.size() + " list:"+ list.size());
//            PageInfo pageResult = new PageInfo(list);
//            pageResult.setList(list);
            return ServerResponse.createBySuccess("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


}
