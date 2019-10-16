package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjSupplier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:03
 */
@Api(description = "供应商管理接口")
@FeignClient("dangjia-service-store")
public interface DjSupplierAPI {

    @PostMapping("/supplier/djSupplier/updateBasicInformation")
    @ApiOperation(value = "供应商基础信息维护", notes = "供应商基础信息维护")
    ServerResponse updateBasicInformation(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("djSupplier") DjSupplier djSupplier);

    @PostMapping("/supplier/djSupplier/selectSupplyList")
    @ApiOperation(value = "选择供货列表", notes = "选择供货列表")
    ServerResponse querySupplyList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("supId") String supId,
                                   @RequestParam("searchKey") String searchKey);


    @PostMapping("/supplier/djSupplier/querySupplierGoods")
    @ApiOperation(value = "供应商商品列表", notes = "供应商商品列表")
    ServerResponse querySupplierGoods(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("supId") String supId);

    @PostMapping("/sup/djSupApplication/queryDjSupplierByShopIdPage")
    @ApiOperation(value = "店铺-审核供应商列表(分页)", notes = "店铺-审核供应商列表(分页)")
    ServerResponse queryDjSupplierByShopIdPage(@RequestParam("pageDTO") PageDTO pageDTO,
                                                 @RequestParam("keyWord") String keyWord,
                                                 @RequestParam("applicationStatus") String applicationStatus,
                                                 @RequestParam("shopId") String shopId);

    @PostMapping("/sup/djSupApplication/queryDjSupApplicationByShopID")
    @ApiOperation(value = "店铺-审核供应商列表", notes = "店铺-审核供应商列表")
    ServerResponse queryDjSupplierByShopID(@RequestParam("keyWord") String keyWord,
                                           @RequestParam("applicationStatus") String applicationStatus,
                                           @RequestParam("shopId") String shopId);

    @PostMapping("/sup/getDjSupplierByID")
    @ApiOperation(value = "店铺-审核供应商-查看单个详情", notes = "店铺-审核供应商-查看详情")
    ServerResponse getDjSupplierByID(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id,@RequestParam("shopId") String shopId);

    @PostMapping("/sup/setDjSupplierPass")
    @ApiOperation(value = "店铺-审核供应商-通过", notes = "店铺-审核供应商-通过")
    ServerResponse setDjSupplierPass(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id,
                                     @RequestParam("applicationStatus") String applicationStatus);

    @PostMapping("/sup/setDjSupplierReject")
    @ApiOperation(value = "店铺-审核供应商-驳回", notes = "店铺-审核供应商-驳回")
    ServerResponse setDjSupplierReject(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("id") String id,
                                       @RequestParam("applicationStatus") String applicationStatus,
                                       @RequestParam("failReason") String failReason);


}
