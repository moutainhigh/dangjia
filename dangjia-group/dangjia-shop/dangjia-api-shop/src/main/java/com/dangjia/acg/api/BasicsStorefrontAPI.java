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
    @ApiOperation(value = "根据调件模糊查询店铺信息", notes = "根据调件模糊查询店铺信息")
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


}
