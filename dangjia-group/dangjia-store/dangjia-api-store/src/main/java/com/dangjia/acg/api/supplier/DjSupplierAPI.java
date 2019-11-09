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

    @PostMapping("/supplier/djSupplier/queryDjSupplierById")
    @ApiOperation(value = "根据主键ID查询供应商信息", notes = "根据主键ID查询供应商信息")
    DjSupplier queryDjSupplierById(@RequestParam("supplierId") String supplierId);

    @PostMapping("/supplier/djSupplier/queryDjSupplierByPass")
    @ApiOperation(value = "根据主键ID查询审核通过的供应商信息", notes = "根据主键ID查询审核通过的供应商信息")
    DjSupplier queryDjSupplierByPass(@RequestParam("supplierId") String supplierId);

    @PostMapping("/supplier/djSupplier/querySingleDjSupplier")
    @ApiOperation(value = "根据userId查询供应商信息", notes = "根据userId查询供应商信息")
    DjSupplier querySingleDjSupplier(@RequestParam("userId") String userId,
                                     @RequestParam("cityId") String cityId);

    @PostMapping("/supplier/djSupplier/querySingleDjSupplierDetail")
    @ApiOperation(value = "根据userId查询供应商信息", notes = "根据userId查询供应商信息")
    ServerResponse querySingleDjSupplierDetail(@RequestParam("userId") String userId,
                                               @RequestParam("cityId") String cityId);

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
                                               @RequestParam("userId") String userId,@RequestParam("cityId") String cityId);

    @PostMapping("/sup/djSupApplication/queryDjSupplierByShopID")
    @ApiOperation(value = "店铺-审核供应商列表", notes = "店铺-审核供应商列表")
    ServerResponse queryDjSupplierByShopID(@RequestParam("keyWord") String keyWord,
                                           @RequestParam("applicationStatus") String applicationStatus,
                                           @RequestParam("shopId") String shopId,@RequestParam("cityId") String cityId);

    @PostMapping("/sup/getDjSupplierByID")
    @ApiOperation(value = "店铺-审核供应商-查看供应商品列表", notes = "店铺-审核供应商-查看供应商品列表")
    ServerResponse getDjSupplierByID(@RequestParam("id") String id, @RequestParam("shopId") String shopId,@RequestParam("cityId") String cityId);

    @PostMapping("/sup/setDjSupplierPass")
    @ApiOperation(value = "店铺-审核供应商-通过", notes = "店铺-审核供应商-通过")
    ServerResponse setDjSupplierPass(@RequestParam("id") String id,
                                     @RequestParam("applicationStatus") String applicationStatus,@RequestParam("cityId") String cityId);

    @PostMapping("/sup/setDjSupplierReject")
    @ApiOperation(value = "店铺-审核供应商-驳回", notes = "店铺-审核供应商-驳回")
    ServerResponse setDjSupplierReject(@RequestParam("id") String id,
                                       @RequestParam("applicationStatus") String applicationStatus,
                                       @RequestParam("failReason") String failReason,@RequestParam("cityId") String cityId);

    @PostMapping("/sup/myWallet")
    @ApiOperation(value = "我的钱包", notes = "我的钱包")
    ServerResponse myWallet(@RequestParam("supId") String supId);

    @PostMapping("/sup/supplierWithdrawal")
    @ApiOperation(value = "供应商提现", notes = "供应商提现")
    ServerResponse supplierWithdrawal(@RequestParam("supId") String supId,
                                      @RequestParam("bankCard") String bankCard,
                                      @RequestParam("surplusMoney") Double surplusMoney,
                                      @RequestParam("payPassword") String payPassword);

    @PostMapping("/sup/SupplierRecharge")
    @ApiOperation(value = "供应商充值", notes = "供应商充值")
    ServerResponse SupplierRecharge(@RequestParam("supId") String supId,
                                    @RequestParam("payState") String payState,
                                    @RequestParam("rechargeAmount") Double rechargeAmount,
                                    @RequestParam("payPassword") String payPassword,
                                    @RequestParam("businessOrderType") String businessOrderType,
                                    @RequestParam("userId") String userId);

    @PostMapping("/sup/queryIncomeRecord")
    @ApiOperation(value = "供应商收入记录", notes = "供应商收入记录")
    ServerResponse queryIncomeRecord(@RequestParam("supId") String supId);

    @PostMapping("/sup/queryIncomeRecordDetail")
    @ApiOperation(value = "供应商收入记录详情", notes = "供应商收入记录详情")
    ServerResponse queryIncomeRecordDetail(@RequestParam("merge") String merge);
}
