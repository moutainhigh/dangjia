package com.dangjia.acg.controller.storefront;


import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;


import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.storefront.StorefrontService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
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
    private static Logger logger = LoggerFactory.getLogger(StorefrontController.class);

    @Autowired
    private StorefrontService storefrontService;

    /**
     * 获取需缴纳的滞留金
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param type 类型
     * @return 类型：1店铺，2供应商
     */
    @Override
    @ApiMethod
    public ServerResponse getNeedRetentionMoney(String userId,String cityId,Integer type){
        return storefrontService.getNeedRetentionMoney(userId, cityId,type);
    }

    /**
     * 获取当前滞留金信息
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param type 类型
     * @return 类型：1店铺，2供应商
     */
    @Override
    @ApiMethod
    public ServerResponse getRetentionMoneyInfo(String userId,String cityId,Integer type){
        return storefrontService.getRetentionMoneyInfo(userId, cityId,type);
    }

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
        try {
            return storefrontService.updateStorefront(storefrontDTO);
        } catch (Exception e) {
          logger.error("修改失败：", e);
          return ServerResponse.createByErrorMessage("修改失败");
        }

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
    public ServerResponse queryStorefrontWallet( String userId, String cityId) {
        return storefrontService.queryStorefrontWallet(userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse operationStorefrontReflect(String userId, String cityId, String bankCard, Double surplusMoney, String payPassword) {
       try{
           return storefrontService.operationStorefrontReflect( userId,  cityId,  bankCard,  surplusMoney,  payPassword) ;
       } catch (Exception e) {
           logger.error("店铺提现异常：", e);
           return ServerResponse.createByErrorMessage("店铺提现异常");
       }
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

    @Override
    @ApiMethod
    public Integer setStorefrontSurplusMoney() {
        return storefrontService.setStorefrontSurplusMoney();
    }

    /**
     * 根据城市Id查询当家虚拟店铺
     * @param cityId 在市ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryWorkerShopByCityId(String cityId){
        return storefrontService.queryWorkerShopByCityId(cityId);
    }

    /**
     * 修改当家虚拟店铺信息
     * @param storefrontDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editWorkerShopInfo(StorefrontDTO storefrontDTO){
        return storefrontService.editWorkerShopInfo(storefrontDTO);
    }


}
