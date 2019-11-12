package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.dto.supplier.SupplierLikeDTO;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.supplier.DjSupplierServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public ServerResponse myWallet(String userId, String cityId) {
        return djSupplierServices.myWallet(userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierWithdrawal(String userId, String cityId, String bankCard, Double surplusMoney, String payPassword) {
        return djSupplierServices.supplierWithdrawal(userId,cityId, bankCard, surplusMoney, payPassword);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierRecharge(String userId, String cityId,String payState, Double rechargeAmount, String payPassword,
                                           String businessOrderType, Integer sourceType) {
        return djSupplierServices.supplierRecharge(userId,cityId,payPassword,rechargeAmount,payPassword,businessOrderType, sourceType);
    }

    @Override
    @ApiMethod
    public ServerResponse queryIncomeRecord(String userId, String cityId) {
        return djSupplierServices.queryIncomeRecord(userId, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryIncomeRecordDetail(String userId, String cityId,String merge) {
        return djSupplierServices.queryIncomeRecordDetail(userId,cityId,merge);
    }

    @Override
    @ApiMethod
    public ServerResponse queryExpenditure(String userId, String cityId) {
        return djSupplierServices.queryExpenditure(userId,cityId);
    }

    @Override
    @ApiMethod
    public List<SupplierLikeDTO> queryLikeSupplier(String searchKey) {
        return djSupplierServices.queryLikeSupplier(searchKey);
    }

}
