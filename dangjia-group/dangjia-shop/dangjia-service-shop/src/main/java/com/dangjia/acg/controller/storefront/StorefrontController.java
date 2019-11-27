package com.dangjia.acg.controller.storefront;


import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;


import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.storefront.StorefrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName: StorefrontController
 * @Description: 店铺管理接口类
 * @author: chenyufeng
 * @date: 2018-10-08
 */
@RestController
public class StorefrontController implements BasicsStorefrontAPI {

    @Autowired
    private StorefrontService storefrontService;

    @Override
    @ApiMethod
    public Storefront queryStorefrontByUserID(String userId, String cityId) {
        return storefrontService.queryStorefrontByUserID(userId, cityId);
    }

    @Override
    @ApiMethod
    public Storefront querySingleStorefrontById(String id) {
        return storefrontService.querySingleStorefrontById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontByUserId(String userId, String cityId) {
        return storefrontService.queryStorefrontByUserId(userId, cityId);
    }

    @Override
    @ApiMethod
    public List<Storefront> queryLikeSingleStorefront(String searchKey) {
        return storefrontService.queryLikeSingleStorefront(searchKey);
    }


    @Override
    @ApiMethod
    public ServerResponse updateStorefront(StorefrontDTO storefrontDTO) {
        return storefrontService.updateStorefront(storefrontDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierApplicationShopList(HttpServletRequest request, PageDTO pageDTO, String searchKey, String applicationStatus, String userId, String cityId) {
        return storefrontService.querySupplierApplicationShopList(pageDTO, searchKey, applicationStatus, userId, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierSelectionSupply(HttpServletRequest request, PageDTO pageDTO, String searchKey, String userId, String cityId) {
        return storefrontService.querySupplierSelectionSupply(pageDTO, searchKey, userId, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontWallet(HttpServletRequest request, PageDTO pageDTO, String searchKey, String userId, String cityId) {
        return storefrontService.queryStorefrontWallet(request,pageDTO,searchKey,userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse operationStorefrontReflect(String userId, String cityId, String bankCard, Double surplusMoney, String payPassword) {
        return storefrontService.operationStorefrontReflect( userId,  cityId,  bankCard,  surplusMoney,  payPassword) ;
    }

    @Override
    @ApiMethod
    public ServerResponse operationStorefrontRecharge(String userId, String cityId, String payState, Double rechargeAmount, String payPassword, String businessOrderType, Integer sourceType) {
        return storefrontService.operationStorefrontRecharge( userId,  cityId,  payState,  rechargeAmount,  payPassword,  businessOrderType,  sourceType) ;
    }

    @Override
    @ApiMethod
    public ServerResponse queryStoreSupplierSettlement(HttpServletRequest request, PageDTO pageDTO,
                                                       String userId, String cityId, String searchKey) {
        return storefrontService.queryStoreSupplierSettlement(pageDTO, userId, cityId, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse storeExpenseRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber) {
        return storefrontService.storeExpenseRecord(request,pageDTO,userId,cityId,orderNumber);
    }

    @Override
    @ApiMethod
    public ServerResponse storeExpenseRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId,String orderId) {
        return storefrontService.storeExpenseRecordOrderDetail(request,pageDTO,userId,cityId,orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse storeExpenseRecordGoodDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String deliverId) {
        return storefrontService.storeExpenseRecordGoodDetail(request,pageDTO,userId,cityId,deliverId);
    }


    @Override
    @ApiMethod
    public ServerResponse storeRevenueRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber) {
        return storefrontService.storeRevenueRecord(request,pageDTO,userId,cityId,orderNumber);
    }

    @Override
    @ApiMethod
    public ServerResponse storeRevenueRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber, Integer type) {
        return storefrontService.storeRevenueRecordOrderDetail(request,pageDTO,userId,cityId,orderNumber,type);
    }


}
