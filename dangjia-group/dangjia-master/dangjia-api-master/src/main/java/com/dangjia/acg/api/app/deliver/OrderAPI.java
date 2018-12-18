package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:55
 */
@FeignClient("dangjia-service-master")
@Api(value = "要货管理类", description = "要货管理类")
public interface OrderAPI {

    @PostMapping("app/deliver/order/confirmOrderSplit")
    @ApiOperation(value = "管家确认要货", notes = "管家确认要货")
    ServerResponse confirmOrderSplit(@RequestParam("houseId")String houseId, @RequestParam("userToken")String userToken);

    @PostMapping("app/deliver/order/getOrderItemList")
    @ApiOperation(value = "已添加要货单明细", notes = "已添加要货单明细")
    ServerResponse getOrderItemList(@RequestParam("houseId")String houseId);

    @PostMapping("app/deliver/order/saveOrderSplit")
    @ApiOperation(value = "提交到要货", notes = "提交到要货")
    ServerResponse saveOrderSplit(@RequestParam("productArr")String productArr, @RequestParam("houseId")String houseId,
                                  @RequestParam("userToken")String userToken);

    @PostMapping("app/deliver/order/warehouseList")
    @ApiOperation(value = "模糊搜仓库", notes = "模糊搜仓库")
    ServerResponse warehouseList(@RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize") Integer pageSize,@RequestParam("houseId")String houseId,
                                 @RequestParam("categoryId")String categoryId, @RequestParam("name")String name);

}
