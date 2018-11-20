package com.dangjia.acg.api.app.worker;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "工匠管理接口", description = "工匠管理关联接口")
public interface WorkerAPI {

    @PostMapping("app/worker/getWorker")
    @ApiOperation(value = "我的资料", notes = "我的资料")
    ServerResponse getWorker(@RequestParam("userToken") String userToken );

    @PostMapping("app/worker/getWithdrawDeposit")
    @ApiOperation(value = "提现记录", notes = "提现记录")
    public ServerResponse getWithdrawDeposit(@RequestParam("userToken")String userToken);

    @PostMapping("app/worker/gethouseWorkerList")
    @ApiOperation(value = "我的任务", notes = "我的任务")
    public ServerResponse gethouseWorkerList(@RequestParam("userToken")String userToken);

    @PostMapping("app/worker/getMyBankCard")
    @ApiOperation(value = "我的银行卡", notes = "我的银行卡")
    public ServerResponse getMyBankCard(@RequestParam("userToken")String userToken);

    @PostMapping("app/worker/getRanking")
    @ApiOperation(value = "邀请排行榜", notes = "邀请排行榜")
    public ServerResponse getRanking(@RequestParam("userToken")String userToken);

    @PostMapping("app/worker/getTakeOrder")
    @ApiOperation(value = "接单记录", notes = "接单记录")
    public ServerResponse getTakeOrder(@RequestParam("userToken")String userToken);

}
