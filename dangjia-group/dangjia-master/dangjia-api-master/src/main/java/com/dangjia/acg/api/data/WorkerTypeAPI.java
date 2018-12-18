package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 11:00
 */
@FeignClient("dangjia-service-master")
@Api(value = "工种接口", description = "工种接口")
public interface WorkerTypeAPI {

    @PostMapping("/data/workerType/getWorkerTypeRegister")
    @ApiOperation(value = "除设计防水工种列表", notes = "除设计精算防水工种列表")
    ServerResponse getWorkerTypeRegister();

    @PostMapping("/data/workerType/getWorkerTypeList")
    @ApiOperation(value = "除设计精算防水工种列表", notes = "除设计精算防水工种列表")
    ServerResponse getWorkerTypeList();

    @PostMapping("/data/workerType/list")
    @ApiOperation(value = "所有可用工种列表", notes = "所有可用工种列表")
    ServerResponse getWorkerTypeListAll();

    @PostMapping("/data/workerType/getNameByWorkerTypeId")
    @ApiOperation(value = "根据workerTypeId返回工种名字", notes = "根据workerTypeId返回工种名字")
    ServerResponse getNameByWorkerTypeId(@RequestParam("workerTypeId")String workerTypeId);

    @PostMapping("/data/workerType/getWorkerType")
    @ApiOperation(value = "根据workerTypeId返回工种对象", notes = "根据workerTypeId返回工种对象")
    ServerResponse getWorkerType(@RequestParam("workerTypeId")String workerTypeId);
}
