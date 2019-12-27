package com.dangjia.acg.api.app.worker;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "工匠管理接口", description = "工匠管理关联接口")
public interface WorkerAPI {

    @PostMapping("app/worker/integral/ranking")
    @ApiOperation(value = "获取积分排行记录", notes = "获取积分排行记录")
    ServerResponse queryRankingIntegral(Integer type, String userToken);

    @PostMapping("app/worker/getMailList")
    @ApiOperation(value = "通讯录", notes = "通讯录")
    ServerResponse getMailList(@RequestParam("userToken") String userToken,
                               @RequestParam("houseId") String houseId);

    @PostMapping("app/worker/getWorker")
    @ApiOperation(value = "我的资料", notes = "我的资料")
    ServerResponse getWorker(@RequestParam("userToken") String userToken);

    @PostMapping("app/worker/getWithdrawDeposit")
    @ApiOperation(value = "提现记录", notes = "提现记录")
    ServerResponse getWithdrawDeposit(@RequestParam("userToken") String userToken,
                                      @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("app/worker/getHouseWorkerList")
    @ApiOperation(value = "我的任务", notes = "我的任务")
    ServerResponse getHouseWorkerList(@RequestParam("userToken") String userToken,
                                      @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("app/worker/getHouseWorkerDetail")
    @ApiOperation(value = "我的任务-详细流水", notes = "我的任务-详细流水")
    ServerResponse getHouseWorkerDetail(@RequestParam("userToken") String userToken,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("houseId") String houseId);

    @PostMapping("app/worker/getMyBankCard")
    @ApiOperation(value = "我的银行卡", notes = "我的银行卡")
    ServerResponse getMyBankCard(@RequestParam("userToken") String userToken,
                                 @RequestParam("userId")  String userId);

    @PostMapping("app/worker/addMyBankCard")
    @ApiOperation(value = "添加银行卡", notes = "添加银行卡")
    ServerResponse addMyBankCard(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("bankCard") WorkerBankCard bankCard,
                                 @RequestParam("userId")  String userId,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("smscode") Integer smscode);

    @PostMapping("web/worker/registerCode")
    @ApiOperation(value = "绑定银行卡验证码", notes = "绑定银行卡验证码")
    ServerResponse registerCode(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("phone") String phone);

    @PostMapping("app/worker/delMyBankCard")
    @ApiOperation(value = "删除银行卡", notes = "删除银行卡")
    ServerResponse delMyBankCard(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("workerBankCardId") String workerBankCardId);

    @PostMapping("app/worker/untyingBankCard")
    @ApiOperation(value = "解绑银行卡", notes = "解绑银行卡")
    ServerResponse untyingBankCard(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("workerBankCardId") String workerBankCardId,
                                   @RequestParam("payPassword") String payPassword);


    @PostMapping("app/worker/getRanking")
    @ApiOperation(value = "邀请排行榜", notes = "邀请排行榜")
    ServerResponse getRanking(@RequestParam("userToken") String userToken);

    @PostMapping("app/worker/getTakeOrder")
    @ApiOperation(value = "接单记录", notes = "接单记录")
    ServerResponse getTakeOrder(@RequestParam("userToken") String userToken);

    @PostMapping("/app/rewardPunish/list")
    @ApiOperation(value = "奖罚记录", notes = "奖罚记录")
    ServerResponse queryRewardPunishRecord(@RequestParam("userToken") String userToken,
                                           @RequestParam("workerId") String workerId,
                                           @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/app/rewardPunish/get")
    @ApiOperation(value = "奖罚详情", notes = "奖罚详情")
    ServerResponse getRewardPunishRecord(@RequestParam("recordId") String recordId);
}
