package com.dangjia.acg.api.web.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:38
 * web端包括普通工匠端 补退货
 */
@FeignClient("dangjia-service-master")
@Api(value = "Web端补退货", description = "Web端补退货")
public interface WebMendMaterielAPI {

    @PostMapping(value = "web/repair/webMendMateriel/landlordState")
    @ApiOperation(value = "业主退货单列表", notes = "业主退货单列表")
    ServerResponse landlordState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);


    @PostMapping(value = "web/repair/webMendMateriel/materialBackState")
    @ApiOperation(value = "房子id查询退货单列表", notes = "房子id查询退货单列表")
    ServerResponse materialBackState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);

    @PostMapping(value = "web/repair/webMendMateriel/mendMaterialList")
    @ApiOperation(value = "补退单查明细", notes = "补退单查明细")
    ServerResponse mendMaterialList(@RequestParam("mendOrderId")String mendOrderId);

    @PostMapping(value = "web/repair/webMendMateriel/materialOrderState")
    @ApiOperation(value = "房子id查询补货单列表", notes = "房子id查询补货单列表")
    ServerResponse materialOrderState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);
}
