package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.BrandCardDTO;
import com.dangjia.acg.dto.member.DetailDTO;
import com.dangjia.acg.dto.member.WalletDTO;
import com.dangjia.acg.dto.member.WithdrawDTO;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.worker.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 16:54
 */
@Service
public class WalletService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWithdrawDepositMapper withdrawDepositMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
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


    /**
     * 完成验证提现
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkFinish(String userToken, Integer paycode, Double money, String workerBankCardId, Integer roleType) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());
            if (worker == null) {
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            worker = memberMapper.selectByPrimaryKey(worker.getId());
            if (!paycode.equals(worker.getPaycode())) {
                return ServerResponse.createByErrorMessage("验证码错误！");
            }
            if (money <= 0) {
                return ServerResponse.createByErrorMessage("金额错误，提现失败！");
            }
            Double surplusMoney = worker.getSurplusMoney().doubleValue();//可取

            if (surplusMoney - money < 0) {
                return ServerResponse.createByErrorMessage("余额不足，提现失败");
            }

            WorkerBankCard workerBankCard = workerBankCardMapper.selectByPrimaryKey(workerBankCardId);
            if (workerBankCard == null) {
                return ServerResponse.createByErrorMessage("您还未绑定银行卡,请重新选择");
            }

            //生成提现订单
            BankCard bankCard = bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId());
            WithdrawDeposit wd = new WithdrawDeposit();
            wd.setRoleType(roleType);
            wd.setName(worker.getName());
            wd.setWorkerId(worker.getId());
            wd.setMoney(new BigDecimal(money));
            wd.setBankName(bankCard == null ? "" : bankCard.getBankName());
            wd.setCardNumber(workerBankCard.getBankCardNumber());
            wd.setState(0);//未处理
            withdrawDepositMapper.insert(wd);


            BigDecimal haveMoney = worker.getHaveMoney().subtract(new BigDecimal(money));
            BigDecimal surplusMoneys = worker.getSurplusMoney().subtract(new BigDecimal(money));
            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName("提取现金");
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setMoney(new BigDecimal(money));
            workerDetail.setState(1);//出
            workerDetail.setWalletMoney(haveMoney);
            workerDetailMapper.insert(workerDetail);

            worker.setHaveMoney(haveMoney);//更新已有钱
            worker.setSurplusMoney(surplusMoneys);
            worker.setPaycode(0);//验证码置0
            memberMapper.updateByPrimaryKeySelective(worker);
            return ServerResponse.createBySuccessMessage("提现成功！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("系统出错，提现失败！");
        }
    }

    /**
     * 获取验证码
     */
    public ServerResponse getPaycode(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member member = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());
            if (member == null) {
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            int paycode = (int) (Math.random() * 9000 + 1000);
            JsmsUtil.SMS(paycode, member.getMobile());
            //记录短信发送
            Sms sms = new Sms();
            sms.setCode(String.valueOf(paycode));
            sms.setMobile(member.getMobile());
            smsMapper.insert(sms);

            member.setPaycode(paycode);//提现验证码
            memberMapper.updateByPrimaryKeySelective(member);
            return ServerResponse.createBySuccessMessage("验证码已发送！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取验证码失败");
        }
    }

    /**
     * 获取提现信息
     */
    public ServerResponse getWithdraw(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);//图片地址
            Member member = accessToken.getMember();
            member = memberMapper.selectByPrimaryKey(member.getId());
            Example example = new Example(RewardPunishRecord.class);
            example.createCriteria().andEqualTo(RewardPunishRecord.MEMBER_ID, member.getId());
            List<RewardPunishRecord> recordList = rewardPunishRecordMapper.selectByExample(example);
            //通过查看奖罚限制抢单时间限制抢单
            for (RewardPunishRecord record : recordList) {
                example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID, record.getRewardPunishCorrelationId());
                List<RewardPunishCondition> conditionList = rewardPunishConditionMapper.selectByExample(example);
                for (RewardPunishCondition rewardPunishCondition : conditionList) {
                    if (rewardPunishCondition.getType() == 4) {
                        Date endTime = rewardPunishCondition.getEndTime();
                        DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                        Date date = new Date();
                        if (date.getTime() < endTime.getTime()) {
                            return ServerResponse.createByErrorMessage("您处于平台处罚期内，" + longDateFormat.format(endTime) + "以后才能提现,如有疑问请致电400-168-1231");
                        }
                    }
                }
            }
            //工匠关联银卡
            example = new Example(WorkerBankCard.class);
            example.createCriteria().andEqualTo(WorkerBankCard.WORKER_ID, member.getId());
            List<WorkerBankCard> workerBankCardList = workerBankCardMapper.selectByExample(example);
            if (workerBankCardList.size() == 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "请绑定银行卡");
            }
            WithdrawDTO withdrawDTO = new WithdrawDTO();
            String mobile = member.getMobile();//号码
            String mob = mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length());

            withdrawDTO.setMobile(mob);//电话
            withdrawDTO.setSurplusMoney(member.getSurplusMoney());//可取
            List<BrandCardDTO> brandCardDTOList = new ArrayList<BrandCardDTO>();
            for (WorkerBankCard workerBankCard : workerBankCardList) {
                BrandCardDTO brandCardDTO = new BrandCardDTO();
                String number = workerBankCard.getBankCardNumber();//卡号
                BankCard bankCard = bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId());//银行
                number = number.replaceAll("\\s*", "");
                brandCardDTO.setBrandName(bankCard.getBankName() + number.substring(number.length() - 4, number.length()));
                brandCardDTO.setWorkerBankCardId(workerBankCard.getId());
                brandCardDTO.setBkMaxAmt(bankCard.getBkMaxAmt() == null ? "20000" : bankCard.getBkMaxAmt());//最多提现
                brandCardDTO.setBkMinAmt(bankCard.getBkMinAmt() == null ? "200" : bankCard.getBkMinAmt());//最少提现
                brandCardDTO.setBankCardImage(address + bankCard.getBankCardImage());
                brandCardDTOList.add(brandCardDTO);
            }
            withdrawDTO.setBrandCardDTOList(brandCardDTOList);
            return ServerResponse.createBySuccess("获取成功", withdrawDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

    /**
     * 流水详情
     */
    public ServerResponse getExtractDetail(String workerDetailId) {
        try {
            WorkerDetail workerDetail = workerDetailMapper.selectByPrimaryKey(workerDetailId);//根据流水id查询流水详情
            Map<String, Object> returnMap = new HashMap<String, Object>();
            returnMap.put("id", workerDetail.getId());//id
            returnMap.put("typeName", workerDetail.getName());//流水详情描述
            returnMap.put("image", configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/rmb.png");
            House house = houseMapper.selectByPrimaryKey(workerDetail.getHouseId());
            String houseName = "自定义流水";
            if (house != null) {
                houseName = house.getHouseName();//流水描述
            } else {
                if (StringUtils.isNoneBlank(workerDetail.getDefinedName()))//自定义流水说明
                    houseName = workerDetail.getDefinedName();
            }
            returnMap.put("name", houseName);//流水来源
            if (workerDetail.getState() == 0 || workerDetail.getState() == 2 || workerDetail.getState() == 4
                    || workerDetail.getState() == 5 || workerDetail.getState() == 6) {
                returnMap.put("money", "+" + workerDetail.getMoney());//流水金额(加)
            } else {
                returnMap.put("money", "-" + workerDetail.getMoney());//流水金额(减)
            }
            returnMap.put("createDate", workerDetail.getCreateDate().getTime());//流水时间

            return ServerResponse.createBySuccess("获取流水详情成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取流水详细失败");
        }
    }

    /**
     * 支出 收入
     */
    public ServerResponse workerDetail(String userToken, int type, Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();
            PageHelper.startPage(pageNum, pageSize);
            PageInfo pageResult;
            List<WorkerDetail> outDetailList;
            List<DetailDTO> detailDTOList = new ArrayList<DetailDTO>();
            if (type == 0) {//总支出
                outDetailList = workerDetailMapper.outDetail(member.getId());
                pageResult = new PageInfo(outDetailList);
                for (WorkerDetail workerDetail : outDetailList) {
                    DetailDTO detailDTO = new DetailDTO();
                    detailDTO.setWorkerDetailId(workerDetail.getId());
                    detailDTO.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/rmb.png");//图标
                    detailDTO.setName(workerDetail.getName());
                    detailDTO.setCreateDate(workerDetail.getCreateDate());
                    detailDTO.setMoney("-" + workerDetail.getMoney());
                    detailDTOList.add(detailDTO);
                }
                pageResult.setList(detailDTOList);
            } else {
                outDetailList = workerDetailMapper.incomeDetail(member.getId());
                pageResult = new PageInfo(outDetailList);
                for (WorkerDetail workerDetail : outDetailList) {
                    DetailDTO detailDTO = new DetailDTO();
                    detailDTO.setWorkerDetailId(workerDetail.getId());
                    detailDTO.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/rmb.png");//图标
                    detailDTO.setName(workerDetail.getName());
                    detailDTO.setCreateDate(workerDetail.getCreateDate());
                    detailDTO.setMoney("+" + workerDetail.getMoney());
                    detailDTOList.add(detailDTO);
                }
                pageResult.setList(detailDTOList);
            }

            return ServerResponse.createBySuccess("获取成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

    /**
     * 钱包信息, 查询余额
     */
    public ServerResponse walletInformation(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();
            member = memberMapper.selectByPrimaryKey(member.getId());
            WalletDTO walletDTO = new WalletDTO();
            Double workerPrice = workerDetailMapper.getCountWorkerDetailByWid(member.getId());
            Double out = workerDetailMapper.outMoney(member.getId());
            Double income = workerDetailMapper.incomeMoney(member.getId());

            walletDTO.setWorkerPrice(workerPrice == null ? 0 : workerPrice);//总赚到
            walletDTO.setSurplusMoney(member.getSurplusMoney() == null ? new BigDecimal(0) : member.getSurplusMoney());//可取
            walletDTO.setRetentionMoney(member.getRetentionMoney() == null ? new BigDecimal(0) : member.getRetentionMoney());//滞留金
            walletDTO.setOutAll(out == null ? 0 : out);//总支出
            walletDTO.setIncome(income == null ? 0 : income);//总收入
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId());
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            walletDTO.setHouseOrder(houseWorkerList.size());//接单量
//            walletDTO.setHouseOrder(member.getVolume().intValue());//接单量

            return ServerResponse.createBySuccess("获取成功", walletDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

}
