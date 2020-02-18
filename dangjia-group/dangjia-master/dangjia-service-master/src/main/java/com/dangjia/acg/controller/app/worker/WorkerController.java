package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.WorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.service.worker.RewardPunishService;
import com.dangjia.acg.service.worker.WorkerIntegraService;
import com.dangjia.acg.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**工匠管理
 * zmj
 */
@RestController
public class WorkerController implements WorkerAPI {

    @Autowired
    private WorkerService workerService;
    @Autowired
    private RewardPunishService rewardPunishService;


    @Autowired
    private WorkerIntegraService workerIntegraService;
    /**
     * 获取积分排行记录
     * @param type   0=排行榜 1=飙升榜
     */
    @Override
    @ApiMethod
    public ServerResponse queryRankingIntegral(Integer type, String userToken){
        return workerIntegraService.queryRankingIntegral(type, userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse getComprehensiveWorker(String userToken){
        return workerIntegraService.getComprehensiveWorker(userToken);
    }
    /**
     * 查询通讯录
     */
    @Override
    @ApiMethod
    public ServerResponse getMailList(String userToken, String houseId){
        return workerService.getMailList(userToken, houseId);
    }
    /**
     * 我的资料
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getWorker(String userToken){
        return  workerService.getWorker(userToken);
    }

    /**
     * 提现记录
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getWithdrawDeposit(String userToken,PageDTO pageDTO){
        return  workerService.getWithdrawDeposit(userToken,pageDTO);
    }

    /**
     * 我的任务
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseWorkerList(String userToken,PageDTO pageDTO){
        return  workerService.getHouseWorkerList(userToken,pageDTO);
    }
    /**
     * 我的任务-详情流水
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseWorkerDetail(String userToken,PageDTO pageDTO,String houseId){
        return  workerService.getHouseWorkerDetail(userToken,pageDTO,houseId);
    }
    /**
     * 我的银行卡
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getMyBankCard(String userToken,String userId){
        return  workerService.getMyBankCard(userToken,userId);
    }
    /**
     * 添加银行卡
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addMyBankCard(HttpServletRequest request, String userToken,WorkerBankCard bankCard,
                                        String userId,String phone,Integer smscode){
        return  workerService.addMyBankCard(request,userToken,bankCard,userId,phone,smscode);
    }


    /**
     * 绑定银行卡验证码
     * @param phone
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse registerCode(HttpServletRequest request,String phone) {
        return  workerService.registerCode(phone);
    }

    /**
     * 删除银行卡
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delMyBankCard(HttpServletRequest request, String userToken,String workerBankCardId){
        return  workerService.delMyBankCard(request,userToken,workerBankCardId);
    }

    /**
     * 解绑银行卡
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse untyingBankCard(HttpServletRequest request,String userId, String workerBankCardId,String payPassword){
        return  workerService.untyingBankCard(userId,workerBankCardId,payPassword);
    }

    /**
     * 邀请排行榜
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getRanking(String userToken){
        return  workerService.getRanking(userToken);
    }

    /**
     * 接单记录
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getTakeOrder(String userToken){
        return  workerService.getTakeOrder(userToken);
    }

    /**
     *奖罚记录
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRewardPunishRecord(String userToken, String workerId,String houseId,PageDTO pageDTO){
        return rewardPunishService.queryRewardPunishRecord(userToken, workerId, houseId,pageDTO);
    }
    /**
     *奖罚详情
     * @param recordId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getRewardPunishRecord(String recordId){
        return rewardPunishService.getRewardPunishRecord(recordId);
    }
}
