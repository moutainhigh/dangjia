package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.WorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**工匠管理
 * zmj
 */
@RestController
public class WorkerController implements WorkerAPI {

    @Autowired
    private WorkerService workerService;
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
    public ServerResponse getWithdrawDeposit(String userToken){
        return  workerService.getWithdrawDeposit(userToken);
    }

    /**
     * 我的任务
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse gethouseWorkerList(String userToken){
        return  workerService.gethouseWorkerList(userToken);
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

}
