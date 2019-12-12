package com.dangjia.acg.api.data;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseListDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "提供精算数据接口", description = "提供精算数据接口")
public interface ActuaryAPI {

    @PostMapping("/data/actuary/getActuaryBudgetOk")
    @ApiOperation(value = "返回精算列表", notes = "返回精算列表")
    ServerResponse getActuaryBudgetOk(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name,
            @RequestParam("budgetOk") String budgetOk,
            @RequestParam("workerKey") String workerKey,
            @RequestParam("userId") String userId,
            @RequestParam("budgetStatus") String budgetStatus,
            @RequestParam("decorationType") String decorationType
    );

    /**
     * 查询精算的订单详情
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @return
     */
    @PostMapping("/data/actuary/getBudgetOrderDetail")
    @ApiOperation(value = "查询精算的订单详情", notes = "查询精算的订单详情")
    ServerResponse getBudgetOrderDetail(@RequestParam("cityId") String cityId,
                                        @RequestParam("houseId") String houseId);

    @PostMapping("/data/actuary/searchActuarialProductList")
    @ApiOperation(value = "精算接口--查询默认配置的设计商品", notes = "查询设计精算阶段配置列表")
    ServerResponse searchActuarialProductList(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("cityId") String cityId);

    @PostMapping("/data/actuary/saveRecommendedGoods")
    @ApiOperation(value = "精算接口--保存推荐的设计商品", notes = "精算接口--保存推荐的设计商品")
    ServerResponse saveRecommendedGoods(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("houseId") String houseId,
                                        @RequestParam("productStr") String productStr);


    @PostMapping("/data/actuary/getActuaryWaitPay")
    @ApiOperation(value = "返回待业主支付精算列表", notes = "返回待业主支付精算列表")
    ServerResponse getActuaryWaitPay(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name,
            @RequestParam("workerKey") String workerKey

    );

    @PostMapping("/data/actuary/getActuaryCommit")
    @ApiOperation(value = "返回待提交精算列表", notes = "返回待提交精算列表")
    ServerResponse getActuaryCommit(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name,
            @RequestParam("workerKey") String workerKey
    );

    @PostMapping("/data/actuary/getActuaryConfirm")
    @ApiOperation(value = "返回待业主确认精算列表", notes = "返回待业主确认精算列表")
    ServerResponse getActuaryConfirm(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name,
            @RequestParam("workerKey") String workerKey
    );

    @PostMapping("/data/actuary/getActuaryComplete")
    @ApiOperation(value = "返回已完成精算列表", notes = "返回已完成精算列表")
    ServerResponse getActuaryComplete(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name,
            @RequestParam("workerKey") String workerKey
    );

    @PostMapping("/data/actuary/getStatistics")
    @ApiOperation(value = "返回统计列表", notes = "返回统计列表")
    ServerResponse getStatistics(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("name") String name
    );

    @PostMapping("/data/actuary/getStatisticsByDate")
    @ApiOperation(value = "返回按日期统计列表", notes = "返回按日期统计列表")
    ServerResponse getStatisticsByDate(@RequestParam("startDate") String startDate,
                                       @RequestParam("endDate") String endDate);
}
