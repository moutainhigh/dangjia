package com.dangjia.acg.api.actuary.app;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:31
 */
@Api(description = "我要装修--首页展示")
@FeignClient("dangjia-service-goods")
public interface SearchActuarialConfigAPI {


    @PostMapping("app/search/actuarialConfig/searchActuarialList")
    @ApiOperation(value = "我要装修--查询设计精算阶段配置列表", notes = "查询设计精算阶段配置列表")
    ServerResponse searchActuarialList(@RequestParam("request") HttpServletRequest request);

    @PostMapping("app/search/actuarialConfig/searchChangeProductList")
    @ApiOperation(value = "我要装修--查询设计精算可切换商品列表", notes = "查询设计精算可切换商品列表")
    ServerResponse searchChangeProductList(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("goodsId") String goodsId);

    @PostMapping("app/search/actuarialConfig/searchSimulationTitleList")
    @ApiOperation(value = "我要装修--模拟花费标题查询", notes = "模拟花费标题查询")
    ServerResponse searchSimulationTitleList(@RequestParam("request") HttpServletRequest request);

    @PostMapping("app/search/actuarialConfig/searchSimulationTitleDetailList")
    @ApiOperation(value = "我要装修--模拟花费标题详情列表查询", notes = "模拟花费标题详情列表查询")
    ServerResponse searchSimulationTitleDetailList(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("titleId") String titleId);



    @PostMapping("app/search/actuarialConfig/searchSimulateCostInfoList")
    @ApiOperation(value = "模拟花费--模拟花费花费详情展示", notes = "模拟花费花费详情展示")
    ServerResponse searchSimulateCostInfoList(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("groupCode") String groupCode);


}
