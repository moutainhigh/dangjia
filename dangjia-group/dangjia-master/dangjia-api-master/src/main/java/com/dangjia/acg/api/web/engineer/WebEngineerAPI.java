package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:32
 */
@FeignClient("dangjia-service-master")
@Api(value = "工程部功能", description = "工程部功能")
public interface WebEngineerAPI {

    @PostMapping(value = "web/engineer/changePayed")
    @ApiOperation(value = "已支付换工匠", notes = "已支付换工匠")
    ServerResponse changePayed(@RequestParam("houseWorkerId")String houseWorkerId,@RequestParam("workerId")String workerId);

    @PostMapping(value = "web/engineer/changeWorker")
    @ApiOperation(value = "换工匠重新抢", notes = "换工匠重新抢")
    ServerResponse changeWorker(@RequestParam("houseWorkerId")String houseWorkerId);

    @PostMapping(value = "web/engineer/cancelLockWorker")
    @ApiOperation(value = "取消指定", notes = "取消指定")
    ServerResponse cancelLockWorker(@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping(value = "web/engineer/setLockWorker")
    @ApiOperation(value = "指定修改工匠", notes = "指定修改工匠")
    ServerResponse setLockWorker(@RequestParam("houseFlowId")String houseFlowId,@RequestParam("workerId")String workerId);

    @PostMapping(value = "web/engineer/grabRecord")
    @ApiOperation(value = "抢单记录", notes = "抢单记录")
    ServerResponse grabRecord(@RequestParam("houseId")String houseId,@RequestParam("workerTypeId")String workerTypeId);

    @PostMapping(value = "web/engineer/workerOrder")
    @ApiOperation(value = "查看工匠订单", notes = "查看工匠订单")
    ServerResponse workerOrder(@RequestParam("houseId")String houseId);

    @PostMapping(value = "web/engineer/houseFlowList")
    @ApiOperation(value = "查看工序", notes = "查看工序")
    ServerResponse houseFlowList(@RequestParam("houseId")String houseId);

    @PostMapping(value = "web/engineer/workerMess")
    @ApiOperation(value = "工匠钱包信息", notes = "工匠钱包信息")
    ServerResponse workerMess(@RequestParam("workerId")String workerId);

    @PostMapping(value = "web/engineer/historyHouse")
    @ApiOperation(value = "历史工地", notes = "历史工地")
    ServerResponse historyHouse(@RequestParam("workerId")String workerId);

    @PostMapping(value = "web/engineer/getHouseList")
    @ApiOperation(value = "工地列表", notes = "工地列表")
    ServerResponse getHouseList(@RequestParam("pageDTO")PageDTO pageDTO);

    @PostMapping(value = "web/engineer/artisanList")
    @ApiOperation(value = "工匠列表", notes = "工匠列表")
    ServerResponse artisanList(@RequestParam("name")String name,@RequestParam("workerTypeId")String workerTypeId,
                               @RequestParam("pageDTO") PageDTO pageDTO);
}
