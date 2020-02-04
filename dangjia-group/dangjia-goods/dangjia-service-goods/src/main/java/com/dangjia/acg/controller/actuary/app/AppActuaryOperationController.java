package com.dangjia.acg.controller.actuary.app;

import com.dangjia.acg.api.actuary.app.AppActuaryOperationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.app.AppActuaryOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/09/18
 */
@RestController
public class AppActuaryOperationController implements AppActuaryOperationAPI {

    @Autowired
    private AppActuaryOperationService actuaryOperationService;


    @Override
    @ApiMethod
    public ServerResponse choiceGoods(String userToken,HttpServletRequest request, String houseId,String productId,String addedProductIds,String cityId) {
        return actuaryOperationService.choiceGoods(userToken,houseId, productId,addedProductIds,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse changeProduct(HttpServletRequest request, String productId,String addedProductIds, String budgetMaterialId,  String houseId, String workerTypeId) {
        return actuaryOperationService.changeProduct(productId, addedProductIds, budgetMaterialId, houseId, workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse recoveryProduct(HttpServletRequest request,String houseId, String productId,String workerTypeId,String storefontId,String labelId) {
        return actuaryOperationService.recoveryProduct( houseId, productId, workerTypeId,storefontId,labelId);
    }
    @Override
    @ApiMethod
    public ServerResponse selectProduct(HttpServletRequest request, String goodsId, String selectVal, String budgetMaterialId) {
        return actuaryOperationService.selectProduct(goodsId,  selectVal,  budgetMaterialId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCommo(HttpServletRequest request, String productId, String cityId) {
        return actuaryOperationService.getCommo(productId, null);
    }

    @Override
    @ApiMethod
    public ServerResponse getAttributeData(HttpServletRequest request, String productId) {
        return actuaryOperationService.getAttributeData(productId);
    }
    @Override
    public String getAttributeName( String cityId, String productId){
        return actuaryOperationService.getAttributeName(productId);
    }

}
