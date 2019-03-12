package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.repair.MendOrderInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("dangjia-service-master")
@Api(value = "补退提交", description = "补退提交")
public interface MendOrderAPI {

    @PostMapping(value = "app/repair/mendOrder/confirmLandlordState")
    @ApiOperation(value = "业主确认退货", notes = "业主确认退货")
    ServerResponse confirmLandlordState(@RequestParam("houseId") String houseId);

    @PostMapping(value = "app/repair/mendOrder/landlordBackDetail")
    @ApiOperation(value = "业主已添加退货单明细", notes = "业主已添加退货单明细")
    ServerResponse landlordBackDetail(@RequestParam("houseId") String houseId);

    @PostMapping(value = "app/repair/mendOrder/landlordBack")
    @ApiOperation(value = "业主退材料", notes = "业主退材料")
    ServerResponse landlordBack(@RequestParam("userToken") String userToken,
                                @RequestParam("houseId") String houseId,
                                @RequestParam("productArr") String productArr);

    @PostMapping(value = "app/repair/mendOrder/confirmBackMendWorker")
    @ApiOperation(value = "确认退人工", notes = "确认退人工")
    ServerResponse confirmBackMendWorker(@RequestParam("houseId") String houseId,
                                         @RequestParam("workerTypeId") String workerTypeId);

    @PostMapping(value = "app/repair/mendOrder/backMendWorkerList")
    @ApiOperation(value = "退人工单明细", notes = "退人工单明细")
    ServerResponse backMendWorkerList(@RequestParam("houseId") String houseId,
                                      @RequestParam("workerTypeId") String workerTypeId);

    @PostMapping(value = "app/repair/mendOrder/backMendWorker")
    @ApiOperation(value = "提交退人工", notes = "提交退人工")
    ServerResponse backMendWorker(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("workerGoodsArr") String workerGoodsArr,
                                  @RequestParam("workerTypeId") String workerTypeId,
                                  @RequestParam("changeOrderId") String changeOrderId);

    @PostMapping(value = "app/repair/mendOrder/confirmMendWorker")
    @ApiOperation(value = "确认补人工", notes = "确认补人工")
    ServerResponse confirmMendWorker(@RequestParam("houseId") String houseId,
                                     @RequestParam("workerTypeId") String workerTypeId);

    @PostMapping(value = "app/repair/mendOrder/getMendWorkerList")
    @ApiOperation(value = "补退订单表单明细", notes = "补人工单明细")
    ServerResponse getMendWorkerList(@RequestParam("houseId") String houseId,
                                     @RequestParam("workerTypeId") String workerTypeId);


    @PostMapping(value = "app/repair/mendOrder/getMendDetail")
    @ApiOperation(value = "补明细(新)", notes = "补明细(新)")
    MendOrderInfoDTO getMendDetail(@RequestParam("workerTypeId") String workerTypeId,
                                          @RequestParam("type") String type);

    @PostMapping(value = "app/repair/mendOrder/getMendMendOrderInfo")
    @ApiOperation(value = "补退订单明细", notes = "补退订单明细")
    MendOrderInfoDTO getMendMendOrderInfo(@RequestParam("houseId") String houseId,
                                          @RequestParam("workerTypeId") String workerTypeId,
                                          @RequestParam("type") String type,
                                          @RequestParam("state") String state);


    @PostMapping(value = "app/repair/mendOrder/saveMendWorker")
    @ApiOperation(value = "保存补人工", notes = "保存补人工")
    ServerResponse saveMendWorker(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("workerGoodsArr") String workerGoodsArr,
                                  @RequestParam("workerTypeId") String workerTypeId,
                                  @RequestParam("changeOrderId") String changeOrderId);

    @PostMapping(value = "app/repair/mendOrder/confirmBackMendMaterial")
    @ApiOperation(value = "确认退货", notes = "确认退货")
    ServerResponse confirmBackMendMaterial(@RequestParam("userToken") String userToken,
                                           @RequestParam("houseId") String houseId,
                                           @RequestParam("imageArr") String imageArr);

    @PostMapping(value = "app/repair/mendOrder/backMendMaterialList")
    @ApiOperation(value = "退货单明细", notes = "退货单明细")
    ServerResponse backMendMaterialList(@RequestParam("userToken") String userToken,
                                        @RequestParam("houseId") String houseId);

    @PostMapping(value = "app/repair/mendOrder/backMendMaterial")
    @ApiOperation(value = "提交退货", notes = "提交退货")
    ServerResponse backMendMaterial(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("productArr") String productArr);

    @PostMapping(value = "app/repair/mendOrder/confirmMendMaterial")
    @ApiOperation(value = "确认补货", notes = "确认补货")
    ServerResponse confirmMendMaterial(@RequestParam("userToken") String userToken,
                                       @RequestParam("houseId") String houseId);

    @PostMapping(value = "app/repair/mendOrder/getMendMaterialList")
    @ApiOperation(value = "已添加补材料单明细", notes = "已添加补材料单明细")
    ServerResponse getMendMaterialList(@RequestParam("userToken") String userToken,
                                       @RequestParam("houseId") String houseId);

    @PostMapping(value = "app/repair/mendOrder/saveMendMaterial")
    @ApiOperation(value = "保存补材料", notes = "保存补材料")
    ServerResponse saveMendMaterial(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("productArr") String productArr);

}

