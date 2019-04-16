package com.dangjia.acg.service.worker;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 工匠管理
 * zmj
 */
@Service
public class WorkerService {
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IWithdrawDepositMapper withdrawDepositMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    /**
     * 查询通讯录
     */
    public ServerResponse getMailList(String userToken, String houseId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            House house = houseMapper.selectByPrimaryKey(houseId);
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();//返回通讯录list
            if (worker != null) {
                if (worker.getWorkerType() == 3) {//大管家
                    List<HouseWorker> listHouseWorker = houseWorkerMapper.paidListByHouseId(houseId);
                    for (HouseWorker houseWorker : listHouseWorker) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                        if (worker2 == null) {
                            continue;
                        }
                        map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId()).getName());
                        map.put("workerName", worker2.getName());
                        map.put("workerPhone", worker2.getMobile());
                        map.put("workerId", worker2.getId());
                        listMap.add(map);
                    }
                } else {//普通工匠
                    HouseWorker houseWorker = houseWorkerMapper.getHwByHidAndWtype(houseId, 3);
                    Map<String, Object> map = new HashMap<String, Object>();
                    Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());//根据工匠id查询工匠信息详情
                    map.put("workerTypeName", "大管家");
                    map.put("workerName", worker2.getName());//大管家
                    map.put("workerPhone", worker2.getMobile());
                    map.put("workerId", worker2.getId());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());//房主
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("workerTypeName", "业主");
                    map2.put("workerName", member.getNickName() == null ? member.getName() : member.getNickName());
                    map2.put("workerPhone", member.getMobile());
                    map2.put("workerId", member.getId());
                    listMap.add(map);
                    listMap.add(map2);
                }
            }
            return ServerResponse.createBySuccess("获取成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

    /**
     * 我的资料
     *
     * @param userToken
     * @return
     */
    public ServerResponse getWorker(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            return ServerResponse.createBySuccess("获取工匠基本信息成功", worker);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取工匠基本信息失败");
        }
    }

    /**
     * 提现记录
     *
     * @param userToken
     * @return
     */
    public ServerResponse getWithdrawDeposit(String userToken, PageDTO pageDTO) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WithdrawDeposit> wdList = withdrawDepositMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(wdList);
            return ServerResponse.createBySuccess("获取工匠提现记录成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠提现记录失败");
        }
    }

    /**
     * 我的任务
     *
     * @param userToken
     * @return
     */
    public ServerResponse getHouseWorkerList(String userToken, PageDTO pageDTO) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(HouseWorkerOrder.class);
            example.createCriteria().andEqualTo(HouseWorkerOrder.WORKER_ID, worker.getId());
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            example.orderBy(HouseFlow.CREATE_DATE).desc();
            List<HouseWorkerOrder> hwList = houseWorkerOrderMapper.selectByExample(example);
            List<Map> hwMapList = new ArrayList<>();
            for (HouseWorkerOrder hw : hwList) {
                Map hwMap = BeanUtils.beanToMap(hw);
                House house = houseMapper.selectByPrimaryKey(hw.getHouseId());
                if (house != null) {
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    if (member != null) {
                        hwMap.put(Member.NICK_NAME, member.getNickName());
                    }
                    hwMap.put("houseName", house.getHouseName());
                    hwMap.put("buildSquare", house.getBuildSquare());
                }
                hwMapList.add(hwMap);
            }
            PageInfo pageResult = new PageInfo(hwList);
            pageResult.setList(hwMapList);
            return ServerResponse.createBySuccess("获取我的任务成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取我的任务失败");
        }
    }

    /**
     * 我的任务-详情流水
     *
     * @param userToken
     * @return
     */
    public ServerResponse getHouseWorkerDetail(String userToken, PageDTO pageDTO, String houseId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(WorkerDetail.class);
            example.createCriteria().andEqualTo(WorkerDetail.WORKER_ID, worker.getId()).andEqualTo(WorkerDetail.HOUSE_ID, houseId);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            example.orderBy(WorkerDetail.CREATE_DATE).desc();
            List<WorkerDetail> hwList = workerDetailMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(hwList);
            return ServerResponse.createBySuccess("获取我的任务成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取我的任务失败");
        }
    }

    /**
     * 我的银行卡
     *
     * @param userToken
     * @return
     */
    public ServerResponse getMyBankCard(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(WorkerBankCard.class);
            example.createCriteria().andEqualTo(WorkerBankCard.WORKER_ID, worker.getId())
                    .andEqualTo(WorkerBankCard.DATA_STATUS, 0);
            List<WorkerBankCard> wbList = workerBankCardMapper.selectByExample(example);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (WorkerBankCard wb : wbList) {
                Map<String, Object> map = BeanUtils.beanToMap(wb);
                BankCard bankCard = bankCardMapper.selectByPrimaryKey(wb.getBankCardId());
                bankCard.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                map.put("bankCardName", bankCard == null ? "" : bankCard.getBankName());//银行名称
                map.put("bankCardImage", bankCard == null ? "" : bankCard.getBankCardImage());//银行卡图片
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("获取我的银行卡成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取我的银行卡失败");
        }
    }

    /**
     * 新增银行卡
     *
     * @param bankCard
     * @return
     */
    public ServerResponse addMyBankCard(HttpServletRequest request, String userToken, WorkerBankCard bankCard) {
        try {
            if (CommonUtil.isEmpty(bankCard.getBankCardNumber())) {
                return ServerResponse.createByErrorMessage("请输入银行卡卡号");
            }
            if (CommonUtil.isEmpty(bankCard.getBankCardId())) {
                return ServerResponse.createByErrorMessage("请选择银行卡类型");
            }
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Example example = new Example(WorkerBankCard.class);
            example.createCriteria().andEqualTo(WorkerBankCard.BANK_CARD_NUMBER, bankCard.getBankCardNumber())
                    .andEqualTo(WorkerBankCard.WORKER_ID, accessToken.getMember().getId());
            if (workerBankCardMapper.selectByExample(example).size() > 0) {
                return ServerResponse.createByErrorMessage("添加失败，银行卡以被使用！");
            }
            bankCard.setWorkerId(accessToken.getMember().getId());
            bankCard.setDataStatus(0);
            bankCard.setStatus(0);
            this.workerBankCardMapper.insertSelective(bankCard);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 删除银行卡
     *
     * @param request
     * @param userToken
     * @param workerBankCardId 银行卡ID
     * @return
     */
    public ServerResponse delMyBankCard(HttpServletRequest request, String userToken, String workerBankCardId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            WorkerBankCard workerBankCard = workerBankCardMapper.selectByPrimaryKey(workerBankCardId);
            if (workerBankCard == null) {
                return ServerResponse.createByErrorMessage("没有找到对应的银行卡");
            }
            if (!accessToken.getMember().getId().equals(workerBankCard.getWorkerId())) {
                return ServerResponse.createByErrorMessage("您无权删除此卡");
            }
            workerBankCard.setDataStatus(1);
            workerBankCardMapper.updateByPrimaryKeySelective(workerBankCard);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 我的二维码
     *
     * @param userToken
     * @return
     */
    public ServerResponse getMyQrcode(String userToken) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            return ServerResponse.createBySuccess("获取我的二维码成功", address + worker.getQrcode());
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取我的二维码失败");
        }
    }

    /**
     * 邀请排行榜
     *
     * @param userToken
     * @return
     */
    public ServerResponse getRanking(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(Member.class);
            example.createCriteria().andEqualTo("superiorId", worker.getId());
            List<Member> workerList = memberMapper.selectByExample(example);
            workerList.add(worker);
            for (Member w : workerList) {
                Example example2 = new Example(Member.class);
                example2.createCriteria().andEqualTo("superiorId", w.getId());
                List<Member> mList = memberMapper.selectByExample(example);
                w.setInviteNum(mList.size());
            }
            Collections.sort(workerList, new Comparator<Member>() {
                public int compare(Member w1, Member w2) {
                    return (int) (w2.getInviteNum() - w1.getInviteNum());
                }
            });
            return ServerResponse.createBySuccess("获取邀请排行榜成功", workerList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取邀请排行榜失败");
        }
    }

    /**
     * 接单记录
     *
     * @param userToken
     * @return
     */
    public ServerResponse getTakeOrder(String userToken) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);
            return ServerResponse.createBySuccess("获取接单记录成功", hwList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取接单记录失败");
        }
    }

}
