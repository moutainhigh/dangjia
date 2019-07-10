package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @创建时间： 2018-9-18下午3:53:07
 */
@Api(description = "材料精算")
@FeignClient("dangjia-service-goods")
public interface BudgetMaterialAPI {


    @PostMapping("/actuary/budgetMaterial/getHouseBudgetTotalAmount")
    @ApiOperation(value = "房子精算总花费统计", notes = "房子精算总花费统计")
    BigDecimal getHouseBudgetTotalAmount( @RequestParam("request") HttpServletRequest request,
                                          @RequestParam("houseId") String houseId);

    @PostMapping("/actuary/budgetMaterial/getHouseBudgetStageCost")
    @ApiOperation(value = "精算阶段花费统计", notes = "精算阶段花费统计")
    ServerResponse getHouseBudgetStageCost(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("houseId") String houseId,
            @RequestParam("workerTypeId") String workerTypeId);

    /**
     * 查询所有精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getAllBudgetMaterial")
    @ApiOperation(value = "查询所有精算", notes = "查询所有精算")
    ServerResponse getAllBudgetMaterial(@RequestParam("request") HttpServletRequest request);

    /**
     * 根据houseId和wokerTypeId查询房子材料精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getAllBudgetMaterialById")
    @ApiOperation(value = "根据houseId和wokerTypeId查询房子精算", notes = "根据houseId和wokerTypeId查询房子精算")
    ServerResponse getAllBudgetMaterialById(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("workerTypeId") String workerTypeId);


    @PostMapping("/actuary/budgetMaterial/queryBudgetMaterialByHouseFlowId")
    @ApiOperation(value = "根据HouseFlowId查询房子材料精算", notes = "根据HouseFlowId查询房子材料精算")
    ServerResponse queryBudgetMaterialByHouseFlowId(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("houseFlowId") String houseFlowId);

    /**
     * 根据id查询精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getBudgetMaterialById")
    @ApiOperation(value = "根据id查询精算", notes = "根据id查询精算")
    ServerResponse getBudgetMaterialById(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("id") String id);

    /**
     * 根据类别Id查询所属商品
     *
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getAllGoodsByCategoryId")
    @ApiOperation(value = "根据类别Id查询所属商品", notes = "根据类别Id查询所属商品")
    ServerResponse getAllGoodsByCategoryId(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("categoryId") String categoryId);

    /**
     * 根据商品Id查询货品
     *
     * @param goodsId
     * @return
     */
    @PostMapping("/actuary/budgetMaterial/getAllProductByGoodsId")
    @ApiOperation(value = "根据商品Id查询货品", notes = "根据商品Id查询货品")
    ServerResponse getAllProductByGoodsId(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("goodsId") String goodsId);


}
