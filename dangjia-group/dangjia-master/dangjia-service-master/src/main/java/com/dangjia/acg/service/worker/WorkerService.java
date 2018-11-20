package com.dangjia.acg.service.worker;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**工匠管理
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
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    /**
     * 我的资料
     * @param userToken
     * @return
     */
    public ServerResponse getWorker(String userToken){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            return ServerResponse.createBySuccess("获取工匠基本信息成功",worker);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取工匠基本信息失败");
        }
    }

    /**
     * 提现记录
     * @param userToken
     * @return
     */
    public ServerResponse getWithdrawDeposit(String userToken){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<WithdrawDeposit> wdList=withdrawDepositMapper.selectByExample(example);
            return ServerResponse.createBySuccess("获取工匠流水明细成功",wdList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠流水明细失败");
        }
    }

    /**
     * 我的任务
     * @param userToken
     * @return
     */
    public ServerResponse gethouseWorkerList(String userToken){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<HouseWorkerOrder> hwList=houseWorkerOrderMapper.selectByExample(example);
            return ServerResponse.createBySuccess("获取工匠流水明细成功",hwList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠流水明细失败");
        }
    }

    /**
     * 我的银行卡
     * @param userToken
     * @return
     */
    public ServerResponse getMyBankCard(String userToken){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            Example example = new Example(WorkerBankCard.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<WorkerBankCard> wbList=workerBankCardMapper.selectByExample(example);
            List<Map<String,Object>> mapList=new ArrayList<>();
            for(WorkerBankCard wb:wbList){
                Map<String,Object> map = new HashMap<>();
                map.put("bankCardNumber",wb.getBankCardNumber());//银行卡号
                BankCard bankCard = bankCardMapper.selectByPrimaryKey(wb.getBankCardId());
                map.put("bankCardName",bankCard==null?"":bankCard.getBankName());//银行名称
                map.put("bankCardImage",bankCard==null?"":bankCard.getBankCardImage());//银行卡图片
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("获取工匠流水明细成功",mapList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠流水明细失败");
        }
    }

    /**
     * 我的二维码
     * @param userToken
     * @return
     */
    public ServerResponse getMyQrcode(String userToken){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            return ServerResponse.createBySuccess("获取工匠流水明细成功",address+worker.getQrcode());
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠流水明细失败");
        }
    }

    /**
     * 邀请排行榜
     * @param userToken
     * @return
     */
    public ServerResponse getRanking(String userToken){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            Example example = new Example(Member.class);
            example.createCriteria().andEqualTo("superiorId", worker.getId());
            List<Member> workerList = memberMapper.selectByExample(example);
            workerList.add(worker);
            for(Member w : workerList){
                Example example2 = new Example(Member.class);
                example2.createCriteria().andEqualTo("superiorId", w.getId());
                List<Member> mList = memberMapper.selectByExample(example);
                w.setInviteNum(mList.size());
            }
            Collections.sort(workerList,new Comparator<Member>() {
                public int compare(Member w1, Member w2) {
                    return (int)(w2.getInviteNum() - w1.getInviteNum());
                }
            });
            return ServerResponse.createBySuccess("获取工匠流水明细成功",workerList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取工匠流水明细失败");
        }
    }

    /**
     * 接单记录
     * @param userToken
     * @return
     */
    public ServerResponse getTakeOrder(String userToken){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
            }
            Member worker = accessToken.getMember();
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);
            return ServerResponse.createBySuccess("获取接单记录成功",hwList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取接单记录失败");
        }
    }

}
