package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2018/12/12 0012
 * Time: 19:00
 */
@Api(description = "工程资料")
@FeignClient("dangjia-service-goods")
public interface HouseDataAPI {

    @PostMapping("/actuary/houseData/workerGoodsAll")
    @ApiOperation(value = "工匠工艺查询商品库人工", notes = "工匠工艺查询商品库人工")
    ServerResponse workerGoodsAll(@RequestParam("cityId")String cityId,@RequestParam("workerTypeId")String workerTypeId,@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/actuary/houseData/selfBuyingList")
    @ApiOperation(value = "自购清单", notes = "自购清单")
    ServerResponse selfBuyingList(@RequestParam("houseId")String houseId);

    @PostMapping("/actuary/houseData/workerGoodsDetail")
    @ApiOperation(value = "人工详情", notes = "人工详情")
    ServerResponse workerGoodsDetail(@RequestParam("cityId") String cityId,@RequestParam("workerGoodsId") String workerGoodsId);

    @PostMapping("/actuary/houseData/getBudgetWorker")
    @ApiOperation(value = "查询工序人工", notes = "查询工序人工")
    ServerResponse getBudgetWorker(@RequestParam("cityId") String cityId, @RequestParam("houseId")String houseId,
                                   @RequestParam("workerTypeId")String workerTypeId,@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/actuary/houseData/goodsDetail")
    @ApiOperation(value = "材料详情", notes = "材料详情")
    ServerResponse goodsDetail(@RequestParam("cityId")String cityId,@RequestParam("productId")String productId);

    @PostMapping("/actuary/houseData/getBudgetMaterial")
    @ApiOperation(value = "查询工序材料", notes = "查询工序材料")
    ServerResponse getBudgetMaterial(@RequestParam("cityId")String cityId,@RequestParam("houseId")String houseId,
                                     @RequestParam("workerTypeId")String workerTypeId,@RequestParam("pageDTO") PageDTO pageDTO);

    @GetMapping("/actuary/houseData/export/actuary/exportActuaryTotal")
    @ApiOperation(value = "导出精算汇总表", notes = "导出精算汇总表", produces = "*/*,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/octet-stream")
    ServerResponse exportActuaryTotal(@RequestParam("response")HttpServletResponse response,@RequestParam("houseId")String houseId);

}
