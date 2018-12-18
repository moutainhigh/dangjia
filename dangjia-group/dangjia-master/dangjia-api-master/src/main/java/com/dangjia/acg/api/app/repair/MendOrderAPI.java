package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("dangjia-service-master")
@Api(value = "用户接口", description = "用户接口")
public interface MendOrderAPI {

    @RequestMapping(value = "app/repair/mendOrder/confirmBackMendWorker")
    @ApiOperation(value = "确认退人工", notes = "确认退人工")
    ServerResponse confirmBackMendWorker(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/backMendWorkerList")
    @ApiOperation(value = "退人工单明细", notes = "退人工单明细")
    ServerResponse backMendWorkerList(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/backMendWorker")
    @ApiOperation(value = "提交退人工", notes = "提交退人工")
    ServerResponse backMendWorker(@RequestParam("userToken")String userToken,@RequestParam("houseId")String houseId
            ,@RequestParam("workerGoodsArr")String workerGoodsArr,@RequestParam("workerTypeId")String workerTypeId);

    @RequestMapping(value = "app/repair/mendOrder/confirmMendWorker")
    @ApiOperation(value = "确认补人工", notes = "确认补人工")
    ServerResponse confirmMendWorker(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/getMendWorkerList")
    @ApiOperation(value = "补人工单明细", notes = "补人工单明细")
    ServerResponse getMendWorkerList(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/saveMendWorker")
    @ApiOperation(value = "保存补人工", notes = "保存补人工")
    ServerResponse saveMendWorker(@RequestParam("userToken")String userToken,@RequestParam("houseId")String houseId
            ,@RequestParam("workerGoodsArr")String workerGoodsArr,@RequestParam("workerTypeId")String workerTypeId);

    @RequestMapping(value = "app/repair/mendOrder/confirmBackMendMaterial")
    @ApiOperation(value = "确认退货", notes = "确认退货")
    ServerResponse confirmBackMendMaterial(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/backMendMaterialList")
    @ApiOperation(value = "退货单明细", notes = "退货单明细")
    ServerResponse backMendMaterialList(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/backMendMaterial")
    @ApiOperation(value = "提交退货", notes = "提交退货")
    ServerResponse backMendMaterial(@RequestParam("userToken")String userToken,@RequestParam("houseId")String houseId
            ,@RequestParam("productArr")String productArr);

    @RequestMapping(value = "app/repair/mendOrder/confirmMendMaterial")
    @ApiOperation(value = "确认补货", notes = "确认补货")
    ServerResponse confirmMendMaterial(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/getMendMaterialList")
    @ApiOperation(value = "已添加补材料单明细", notes = "已添加补材料单明细")
    ServerResponse getMendMaterialList(@RequestParam("houseId")String houseId);

    @RequestMapping(value = "app/repair/mendOrder/saveMendMaterial")
    @ApiOperation(value = "保存补材料", notes = "保存补材料")
    ServerResponse saveMendMaterial(@RequestParam("userToken")String userToken,@RequestParam("houseId")String houseId
                    ,@RequestParam("productArr")String productArr);

}

