package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:32
 */
@FeignClient("dangjia-service-master")
@Api(value = "工程部功能", description = "工程部功能")
public interface WebEngineerAPI {

    @PostMapping(value = "web/engineer/checkWorker")
    @ApiOperation(value = "工匠审核", notes = "工匠审核")
    ServerResponse checkWorker(@RequestParam("workerId") String workerId,
                               @RequestParam("checkType") Integer checkType,
                               @RequestParam("checkDescribe") String checkDescribe);

    @PostMapping(value = "web/engineer/changePayed")
    @ApiOperation(value = "已支付换工匠", notes = "已支付换工匠")
    ServerResponse changePayed(@RequestParam("houseWorkerId") String houseWorkerId,
                               @RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/changeWorker")
    @ApiOperation(value = "换工匠重新抢", notes = "换工匠重新抢")
    ServerResponse changeWorker(@RequestParam("houseWorkerId") String houseWorkerId);

    @PostMapping(value = "web/engineer/cancelLockWorker")
    @ApiOperation(value = "取消指定", notes = "取消指定")
    ServerResponse cancelLockWorker(@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping(value = "web/engineer/setLockWorker")
    @ApiOperation(value = "指定修改工匠", notes = "指定修改工匠")
    ServerResponse setLockWorker(@RequestParam("houseFlowId") String houseFlowId,
                                 @RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/grabRecord")
    @ApiOperation(value = "抢单记录", notes = "抢单记录")
    ServerResponse grabRecord(@RequestParam("houseId") String houseId,
                              @RequestParam("workerTypeId") String workerTypeId);

    @PostMapping(value = "web/engineer/workerOrder")
    @ApiOperation(value = "查看工匠订单", notes = "查看工匠订单")
    ServerResponse workerOrder(@RequestParam("houseId") String houseId);

    @PostMapping(value = "web/engineer/setState")
    @ApiOperation(value = "禁用启用工序", notes = "禁用启用工序")
    ServerResponse setState(@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping(value = "web/engineer/houseFlowList")
    @ApiOperation(value = "查看工序", notes = "查看工序")
    ServerResponse houseFlowList(@RequestParam("houseId") String houseId);

    @PostMapping(value = "web/engineer/workerMess")
    @ApiOperation(value = "工匠钱包信息", notes = "工匠钱包信息")
    ServerResponse workerMess(@RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/historyHouse")
    @ApiOperation(value = "历史工地", notes = "历史工地")
    ServerResponse historyHouse(@RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/setPause")
    @ApiOperation(value = "暂停恢复施工", notes = "暂停恢复施工")
    ServerResponse setPause(@RequestParam("houseId") String houseId);

    @PostMapping(value = "web/engineer/getHouseList")
    @ApiOperation(value = "工地列表", notes = "工地列表")
    ServerResponse getHouseList(@RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("visitState") Integer visitState,
                                @RequestParam("searchKey") String searchKey);

    @PostMapping(value = "web/engineer/artisanList")
    @ApiOperation(value = "工匠列表", notes = "工匠列表")
    ServerResponse artisanList(@RequestParam("name") String name,
                               @RequestParam("workerTypeId") String workerTypeId,
                               @RequestParam("type") String type, @RequestParam("checkType") String checkType ,
                               @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping(value = "web/engineer/getWareHouse")
    @ApiOperation(value = "仓库列表", notes = "仓库列表")
    ServerResponse getWareHouse(@RequestParam("houseId") String houseId,
                               @RequestParam("pageDTO") PageDTO pageDTO);

    @GetMapping("/web/engineer/exportWareHouse")
    @ApiOperation(value = "导出仓库", notes = "导出仓库", produces = "*/*,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/octet-stream")
    ServerResponse exportWareHouse(@RequestParam("response") HttpServletResponse response, @RequestParam("houseId") String houseId,@RequestParam("userName") String userName,@RequestParam("address") String address);

    @PostMapping(value = "web/engineer/freeze")
    @ApiOperation(value = "冻结账户", notes = "冻结账户")
    ServerResponse freeze(@RequestParam("memberId") String  memberId,@RequestParam("type") boolean type);

    @PostMapping(value = "web/engineer/getSureList")
    @ApiOperation(value = "获取工地交底/帮助事项", notes = "获取工地交底/帮助事项")
    ServerResponse getSureList(@RequestParam("type") Integer type, @RequestParam("state") Integer state,
                               @RequestParam("search") String search,@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping(value = "web/engineer/addSure")
    @ApiOperation(value = "添加工地交底/帮助事项", notes = "添加工地交底/帮助事项")
    ServerResponse addSure(@RequestParam("workerDisclosure") WorkerDisclosure workerDisclosure);

    @PostMapping(value = "web/engineer/updateSure")
    @ApiOperation(value = "修改工地交底/帮助事项", notes = "修改工地交底/帮助事项")
    ServerResponse updateSure(@RequestParam("name") String name,@RequestParam("details") String details,
                           @RequestParam("img") String img, @RequestParam("state") Integer state,
                              @RequestParam("id") String id);

    @PostMapping(value = "web/engineer/getItemsList")
    @ApiOperation(value = "查询开工完工事项", notes = "查询开工完工事项")
    ServerResponse getItemsList(@RequestParam("type") Integer type,@RequestParam("state") Integer state,
                                @RequestParam("search") String search,@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping(value = "web/engineer/addItems")
    @ApiOperation(value = "添加开工完工事项", notes = "添加开工完工事项")
    ServerResponse addItems(@RequestParam("name") String name, @RequestParam("type") Integer type,
                            @RequestParam("state") Integer state);

    @PostMapping(value = "web/engineer/updateItems")
    @ApiOperation(value = "修改开工完工事项", notes = "修改开工完工事项")
    ServerResponse updateItems(@RequestParam("name") String name, @RequestParam("type") Integer type,
                               @RequestParam("state") Integer state,@RequestParam("id") String id);
}
