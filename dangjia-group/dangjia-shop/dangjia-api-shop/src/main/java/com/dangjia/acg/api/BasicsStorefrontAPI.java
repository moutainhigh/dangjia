package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * chenyufeng  2019-10-08  店铺管理
 */
@Api(description = "店铺管理接口")
@FeignClient("dangjia-service-shop")
public interface BasicsStorefrontAPI {

    @PostMapping("/web/queryStorefrontByUserID")
    @ApiOperation(value = "通过用户ID查询店铺", notes = "通过用户ID查询店铺")
    Storefront queryStorefrontByUserID(@RequestParam("userId") String userId,
                                       @RequestParam("cityId") String cityId);

    @PostMapping("/web/querySingleStorefrontById")
    @ApiOperation(value = "根据主键ID查询店铺", notes = "根据主键ID查询店铺")
    Storefront querySingleStorefrontById(@RequestParam("id") String id);

    @PostMapping("/web/queryStorefrontByUserId")
    @ApiOperation(value = "根据userId查询店铺信息", notes = "根据userId查询店铺信息")
    ServerResponse queryStorefrontByUserId(@RequestParam("userId") String userId,@RequestParam("cityId") String cityId
    );

       @PostMapping("/web/queryLikeSingleStorefront")
    @ApiOperation(value = "根据条件模糊查询店铺信息", notes = "根据条件模糊查询店铺信息")
    List<Storefront> queryLikeSingleStorefront(@RequestParam("searchKey") String searchKey);

    @PostMapping("/web/addStorefront")
    @ApiOperation(value = "注册店铺信息", notes = "注册店铺信息")
    ServerResponse addStorefront(@RequestParam("userId") String userId,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("storefrontName") String storefrontName,
                                 @RequestParam("storefrontAddress") String storefrontAddress,
                                 @RequestParam("storefrontDesc") String storefrontDesc,
                                 @RequestParam("storefrontLogo") String storefrontLogo,
                                 @RequestParam("storekeeperName") String storekeeperName,
                                 @RequestParam("mobile") String mobile,
                                 @RequestParam("email") String email
                                 );

    @PostMapping("/web/updateStorefront")
    @ApiOperation(value = "修改店铺信息", notes = "修改店铺信息")
    ServerResponse updateStorefront(StorefrontDTO storefrontDTO);


    @PostMapping("/web/querySupplierApplicationShopList")
    @ApiOperation(value = "查询供应商申请店铺列表", notes = "供应商申请店铺列表")
    ServerResponse querySupplierApplicationShopList(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                                    @RequestParam("searchKey") String searchKey,
                                                    @RequestParam("applicationStatus") String applicationStatus,
                                                    @RequestParam("userId") String userId,
                                                    @RequestParam("cityId") String cityId);


    @PostMapping("/web/querySupplierSelectionSupply")
    @ApiOperation(value = "查询供应商供货列表", notes = "查询供应商供货列表")
    ServerResponse querySupplierSelectionSupply(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("searchKey") String searchKey,
                                                @RequestParam("userId") String userId,
                                                @RequestParam("cityId") String cityId);


    /**
     * 我的钱包
     */
    @PostMapping("/web/queryStorefrontWallet")
    @ApiOperation(value = "店铺-我的钱包", notes = "店铺-我的钱包")
    ServerResponse queryStorefrontWallet(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("searchKey") String searchKey,
                                                @RequestParam("userId") String userId,
                                                @RequestParam("cityId") String cityId);



    /**
     * 体现
     */
    @PostMapping("/web/queryStorefrontReflect")
    @ApiOperation(value = "店铺-店铺体现", notes = "店铺-店铺体现")
    ServerResponse operationStorefrontReflect(@RequestParam("userId") String userId,
                                              @RequestParam("cityId") String cityId,
                                              @RequestParam("bankCard") String bankCard,
                                              @RequestParam("surplusMoney") Double surplusMoney,
                                              @RequestParam("payPassword") String payPassword);
    /**
     * 1.店铺充值
     * 2.缴纳滞留金
     */
    @PostMapping("/web/queryStorefrontRecharge")
    @ApiOperation(value = "店铺-店铺充值", notes = "店铺-店铺充值")
    ServerResponse operationStorefrontRecharge(
            @RequestParam("userId") String userId,
            @RequestParam("cityId") String cityId,
            @RequestParam("payState") String payState,
            @RequestParam("rechargeAmount") Double rechargeAmount,
            @RequestParam("payPassword") String payPassword,
            @RequestParam("businessOrderType") String businessOrderType,
            @RequestParam("sourceType") Integer sourceType);



    /**
     * 店铺财务-供应商结算
     */
    @PostMapping("/web/queryStoreSupplierSettlement")
    @ApiOperation(value = "店铺财务-供应商结算", notes = "店铺财务-供应商结算")
    ServerResponse queryStoreSupplierSettlement(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("userId") String userId,
                                                @RequestParam("cityId") String cityId,
                                                @RequestParam("searchKey") String searchKey);


    /**
     * 店铺-收入记录
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param orderNumber
     * @return
     */
    @PostMapping("/web/storeExpenseRecord")
    @ApiOperation(value = "店铺-收入记录", notes = "店铺-收入记录")
    ServerResponse storeExpenseRecord(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("userId") String userId,
                                                @RequestParam("cityId") String cityId,
                                                @RequestParam("orderNumber") String orderNumber);

    /**
     * 店铺-收入记录-订单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    @PostMapping("/web/storeExpenseRecordOrderDetail")
    @ApiOperation(value = "店铺-收入记录-订单详情", notes = "店铺-收入记录-订单详情")
    ServerResponse storeExpenseRecordOrderDetail(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("userId") String userId,
                                      @RequestParam("cityId") String cityId,
                                       @RequestParam("orderId") String orderId
                                     );

    /**
     * 店铺-收入记录-查看清单
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param deliverId
     * @return
     */
    @PostMapping("/web/storeExpenseRecordGoodDetail")
    @ApiOperation(value = "店铺-收入记录-查看清单", notes = "店铺-收入记录-查看清单")
    ServerResponse storeExpenseRecordGoodDetail(@RequestParam("request") HttpServletRequest request,
                      @RequestParam("pageDTO") PageDTO pageDTO,
                      @RequestParam("userId") String userId,
                      @RequestParam("cityId") String cityId,
                      @RequestParam("deliverId") String deliverId);


    /**
     * 店铺-支出记录
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param orderNumber
     * @return
     */
    @PostMapping("/web/storeRevenueRecord")
    @ApiOperation(value = "店铺-支出记录", notes = "店铺-支出记录")
    ServerResponse storeRevenueRecord(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("userId") String userId,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("orderNumber") String orderNumber);

    /**
     * 店铺-支出记录-查看货单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param type
     * @param orderNumber
     * @return
     */
    @PostMapping("/web/storeRevenueRecordOrderDetail")
    @ApiOperation(value = "店铺-支出记录-查看货单详情", notes = "店铺-支出记录-查看货单详情")
    ServerResponse storeRevenueRecordOrderDetail(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("userId") String userId,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("orderNumber") String orderNumber,
                                      @RequestParam("type") Integer type);

}
