package com.dangjia.acg.service.finance;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.*;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.*;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class WebWalletService {
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
    private static Logger LOG = LoggerFactory.getLogger(WebWalletService.class);
    /**
     * 所有订单流水
     */
    public ServerResponse getAllWallet(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WorkerDetail> list = iWorkerDetailMapper.getAllWallet();
            LOG.info(" getAllWallet list:"+ list);
//            logger.info(" mcDTOList getMemberNickName:" + mcDTOList.get(0).getMemberNickName());
//            logger.info("mcDTOList size:" + mcDTOList.size() +" mcDTOListOrderBy:"+ mcDTOListOrderBy.size() + " list:"+ list.size());
            PageInfo pageResult = new PageInfo(list);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


}
