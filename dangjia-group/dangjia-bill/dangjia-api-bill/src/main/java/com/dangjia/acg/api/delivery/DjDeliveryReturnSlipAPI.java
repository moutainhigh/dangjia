package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:32
 */
@Api(description = "发货退货单接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliveryReturnSlipAPI {

    /*************************************供应商利润统计*************************************************/
    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplyTaskList")
    @ApiOperation(value = "供货任务列表", notes = "供货任务列表")
    ServerResponse querySupplyTaskList(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("userId") String userId,
                                       @RequestParam("cityId") String cityId,
                                       @RequestParam("searchKey") String searchKey,
                                       @RequestParam("invoiceStatus") Integer invoiceStatus);

    @PostMapping("/delivery/djDeliveryReturnSlip/queryRefundSupplyTaskList")
    @ApiOperation(value = "退货任务任务列表", notes = "退货任务任务列表")
    ServerResponse queryRefundSupplyTaskList(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("userId") String userId,
                                       @RequestParam("cityId") String cityId,
                                       @RequestParam("searchKey") String searchKey,
                                       @RequestParam("invoiceStatus") Integer invoiceStatus);

    @PostMapping("/delivery/djDeliveryReturnSlip/setDeliveryTask")
    @ApiOperation(value = "处理供货任务", notes = "处理供货任务")
    ServerResponse setDeliveryTask(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id,
                                   @RequestParam("invoiceType") Integer invoiceType,
                                   @RequestParam("shippingState") Integer shippingState,
                                   @RequestParam("jsonStr") String jsonStr,
                                   @RequestParam("reasons") String reasons);

    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplierSettlementManagement")
    @ApiOperation(value = "供应商结算管理", notes = "供应商结算管理")
    ServerResponse querySupplierSettlementManagement(@RequestParam("request") HttpServletRequest request,
                                                     @RequestParam("user_id") String userId,
                                                     @RequestParam("cityId") String cityId,
                                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                                     @RequestParam("applyState") Integer applyState,
                                                     @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplierSettlementList")
    @ApiOperation(value = "供应商结算列表", notes = "供应商结算列表")
    ServerResponse querySupplierSettlementList(@RequestParam("request") HttpServletRequest request,
                                               @RequestParam("supId") String supId,
                                               @RequestParam("shopId") String shopId,
                                               @RequestParam("applyState") Integer applyState);


    @PostMapping("/delivery/djDeliveryReturnSlip/queryBuyersDimensionList")
    @ApiOperation(value = "供应商买家维度列表", notes = "供应商结算买家维度列表")
    ServerResponse queryBuyersDimensionList(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("pageDTO") PageDTO pageDTO,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djDeliveryReturnSlip/queryBuyersDimensionDetailList")
    @ApiOperation(value = "供应商查看买家维度详情列表", notes = "供应商查看买家维度详情列表")
    ServerResponse queryBuyersDimensionDetailList(@RequestParam("request") HttpServletRequest request,
                                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                                  @RequestParam("supId") String supId,
                                                  @RequestParam("houseId") String houseId,
                                                  @RequestParam("searchKey") String searchKey,
                                                  @RequestParam("cityId") String cityId);

    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplyDimensionList")
    @ApiOperation(value = "供应商商品维度列表", notes = "供应商商品维度列表")
    ServerResponse querySupplyDimensionList(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("pageDTO") PageDTO pageDTO,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("cityId") String cityId,
                                            @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplierStoreDimensionList")
    @ApiOperation(value = "供应商店铺维度列表", notes = "供应商店铺维度列表")
    ServerResponse querySupplierStoreDimensionList(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("cityId") String cityId,
                                                   @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djDeliveryReturnSlip/querySupplierStoreDimensionDetailList")
    @ApiOperation(value = "供应商店铺维度详情列表", notes = "供应商店铺维度详情列表")
    ServerResponse querySupplierStoreDimensionDetailList(@RequestParam("request") HttpServletRequest request,
                                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                                         @RequestParam("supId") String supId,
                                                         @RequestParam("shopId") String shopId,
                                                         @RequestParam("searchKey") String searchKey,
                                                         @RequestParam("cityId") String cityId);
    /*************************************店铺利润统计*************************************************/
    @PostMapping("/delivery/djBasicsStorefrontProfit/supplierDimension")
    @ApiOperation(value = "店铺利润统计-供应商维度", notes = "店铺利润统计-供应商维度")
    ServerResponse supplierDimension(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                                   @RequestParam("startTime") Date startTime,
                                                   @RequestParam("endTime")Date endTime,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("cityId") String cityId,
                                                   @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djBasicsStorefrontProfit/supplierDimensionSupplyDetails")
    @ApiOperation(value = "店铺利润统计-供应商维度--供应详情", notes = "店铺利润统计-供应商维度--供应详情")
    ServerResponse supplierDimensionSupplyDetails(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("storefrontId") String storefrontId,
                                     @RequestParam("supId") String supId,
                                     @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djBasicsStorefrontProfit/supplierDimensionOrderDetails")
    @ApiOperation(value = "店铺利润统计--供应详情--货单详情", notes = "店铺利润统计--供应详情--货单详情")
    ServerResponse supplierDimensionOrderDetails(@RequestParam("request") HttpServletRequest request,
                                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                                  @RequestParam("storefrontId") String storefrontId,
                                                  @RequestParam("supId") String supId,
                                                  @RequestParam("addressId") String addressId,
                                                  @RequestParam("houseId") String houseId);


    @PostMapping("/delivery/djBasicsStorefrontProfit/storefrontProductDimensionDetail")
    @ApiOperation(value = "店铺利润统计-商品维度--货单详情", notes = "店铺利润统计-商品维度--货单详情")
    ServerResponse storefrontProductDimensionDetail(@RequestParam("request") HttpServletRequest request,
                                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                                  @RequestParam("storefrontId") String storefrontId,
                                                  @RequestParam("productId") String productId,
                                                  @RequestParam("type") Integer type);


    @PostMapping("/delivery/djBasicsStorefrontProfit/storefrontProductDimension")
    @ApiOperation(value = "店铺利润统计-商品维度", notes = "店铺利润统计-商品维度")
    ServerResponse storefrontProductDimension(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("cityId") String cityId,
                                                   @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djBasicsStorefrontProfit/sellerDimension")
    @ApiOperation(value = "店铺利润统计-买家维度列表", notes = "店铺利润统计-买家维度列表")
    ServerResponse sellerDimension(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("cityId") String cityId,
                                                   @RequestParam("searchKey") String searchKey);

    @PostMapping("/delivery/djBasicsStorefrontProfit/shippingDetails")
    @ApiOperation(value = "店铺利润统计-查看买家订单详情", notes = "店铺利润统计-查看买家订单详情")
    ServerResponse shippingDetails(
                     @RequestParam("request") HttpServletRequest request,
                     @RequestParam("pageDTO") PageDTO pageDTO,
                     @RequestParam("userId") String userId,  @RequestParam("cityId")String cityId,
                     @RequestParam("orderSplitId") String orderSplitId);

    @PostMapping("/delivery/djBasicsStorefrontProfit/sellerSplitDeliverDetails")
    @ApiOperation(value = "店铺利润统计-买家维度-货单列表", notes = "店铺利润统计-买家维度-货单列表")
    ServerResponse sellerSplitDeliverDetails(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("storefrontId") String storefrontId,
            @RequestParam("addressId") String addressId,
            @RequestParam("houseId") String houseId,
            @RequestParam("type") Integer type);

}
