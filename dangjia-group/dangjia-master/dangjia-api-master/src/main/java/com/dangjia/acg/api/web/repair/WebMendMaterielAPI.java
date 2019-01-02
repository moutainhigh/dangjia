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

    /**
     * 通过不通过业主退货单
     */
    @PostMapping(value = "web/repair/webMendMateriel/checkLandlordState")
    @ApiOperation(value = "审核业主退货单", notes = "审核业主退货单")
    ServerResponse checkLandlordState(@RequestParam("mendOrderId")String mendOrderId, @RequestParam("state")int state,@RequestParam("carriage")Double carriage);

    /**
     * 业主退货单列表
     */
    @PostMapping(value = "web/repair/webMendMateriel/landlordState")
    @ApiOperation(value = "业主退货单列表", notes = "业主退货单列表")
    ServerResponse landlordState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);

    /**
     * 通过不通过退货单
     */
    @PostMapping(value = "web/repair/webMendMateriel/checkMaterialBackState")
    @ApiOperation(value = "审核退货单", notes = "审核退货单")
    ServerResponse checkMaterialBackState(@RequestParam("mendOrderId")String mendOrderId, @RequestParam("state")int state,@RequestParam("carriage")Double carriage);

    /**
     * 退货单列表
     */
    @PostMapping(value = "web/repair/webMendMateriel/materialBackState")
    @ApiOperation(value = "房子id查询退货单列表", notes = "房子id查询退货单列表")
    ServerResponse materialBackState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);

    /**
     * 审核补货单
     */
    @PostMapping(value = "web/repair/webMendMateriel/checkMaterialOrderState")
    @ApiOperation(value = "审核补货单", notes = "审核补货单")
    ServerResponse checkMaterialOrderState(@RequestParam("mendOrderId")String mendOrderId, @RequestParam("state")int state, @RequestParam("carriage")Double carriage);

    /**
     * 根据mendOrderId查明细
     */
    @PostMapping(value = "web/repair/webMendMateriel/mendMaterialList")
    @ApiOperation(value = "补退单查明细", notes = "补退单查明细")
    ServerResponse mendMaterialList(@RequestParam("mendOrderId")String mendOrderId);

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    @PostMapping(value = "web/repair/webMendMateriel/materialOrderState")
    @ApiOperation(value = "房子id查询补货单列表", notes = "房子id查询补货单列表")
    ServerResponse materialOrderState(@RequestParam("houseId")String houseId,@RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);
}
