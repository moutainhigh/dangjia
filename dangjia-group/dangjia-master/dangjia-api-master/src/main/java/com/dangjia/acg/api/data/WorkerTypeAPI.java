package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.WorkerType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Ruking.Cheng
 * @descrilbe 工种查询
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/26 20:13
 */
@FeignClient("dangjia-service-master")
@Api(value = "工种接口", description = "工种接口")
public interface WorkerTypeAPI {

    @PostMapping("/data/workerType/unfinishedFlow")
    @ApiOperation(value = "已进场未完工工种", notes = "已进场未完工工种")
    ServerResponse unfinishedFlow(@RequestParam("houseId") String houseId);

    @PostMapping("/data/workerType/queryWorkerType")
    @ApiOperation(value = "根据workerTypeId返回工种对象", notes = "根据workerTypeId返回工种对象")
    WorkerType queryWorkerType(@RequestParam("workerTypeId") String workerTypeId);


    @PostMapping("/data/workerType/getWorkerTypeList")
    @ApiOperation(value = "获取工种列表", notes = "type：-1：全部，0：除精算防水工种列表，1：除设计精算防水工种列表，2：App选择工种专用")
    ServerResponse getWorkerTypeList(@RequestParam("type") Integer type);

    @PostMapping("/data/workerType/getWorkerType")
    @ApiOperation(value = "根据workerTypeId返回工种对象", notes = "根据workerTypeId返回工种对象")
    ServerResponse getWorkerType(@RequestParam("workerTypeId") String workerTypeId);

    @PostMapping("/data/workerType/updataWorkerType")
    @ApiOperation(value = "修改可抢单数，标准巡查次数，免费次数", notes = "修改可抢单数，标准巡查次数，免费次数")
    ServerResponse updataWorkerType(@RequestParam("workerTypeId") String workerTypeId,
                                    @RequestParam("methods") Integer methods,
                                    @RequestParam("inspectNumber") Integer inspectNumber,
                                    @RequestParam("safeState") Integer safeState);
}
