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

}
