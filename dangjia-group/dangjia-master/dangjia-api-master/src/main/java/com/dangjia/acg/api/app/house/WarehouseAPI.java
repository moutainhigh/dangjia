package com.dangjia.acg.api.app.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "材料仓库统计", description = "材料仓库统计")
public interface WarehouseAPI {

    @PostMapping("app/house/warehouse/warehouseList")
    @ApiOperation(value = "我购买的材料", notes = "我购买的材料")
    ServerResponse warehouseList(@RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("categoryId") String categoryId,
                                 @RequestParam("name") String name,
                                 @RequestParam("type") Integer type);

    @PostMapping("app/house/warehouse/warehouseGmList")
    @ApiOperation(value = "我购买的材料", notes = "我购买的材料")
    ServerResponse warehouseGmList(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("name") String name,
                                 @RequestParam("type") Integer type);
}
