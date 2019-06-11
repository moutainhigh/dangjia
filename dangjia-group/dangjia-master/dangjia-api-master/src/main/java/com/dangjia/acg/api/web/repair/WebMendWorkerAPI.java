package com.dangjia.acg.api.web.repair;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:41
 */
@FeignClient("dangjia-service-master")
@Api(value = "Web端补退人工", description = "Web端补退人工")
public interface WebMendWorkerAPI {

    @PostMapping(value = "web/repair/webMendWorker/workerBackState")
    @ApiOperation(value = "查询退人工列表", notes = "查询退人工列表")
    ServerResponse workerBackState(@RequestParam("houseId") String houseId,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("beginDate") String beginDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam("likeAddress") String likeAddress);

    @PostMapping(value = "web/repair/webMendWorker/mendWorkerList")
    @ApiOperation(value = "人工单明细", notes = "人工单明细")
    ServerResponse mendWorkerList(@RequestParam("mendOrderId") String mendOrderId);

    @PostMapping(value = "web/repair/webMendWorker/workerOrderState")
    @ApiOperation(value = "补人工单列表", notes = "补人工单列表")
    ServerResponse workerOrderState(@RequestParam("houseId") String houseId,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("beginDate") String beginDate,
                                    @RequestParam("endDate") String endDate,
                                    @RequestParam("likeAddress") String likeAddress);
}
