package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.BrandCardDTO;
import com.dangjia.acg.dto.member.DetailDTO;
import com.dangjia.acg.dto.member.WalletDTO;
import com.dangjia.acg.dto.member.WithdrawDTO;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.worker.*;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.engineer.EngineerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 16:54
 */
@Service
public class WalletService {
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWithdrawDepositMapper withdrawDepositMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
    @Autowired
    private ISmsMapper smsMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private EngineerService engineerService;
    private String ruleDate = "2019年12月20日";//收费规则生效时间


    /**
     * 完成验证提现
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkFinish(String userToken, String paycode, Double money, String workerBankCardId, Integer roleType) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (member == null) {
            return ServerResponse.createbyUserTokenError();
        }
        if (member.getCheckType() == 4) {
            //冻结的帐户不能提现
            return ServerResponse.createByErrorMessage("账户冻结，无法提现");
        }
        if (paycode == null) {
            return ServerResponse.createByErrorMessage("验证码错误");
        }
        Integer code;
        try {
            code = Integer.valueOf(paycode);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("验证码错误");
        }
        if (!code.equals(member.getPaycode())) {
            return ServerResponse.createByErrorMessage("验证码错误");
        }
        if (money == null || money <= 0) {
            return ServerResponse.createByErrorMessage("金额错误，提现失败");
        }
        Double surplusMoney = member.getSurplusMoney().doubleValue();//可取
        if (surplusMoney - money < 0) {
            return ServerResponse.createByErrorMessage("余额不足，提现失败");
        }
        WorkerBankCard workerBankCard = workerBankCardMapper.selectByPrimaryKey(workerBankCardId);
        if (workerBankCard == null) {
            return ServerResponse.createByErrorMessage("您还未绑定银行卡,请重新选择");
        }
        BankCard bankCard = bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId());
        if (bankCard == null) {
            return ServerResponse.createByErrorMessage("银行卡信息不正确,请重新选择或添加");
        }
        boolean isOwner = isOwner(member);
        double applyMoney = money;//实际申请金额
        double depositMoney = money;//实际提现的钱
        double rateMoney = 0;//手续费
        if (!isOwner) {
            try {
                Date date = DateUtil.parseDate(ruleDate);
                if (date.getTime() < new Date().getTime()) {
                    if (money < 1000) {
                        rateMoney = money * 6.0 / 1000.0 + 1.0;
                    } else if (money < 6000) {
                        rateMoney = money * 7.0 / 1000.0;
                    } else if (money < 10000) {
                        rateMoney = money * 4.0 / 1000.0;
                    }
                    if (surplusMoney - (money + rateMoney) < 0) {
                        applyMoney = money;
                        depositMoney = money - rateMoney;
                    } else {
                        applyMoney = money + rateMoney;
                        depositMoney = money;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //生成提现订单
        WithdrawDeposit wd = new WithdrawDeposit();
        wd.setRoleType(roleType == 1 ? 1 : member.getWorkerType() == 3 ? 2 : 3);
        wd.setName(member.getName());
        wd.setWorkerId(member.getId());
        //----
        wd.setMoney(new BigDecimal(depositMoney));
        wd.setApplyMoney(new BigDecimal(applyMoney));
        wd.setRateMoney(new BigDecimal(rateMoney));
        wd.setBankName(bankCard.getBankName());
        wd.setCardNumber(workerBankCard.getBankCardNumber());
        wd.setState(0);//未处理
        withdrawDepositMapper.insert(wd);
        //记录流水
        BigDecimal haveMoney = member.getHaveMoney().subtract(new BigDecimal(applyMoney));
        BigDecimal surplusMoneys = member.getSurplusMoney().subtract(new BigDecimal(applyMoney));
        WorkerDetail workerDetail = new WorkerDetail();
        workerDetail.setName("提取现金");
        workerDetail.setWorkerId(member.getId());
        workerDetail.setWorkerName(member.getName());
        //----
        workerDetail.setMoney(new BigDecimal(applyMoney));
        workerDetail.setDepositMoney(new BigDecimal(depositMoney));
        workerDetail.setRateMoney(new BigDecimal(rateMoney));
        workerDetail.setDepositId(wd.getId());
        workerDetail.setState(1);//出
        workerDetail.setWalletMoney(surplusMoneys);
        workerDetailMapper.insert(workerDetail);
        member.setHaveMoney(haveMoney);//更新已有钱
        member.setSurplusMoney(surplusMoneys);
        member.setPaycode(0);//验证码置0
        memberMapper.updateByPrimaryKeySelective(member);
        return ServerResponse.createBySuccessMessage("提现成功！");
    }

    /**
     * 获取验证码
     */
    public ServerResponse getPaycode(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (member == null) {
            return ServerResponse.createbyUserTokenError();
        }
        if (member.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法提现");
        }
        int paycode = (int) (Math.random() * 9000 + 1000);
        String result = JsmsUtil.SMS(paycode, member.getMobile());
        //记录短信发送
        Sms sms = new Sms();
        sms.setCode(String.valueOf(paycode));
        sms.setMobile(member.getMobile());
        sms.setContent(result);
        smsMapper.insert(sms);
        member.setPaycode(paycode);//提现验证码
        memberMapper.updateByPrimaryKeySelective(member);
        return ServerResponse.createBySuccessMessage("验证码已发送！");
    }


    public ServerResponse verificationAmount(String userToken, Double money) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (member == null) {
            return ServerResponse.createbyUserTokenError();
        }
        if (member.getCheckType() == 4) {
            //冻结的帐户不能提现
            return ServerResponse.createByErrorMessage("账户冻结，无法提现");
        }
        if (money == null || money <= 0) {
            return ServerResponse.createByErrorMessage("金额错误，提现失败");
        }
        Double surplusMoney = member.getSurplusMoney().doubleValue();//可取
        if (surplusMoney - money < 0) {
            return ServerResponse.createByErrorMessage("余额不足，提现失败");
        }
        boolean isOwner = isOwner(member);
        double depositMoney;//实际提现的钱
        double rateMoney = 0;//手续费
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("depositMoney", money);
        map.put("rateMoney", rateMoney);
        map.put("message", "三个工作日内");
        if (!isOwner) {
            try {
                Date date = DateUtil.parseDate(ruleDate);
                if (date.getTime() < new Date().getTime()) {
                    if (money < 1000) {
                        rateMoney = money * 6.0 / 1000.0 + 1.0;
                        map.put("ruleMessage", "6‰+1元(<1000元)");
                    } else if (money < 6000) {
                        rateMoney = money * 7.0 / 1000.0;
                        map.put("ruleMessage", "7‰(1000元≤提现＜6000元)");
                    } else if (money < 10000) {
                        rateMoney = money * 4.0 / 1000.0;
                        map.put("ruleMessage", "4‰(6000元≤提现＜10000元)");
                    }
                    if (surplusMoney - (money + rateMoney) < 0) {
                        depositMoney = money - rateMoney;
                    } else {
                        depositMoney = money;
                    }
                    if (rateMoney > 0) {
                        map.put("depositMoney", depositMoney);
                        map.put("rateMoney", rateMoney);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return ServerResponse.createBySuccess("验证", map);
    }

    /**
     * 获取提现信息
     */
    public ServerResponse getWithdraw(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        member = memberMapper.selectByPrimaryKey(member.getId());
        //------查询有没有处罚
        Example example = new Example(RewardPunishRecord.class);
        example.createCriteria().andEqualTo(RewardPunishRecord.MEMBER_ID, member.getId()).andEqualTo(RewardPunishRecord.STATE, "0");
        List<RewardPunishRecord> recordList = rewardPunishRecordMapper.selectByExample(example);
        //通过查看奖罚限制抢单时间限制抢单
        for (RewardPunishRecord record : recordList) {
            example = new Example(RewardPunishCondition.class);
            example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID, record.getRewardPunishCorrelationId());
            List<RewardPunishCondition> conditionList = rewardPunishConditionMapper.selectByExample(example);
            for (RewardPunishCondition rewardPunishCondition : conditionList) {
                if (rewardPunishCondition.getType() == 4) {
                    Date tt = DateUtil.addDateDays(record.getCreateDate(), rewardPunishCondition.getQuantity().intValue());
                    Date date = new Date();
                    if (date.getTime() < tt.getTime()) {
                        return ServerResponse.createByErrorMessage("您处于平台处罚期内，" + DateUtil.getDateString2(tt.getTime()) + "以后才能提现,如有疑问请致电400-168-1231");
                    }
                }
            }
        }
        //------工匠关联银卡
        example = new Example(WorkerBankCard.class);
        example.createCriteria().andEqualTo(WorkerBankCard.WORKER_ID, member.getId()).andEqualTo(WorkerBankCard.DATA_STATUS, 0);
        List<WorkerBankCard> workerBankCardList = workerBankCardMapper.selectByExample(example);
        if (workerBankCardList.size() == 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "请绑定银行卡");
        }
        //----返回对应数据
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        String mobile = member.getMobile();//号码
        String mob = mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
        withdrawDTO.setMobile(mob);//电话
        withdrawDTO.setSurplusMoney(member.getSurplusMoney());//可取
        List<BrandCardDTO> brandCardDTOList = new ArrayList<>();
        boolean isOwner = isOwner(member);
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);//图片地址
        for (WorkerBankCard workerBankCard : workerBankCardList) {
            BrandCardDTO brandCardDTO = new BrandCardDTO();
            String number = workerBankCard.getBankCardNumber();//卡号
            BankCard bankCard = bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId());//银行
            number = number.replaceAll("\\s*", "");
            brandCardDTO.setBrandName(bankCard.getBankName() + number.substring(number.length() - 4));
            brandCardDTO.setWorkerBankCardId(workerBankCard.getId());
            brandCardDTO.setBkMaxAmt(bankCard.getBkMaxAmt() == null ? "20000" : bankCard.getBkMaxAmt());//最多提现
            if (isOwner) {
                brandCardDTO.setBkMinAmt("0");//最少提现
            } else {
                brandCardDTO.setBkMinAmt(bankCard.getBkMinAmt() == null ? "200" : bankCard.getBkMinAmt());//最少提现
            }
            brandCardDTO.setBankCardImage(address + bankCard.getBankCardImage());
            brandCardDTOList.add(brandCardDTO);
        }
        if (!isOwner) {
            withdrawDTO.setMessage("当家装修将在银行转账手续费补贴上做细微调整，于" + ruleDate + "起实行。");
            withdrawDTO.setMessageUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "activityPage?title=提现手续费规则");
        }
        withdrawDTO.setBrandCardDTOList(brandCardDTOList);
        return ServerResponse.createBySuccess("获取成功", withdrawDTO);
    }

    /**
     * 判断是不是业主
     */
    private boolean isOwner(@NotNull Member member) {
        return member.getCheckType() == 5 || member.getCheckType() == 0 || member.getCheckType() == 1;
    }

    /**
     * 0工钱收入,1提现,2自定义增加金额,3自定义减少金额,4退材料退款,5剩余材料退款,6退人工退款,7运费,8提现驳回到余额，9:提前结束装修退款,10滞留金收入，11:滞留金转出,12奖励，13:处罚"
     */
    private String getIcon(Integer state) {
        if (state == null) return "icon/rmb.png";
        switch (state) {
            case 1:
                return "icon/mywallet_icon-yhk.png";
            case 0:
            case 8:
            case 10:
            case 11:
                return "icon/mywallet_icon_rg.png";
            case 4:
            case 5:
            case 6:
            case 9:
                return "icon/mywallet_icon_tk.png";
        }
        return "icon/mywallet_icon_sj.png";
    }

    /**
     * 1提现,3自定义减少金额,,7运费,11:滞留金转出,13:处罚"
     */
    private String getPlusMinus(Integer state) {
        if (state == null) return "-";
        switch (state) {
            case 0:
            case 2:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
                return "+";
        }
        return "-";
    }

    /**
     * 流水详情
     */
    public ServerResponse getExtractDetail(String workerDetailId) {
        WorkerDetail workerDetail = workerDetailMapper.selectByPrimaryKey(workerDetailId);//根据流水id查询流水详情
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("id", workerDetail.getId());//id
        returnMap.put("typeName", workerDetail.getName());//流水详情描述
        returnMap.put("image", configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + getIcon(workerDetail.getState()));
        House house = houseMapper.selectByPrimaryKey(workerDetail.getHouseId());
        String houseName = "自定义流水";
        if (house != null) {
            houseName = house.getHouseName();//流水描述
        } else {
            if (StringUtils.isNoneBlank(workerDetail.getDefinedName()))//自定义流水说明
                houseName = workerDetail.getDefinedName();
        }
        returnMap.put("name", houseName);//流水来源
        if (workerDetail.getState() == 0
                || workerDetail.getState() == 2
                || workerDetail.getState() == 4
                || workerDetail.getState() == 5
                || workerDetail.getState() == 6
                || workerDetail.getState() == 8
                || workerDetail.getState() == 9) {
            returnMap.put("money", "+" + workerDetail.getMoney());//流水金额(加)
        } else {
            returnMap.put("money", "-" + workerDetail.getMoney());//流水金额(减)
        }
        returnMap.put("createDate", DateUtil.dateToString(workerDetail.getCreateDate(), "yyyy-MM-dd HH:mm"));//流水时间
        returnMap.put("rateMoney", workerDetail.getRateMoney());//手续费为空或为0不显示
        returnMap.put("depositMoney", workerDetail.getDepositMoney());//实际提现的钱为空或为0不显示
        returnMap.put("depositState", workerDetail.getState() == 8 ? 2 : -1);//-1不显示，0未处理,1同意 2不同意(驳回)
        if (workerDetail.getState() == 1) {//提现状态
            WithdrawDeposit wd = withdrawDepositMapper.selectByPrimaryKey(workerDetail.getDepositId());
            if (wd != null) {
                returnMap.put("depositState", wd.getState());//0未处理,1同意 2不同意(驳回)
                returnMap.put("memo", wd.getMemo());//备注
//                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
//                returnMap.put("depositImage", CommonUtil.isEmpty(wd.getImage()) ? null : imageAddress + wd.getImage());//回执单图片
                returnMap.put("reason", wd.getReason());//不同意理由
            }
        }
        return ServerResponse.createBySuccess("获取流水详情成功", returnMap);
    }

    /**
     * 资产流水
     *
     * @param userToken 用户ToKen
     * @param time      截止的年月（未来一年内） 默认当前年月
     * @param type      0=全部  1=收益  2=提现  3=奖罚
     * @param stateAll      0=余额  1=滞留金
     * @param pageDTO
     * @return
     */
    public ServerResponse workerDetail(String userToken, String time, String timeMark,Integer type,Integer stateAll, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        if (CommonUtil.isEmpty(time)) {
            time = DateUtil.dateToString(new Date(), DateUtil.FORMAT);
        }
        Date d = DateUtil.toDate(time);
        //取得月份最后的时间
        time = DateUtil.getDateString(DateUtil.getMonthLast(d).getTime());

        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String[] state =  new String[]{};
        if (stateAll==0&&type == 1) {//收益
            state = new String[]{"0"};
        }
        if (stateAll==0&&type == 2) {//提现
            state = new String[]{"1", "8"};

        }
        if (stateAll==0&&type == 3) {//奖罚
            state = new String[]{"12", "13"};

        }
        if (stateAll==0&&type == 4) {//业主流水
            state = new String[]{"0","1","2","3","4","5","6","7","8","9"};
        }

        if (stateAll==1&&type == 1) {//滞留金收入
            state = new String[]{"10"};
        }
        if (stateAll==1&&type == 2) {//滞留金溢出
            state = new String[]{"11", "101"};
        }
        if (stateAll==1&&type == 3) {//滞留金出账
            state = new String[]{"102","14"};
        }

        if(type==null || type==0){
            if (stateAll==1) {//滞留
                state = new String[]{"10", "11", "101", "102","14"};
            }
            if (stateAll==0) {//余额
                state = new String[]{"0","1","2","3","4","5","6","7","8","9","12","13"};
            }
        }
        Example example = new Example(WorkerDetail.class);
        Example.Criteria criteria = example.createCriteria();
        if(type!=5) {
            criteria.andLessThanOrEqualTo(WorkerDetail.CREATE_DATE, time);
            criteria.andGreaterThanOrEqualTo(WorkerDetail.CREATE_DATE, DateUtil.getDateString(DateUtil.getMonthFirst(DateUtil.toDate(time), -12).getTime()));
        }
        criteria.andEqualTo(WorkerDetail.WORKER_ID, member.getId());
        if (state != null&&state.length>0) {
            criteria.andIn(WorkerDetail.STATE, Arrays.asList(state));
        }
        example.orderBy(WorkerDetail.CREATE_DATE).desc();
        List<WorkerDetail> outDetailList = workerDetailMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(outDetailList);
        List<DetailDTO> detailDTOList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Map<String, DetailDTO> map = new HashMap<>();
        for (WorkerDetail workerDetail : outDetailList) {
            String timeYear = DateUtil.dateToString(workerDetail.getCreateDate(), DateUtil.FORMAT);
            String dqYear = DateUtil.dateToString(new Date(), DateUtil.FORMAT);
            DetailDTO detailDTO = new DetailDTO();
            if (map.get(timeYear) == null) {
                DetailDTO detailDTO2 = new DetailDTO();
                Date dVal = DateUtil.toDate(timeYear);
                String timeVal = DateUtil.getDateString(DateUtil.getMonthLast(dVal).getTime());
                Double income = workerDetailMapper.incomeMoney(member.getId(), timeVal, state);
                Double outMoney = workerDetailMapper.outMoney(member.getId(), timeVal, state);
                detailDTO2.setOutMoneyTotal(outMoney);
                detailDTO2.setInMoneyTotal(income);
                detailDTO2.setType(0);
                detailDTO2.setTime(timeYear);
                if (timeYear.equals(dqYear)) {
                    detailDTO2.setTime("本月");
                }
                map.put(timeYear, detailDTO2);
                if(!(!CommonUtil.isEmpty(timeMark) && timeYear.equals(timeMark))){
                    detailDTOList.add(detailDTO2);
                }
            }
            detailDTO.setWorkerDetailId(workerDetail.getId());
            detailDTO.setImage(imageAddress + getIcon(workerDetail.getState()));//图标
            detailDTO.setName(workerDetail.getName());
            detailDTO.setCreateDate(workerDetail.getCreateDate());
            detailDTO.setState(0);
            if (workerDetail.getState() == 11 || workerDetail.getState() == 10|| workerDetail.getState() == 101|| workerDetail.getState() == 102|| workerDetail.getState() == 14) {
                detailDTO.setState(1);
            }
            detailDTO.setType(1);
            detailDTO.setMoney(getPlusMinus(workerDetail.getState()) + workerDetail.getMoney());
            detailDTOList.add(detailDTO);
        }

        if (detailDTOList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        pageResult.setList(detailDTOList);
        return ServerResponse.createBySuccess("获取成功", pageResult);
    }

    /**
     * 钱包信息, 查询余额
     */
    public ServerResponse walletInformation(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        member = memberMapper.selectByPrimaryKey(member.getId());
        WalletDTO walletDTO = new WalletDTO();
        Double income = workerDetailMapper.incomeMoney(member.getId(), null, null);
        walletDTO.setWorkerPrice(income == null ? 0 : income);//总赚到
        walletDTO.setSurplusMoney(member.getSurplusMoney() == null ? new BigDecimal(0) : member.getSurplusMoney());//可取
        walletDTO.setRetentionMoney(member.getRetentionMoney() == null ? new BigDecimal(0) : member.getRetentionMoney());//滞留金
        walletDTO.setHouseOrder(0);
        if (member.getWorkerType() != null) {
            walletDTO.setHouseOrder(engineerService.alternative(member.getId(), member.getWorkerType()));
        }
        return ServerResponse.createBySuccess("获取成功", walletDTO);
    }

    /**
     * 指定某年的收入趋势
     */
    public ServerResponse getIncomeTrend(String userToken, String time) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        List income = workerDetailMapper.getHistoryMonth(member.getId(), time);
        return ServerResponse.createBySuccess("获取成功", income);
    }

}
