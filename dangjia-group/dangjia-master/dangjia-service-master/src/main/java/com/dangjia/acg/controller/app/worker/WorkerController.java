package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.WorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.service.worker.RewardPunishService;
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
    public ServerResponse getMyBankCard(String userToken){
        return  workerService.getMyBankCard(userToken);
    }
    /**
     * 添加银行卡
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addMyBankCard(HttpServletRequest request, String userToken,WorkerBankCard bankCard){
        return  workerService.addMyBankCard(request,userToken,bankCard);
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
    public ServerResponse queryRewardPunishRecord(String userToken, String workerId,PageDTO pageDTO){
        return rewardPunishService.queryRewardPunishRecord(userToken, workerId,pageDTO);
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
