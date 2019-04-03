package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.budget.BudgetItemDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/2/26 0026
 * Time: 10:44
 */
@Api(description = "新精算确认")
@FeignClient("dangjia-service-goods")
public interface ActuaryOpeAPI {

    @PostMapping("/actuary/actuaryOpe/getByCategoryId")
    @ApiOperation(value = "根据分类获取材料", notes = "根据分类获取材料")
    ServerResponse getByCategoryId(@RequestParam("idArr")String idArr, @RequestParam("houseId")String houseId,@RequestParam("cityId")String cityId,
                                   @RequestParam("type")Integer type);

    @PostMapping("/actuary/actuaryOpe/categoryIdList")
    @ApiOperation(value = "所有分类", notes = "所有分类")
    ServerResponse categoryIdList(@RequestParam("houseId")String houseId,@RequestParam("cityId")String cityId,
                                  @RequestParam("type")Integer type);

    @PostMapping("/actuary/actuaryOpe/actuary")
    @ApiOperation(value = "精算详情", notes = "精算详情")
    ServerResponse actuary(@RequestParam("houseId") String houseId,
                           @RequestParam("cityId")String cityId,
                           @RequestParam("type")Integer type);

    @PostMapping("/actuary/actuaryOpe/getHouseWorkerInfo")
    @ApiOperation(value = " 查看房子已购买的人工详细列表(内部使用)", notes = " 内部使用")
    List<BudgetItemDTO> getHouseWorkerInfo(
            @RequestParam("cityId")String cityId,
            @RequestParam("deleteState")String deleteState,
            @RequestParam("houseId")String houseId,
            @RequestParam("address")String address);

    @PostMapping("/actuary/actuaryOpe/getHouseWorkerPrice")
    @ApiOperation(value = " 房子人工总价(内部使用)", notes = " 内部使用")
    Double getHouseWorkerPrice(
            @RequestParam("cityId")String cityId,
            @RequestParam("deleteState")String deleteState,
            @RequestParam("houseId") String houseId);
}
