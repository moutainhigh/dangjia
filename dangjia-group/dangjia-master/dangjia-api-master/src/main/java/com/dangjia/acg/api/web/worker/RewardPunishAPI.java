package com.dangjia.acg.api.web.worker;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 18:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "奖罚管理接口", description = "奖罚管理接口")
public interface RewardPunishAPI {

    @PostMapping("/web/rewardPunish/addRewardPunishCorrelation")
    @ApiOperation(value = "保存奖罚条件及条件明细", notes = "保存奖罚条件及条件明细")
    ServerResponse addRewardPunishCorrelation(@RequestParam("id") String id,
                                              @RequestParam("name") String name,
                                              @RequestParam("content") String content,
                                              @RequestParam("type") Integer type,
                                              @RequestParam("state") Integer state,
                                              @RequestParam("conditionArr") String conditionArr,
                                              @RequestParam("quantity") BigDecimal quantity);

    @PostMapping("/web/rewardPunish/deleteRewardPunishCorrelation")
    @ApiOperation(value = "删除奖罚条件及条件明细", notes = "删除奖罚条件及条件明细")
    ServerResponse deleteRewardPunishCorrelation(@RequestParam("id") String id);

    @PostMapping("/web/rewardPunish/queryCorrelation")
    @ApiOperation(value = "查询所有奖罚条件及条件明细", notes = "查询所有奖罚条件及条件明细")
    ServerResponse queryCorrelation(@RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("name") String name,
                                    @RequestParam("type") Integer type);

    @PostMapping("/web/rewardPunish/queryCorrelationById")
    @ApiOperation(value = "根据id查询奖罚条件及明细", notes = "根据id查询奖罚条件及明细")
    ServerResponse queryCorrelationById(@RequestParam("id") String id);

    @PostMapping("/web/rewardPunish/addRewardPunishRecord")
    @ApiOperation(value = "添加奖罚记录", notes = "添加奖罚记录")
    ServerResponse addRewardPunishRecord(@RequestParam("userToken") String userToken,
                                         @RequestParam("userId") String userId,
                                         @RequestParam("rewardPunishRecord") RewardPunishRecord rewardPunishRecord);
}
