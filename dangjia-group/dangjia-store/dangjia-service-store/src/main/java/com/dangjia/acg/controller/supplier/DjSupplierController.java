package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.supplier.DjSupplierServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:19
 */
@RestController
public class DjSupplierController implements DjSupplierAPI {

    @Autowired
    private DjSupplierServices djSupplierServices;


    @Override
    @ApiMethod
    public DjSupplier queryDjSupplierById(String supplierId) {
        return djSupplierServices.queryDjSupplierById(supplierId);
    }

    @Override
    @ApiMethod
    public DjSupplier queryDjSupplierByPass(String supplierId) {
        return djSupplierServices.queryDjSupplierByPass(supplierId);
    }

    @Override
    @ApiMethod
    public DjSupplier querySingleDjSupplier(String userId, String cityId) {
        return djSupplierServices.querySingleDjSupplier(userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySingleDjSupplierDetail(String userId, String cityId) {
        return djSupplierServices.querySingleDjSupplierDetail(userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateBasicInformation(HttpServletRequest request, DjSupplier djSupplier) {
        return djSupplierServices.updateBasicInformation(djSupplier);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplyList(HttpServletRequest request, PageDTO pageDTO, String supId, String searchKey) {
        return djSupplierServices.querySupplyList(pageDTO, supId, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierGoods(HttpServletRequest request, PageDTO pageDTO, String supId) {
        return djSupplierServices.querySupplierGoods(pageDTO,supId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjSupplierByShopIdPage(PageDTO pageDTO, String keyWord, String applicationStatus, String userId,String cityId) {
        return djSupplierServices.queryDjSupplierByShopIdPage(pageDTO,keyWord,applicationStatus,userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjSupplierByShopID(String keyWord, String applicationStatus, String shopId,String cityId) {
        return djSupplierServices.queryDjSupplierByShopID(keyWord,applicationStatus,shopId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse getDjSupplierByID(String id,String shopId,String cityId) {
        return djSupplierServices.getDjSupplierByID(id,shopId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse setDjSupplierPass(String id, String applicationStatus,String cityId) {
        return djSupplierServices.setDjSupplierPass(id,applicationStatus,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse setDjSupplierReject( String id, String applicationStatus, String failReason,String cityId) {
        return djSupplierServices.setDjSupplierReject(id,applicationStatus,failReason,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse myWallet(String supId) {
        return djSupplierServices.myWallet(supId);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierWithdrawal(String supId, String bankCard, Double surplusMoney, String payPassword) {
        return djSupplierServices.supplierWithdrawal(supId, bankCard, surplusMoney, payPassword);
    }

    @Override
    @ApiMethod
    public ServerResponse SupplierRecharge(String supId,String payState, Double rechargeAmount, String payPassword, String businessOrderType, String userId) {
        return djSupplierServices.SupplierRecharge(supId,payPassword,rechargeAmount,payPassword,businessOrderType, userId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryIncomeRecord(String supId) {
        return djSupplierServices.queryIncomeRecord(supId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryIncomeRecordDetail(String supId,String merge) {
        return djSupplierServices.queryIncomeRecordDetail(supId,merge);
    }
}
