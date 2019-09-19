package com.dangjia.acg.api.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/17
 * Time: 14:15
 */
@Api(description = "材料精算接口")
@FeignClient("dangjia-service-goods")
public interface DjActuaryBudgetMaterialAPI {

    @PostMapping("/product/djActuaryBudgetMaterial/makeBudgets")
    @ApiOperation(value = "生成精算", notes = "生成精算")
    ServerResponse makeBudgets(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("actuarialTemplateId") String actuarialTemplateId,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("workerTypeId") String workerTypeId,
                               @RequestParam("listOfGoods") String listOfGoods);


    @PostMapping("/product/djActuaryBudgetMaterial/queryMakeBudgetsList")
    @ApiOperation(value = "查询精算列表", notes = "查询精算列表")
    ServerResponse queryMakeBudgetsList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("bclId") String bclId,
                                        @RequestParam("categoryId") String categoryId,
                                        @RequestParam("houseId") String houseId);


    @PostMapping("/product/djActuaryBudgetMaterial/queryBasicsProduct")
    @ApiOperation(value = "查询精算详情列表", notes = "查询精算详情列表")
    ServerResponse queryBasicsProduct(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("productId") String productId,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("cityId")String cityId,
                                      @RequestParam("categoryId")String categoryId,
                                      @RequestParam("name")String name,
                                      @RequestParam("attributeVal")String attributeVal,
                                      @RequestParam("brandVal")String brandVal,
                                      @RequestParam("orderKey")String orderKey);

}
