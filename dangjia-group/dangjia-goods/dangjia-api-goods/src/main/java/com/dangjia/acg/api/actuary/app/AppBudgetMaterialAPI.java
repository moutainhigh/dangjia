package com.dangjia.acg.api.actuary.app;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: chenyufeng
 * Date: 2019/10/31
 */
@Api(description = "材料精算操作类")
@FeignClient("dangjia-service-goods")
public interface AppBudgetMaterialAPI {

    /**
     * 查询精算审核（含自购）
     */
    @PostMapping("/app/actuary/queryActuarial")
    @ApiOperation(value = "查询精算审核（含自购）", notes = "查询精算审核（含自购）")
    ServerResponse queryActuarial(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("cityId") String cityId,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("productType") String productType);

    /**
     * 确认精算审核（含自购）l
     */
    @PostMapping("/app/actuary/conconfirmedActuarial")
    @ApiOperation(value = "确认精算审核（含自购）", notes = "确认精算审核（含自购）")
    ServerResponse conconfirmedActuarial(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("cityId") String cityId,
                                         @RequestParam("houseId") String houseId,
                                         @RequestParam("productType") String productType);

    /**
     *
     * @param request
     * @param cityId 城市id
     * @param houseId  房子id
     * @param workerTypeId 工种ID
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getHouseBudgetStageCost")
    @ApiOperation(value = "精算阶段花费统计", notes = "精算阶段花费统计")
    ServerResponse getHouseBudgetStageCost(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("houseId") String houseId,
            @RequestParam("workerTypeId") String workerTypeId);

}
