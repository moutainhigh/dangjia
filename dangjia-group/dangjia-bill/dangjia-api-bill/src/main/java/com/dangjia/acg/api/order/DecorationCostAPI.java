package com.dangjia.acg.api.order;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 4/11/2019
 * Time: 下午 4:53
 */
@Api(description = "装修花费")
@FeignClient("dangjia-service-bill")
public interface DecorationCostAPI {

    @PostMapping("/app/decoration/searchDecorationCostList")
    @ApiOperation(value = "查询当前花费列表", notes = "查询当前花费列表")
    ServerResponse searchDecorationCostList(@RequestParam("pageDTO") PageDTO pageDTO,
                                            @RequestParam("userToken") String userToken,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("labelValId") String labelValId);

    @PostMapping("/app/decoration/searchDecorationCostProductList")
    @ApiOperation(value = "查询当前花费列表商品信息", notes = "查询当前花费列表商品信息")
    ServerResponse searchDecorationCostProductList(@RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("labelValId") String labelValId,
                                            @RequestParam("categoryId") String categoryId);

    @PostMapping("/app/decoration/searchDecorationCategoryList")
    @ApiOperation(value = "查询分类标签汇总信息 ", notes = "查询分类标签汇总信息")
    ServerResponse searchDecorationCategoryLabelList(@RequestParam("userToken") String userToken,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId);

    @PostMapping("/app/decoration/editPurchasePrice")
    @ApiOperation(value = "录入自购商品价格 ", notes = "录入自购商品价格")
    ServerResponse editPurchasePrice(@RequestParam("userToken") String userToken,
                                     @RequestParam("cityId") String cityId,
                                     @RequestParam("actuaryBudgetId") String actuaryBudgetId,
                                     @RequestParam("shopCount") Double shopCount,
                                     @RequestParam("totalPrice") Double totalPrice,
                                     @RequestParam("housekeeperAcceptance") Integer housekeeperAcceptance);


    @PostMapping("/app/decoration/searchBudgetWorkerList")
    @ApiOperation(value = "精算--按工序查询精算", notes = "精算--按工序查询精算")
    ServerResponse searchBudgetWorkerList(@RequestParam("userToken") String userToken,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId);

    @PostMapping("/app/decoration/searchBudgetCategoryList")
    @ApiOperation(value = "精算--按类别查询精算", notes = "精算--按类别查询精算")
    ServerResponse searchBudgetCategoryList(@RequestParam("userToken") String userToken,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId);

    /**
     * 筛选条件查贸易
     * @param userToken
     * @param type 1按工序查，2按分类查
     * @return
     */
    @PostMapping("/app/decoration/selectScreeningConditions")
    @ApiOperation(value = "精算--查询筛选条件列表", notes = "精算--查询筛选条件列表")
    ServerResponse selectScreeningConditions(@RequestParam("userToken") String userToken,
                                          @RequestParam("type") Integer type);


    /**
     * 精算--分类标签汇总信息查询
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param categoryTopId 顶级分类ID
     * @return
     */
    @PostMapping("/app/decoration/searchBudgetCategoryLabelList")
    @ApiOperation(value = "精算--分类标签汇总信息查询 ", notes = "查询分类标签汇总信息")
    ServerResponse searchBudgetCategoryLabelList(@RequestParam("userToken") String userToken,
                                                 @RequestParam("cityId") String cityId,
                                                 @RequestParam("houseId") String houseId,
                                                 @RequestParam("workerTypeId") String workerTypeId,
                                                 @RequestParam("categoryTopId") String categoryTopId);

    /**
     * 精算--分类汇总信息，末级分类
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchTypeId 工种ID/顶级分类ID
     * @param labelValId 类别标签 ID
     * @return
     */
    @PostMapping("/app/decoration/searchBudgetLastCategoryList")
    @ApiOperation(value = "精算--分类汇总信息查询(末级分类） ", notes = "查询分类汇总信息（末级分类）")
    ServerResponse searchBudgetLastCategoryList(@RequestParam("userToken") String userToken,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                             @RequestParam("cityId") String cityId,
                                             @RequestParam("houseId") String houseId,
                                             @RequestParam("searchTypeId") String searchTypeId,
                                            @RequestParam("labelValId") String labelValId);

    @PostMapping("/app/decoration/searchBudgetProductList")
    @ApiOperation(value = "精算--查询商品信息 ", notes = "精算--查询商品信息")
    ServerResponse searchBudgetProductList(@RequestParam("userToken") String userToken,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("searchTypeId") String workerTypeId,
                                            @RequestParam("labelValId") String labelValId,
                                            @RequestParam("categoryId") String categoryId);

}
