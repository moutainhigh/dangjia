package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliveryReturnSlipAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliveryReturnSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:37
 */
@RestController
public class DjDeliveryReturnSlipController implements DjDeliveryReturnSlipAPI {

    @Autowired
    private DjDeliveryReturnSlipService djDeliveryReturnSlipService;

    @Override
    @ApiMethod
    public ServerResponse querySupplyTaskList(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey, Integer invoiceStatus) {
        return djDeliveryReturnSlipService.querySupplyTaskList(pageDTO, userId, cityId, searchKey, invoiceStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRefundSupplyTaskList(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey, Integer invoiceStatus) {
        return djDeliveryReturnSlipService.queryRefundSupplyTaskList(pageDTO, userId, cityId, searchKey, invoiceStatus);
    }


    @Override
    @ApiMethod
    public ServerResponse setDeliveryTask(HttpServletRequest request, String id, Integer invoiceType, Integer shippingState, String jsonStr, String reasons) {
        return djDeliveryReturnSlipService.setDeliveryTask(id, invoiceType, shippingState, jsonStr, reasons);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierSettlementManagement(HttpServletRequest request, String userId, String cityId, PageDTO pageDTO, Integer applyState, String searchKey) {
        return djDeliveryReturnSlipService.querySupplierSettlementManagement(userId,cityId,pageDTO,applyState,searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierSettlementList(HttpServletRequest request, String supId, String shopId, Integer applyState) {
        return djDeliveryReturnSlipService.querySupplierSettlementList(supId, shopId, applyState);
    }

    @Override
    @ApiMethod
    public ServerResponse queryBuyersDimensionList(HttpServletRequest request, PageDTO pageDTO,String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.queryBuyersDimensionList(pageDTO, userId,cityId, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse queryBuyersDimensionDetailList(HttpServletRequest request, PageDTO pageDTO, String supId, String houseId, String searchKey, String cityId) {
        return djDeliveryReturnSlipService.queryBuyersDimensionDetailList(pageDTO, supId, houseId, searchKey, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplyDimensionList(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.querySupplyDimensionList(pageDTO,userId,cityId,searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierStoreDimensionList(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.querySupplierStoreDimensionList(pageDTO, userId,cityId, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierStoreDimensionDetailList(HttpServletRequest request, PageDTO pageDTO,String supId, String shopId, String searchKey, String cityId) {
        return djDeliveryReturnSlipService.querySupplierStoreDimensionDetailList(pageDTO,supId,shopId,searchKey,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierDimension(HttpServletRequest request, PageDTO pageDTO, Date startTime, Date endTime, String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.supplierDimension(pageDTO,startTime,endTime,userId,cityId,searchKey);
    }


    @Override
    @ApiMethod
    public ServerResponse supplierDimensionSupplyDetails(HttpServletRequest request, PageDTO pageDTO, String storefrontId, String supId, String searchKey) {
        return djDeliveryReturnSlipService.supplierDimensionSupplyDetails(request,pageDTO,storefrontId,supId,searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierDimensionOrderDetails(HttpServletRequest request,PageDTO pageDTO,  String storefrontId, String supId,String addressId,String houseId) {
        return djDeliveryReturnSlipService.supplierDimensionOrderDetails(request,pageDTO,storefrontId,supId,addressId,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse storefrontProductDimensionDetail(HttpServletRequest request, PageDTO pageDTO,String storefrontId,String productId,Integer type) {
        return djDeliveryReturnSlipService.storefrontProductDimensionDetail(request,pageDTO, storefrontId, productId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse storefrontProductDimension(HttpServletRequest request, PageDTO pageDTO,String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.storefrontProductDimension(pageDTO,userId,cityId,searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse sellerDimension(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey) {
        return djDeliveryReturnSlipService.sellerDimension(pageDTO,userId,cityId,searchKey);
    }


    @Override
    @ApiMethod
    public ServerResponse shippingDetails(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId,String orderSplitId) {
        return djDeliveryReturnSlipService.shippingDetails(pageDTO,userId,cityId,orderSplitId);
    }

    @Override
    @ApiMethod
    public ServerResponse sellerSplitDeliverDetails(HttpServletRequest request, PageDTO pageDTO,String userId, String cityId, String splitDeliverId) {
        return djDeliveryReturnSlipService.sellerSplitDeliverDetails(request,pageDTO, userId,  cityId,splitDeliverId);
    }
}
