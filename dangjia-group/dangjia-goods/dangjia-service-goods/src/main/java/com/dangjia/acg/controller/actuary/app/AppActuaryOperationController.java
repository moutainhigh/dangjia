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
    public ServerResponse choiceGoods(HttpServletRequest request, String houseId,String productId) {
        return actuaryOperationService.choiceGoods( houseId, productId);
    }

    @Override
    @ApiMethod
    public ServerResponse changeProduct(HttpServletRequest request, String productId, String budgetMaterialId,  String houseId, String workerTypeId) {
        return actuaryOperationService.changeProduct(productId, budgetMaterialId, houseId, workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse recoveryProduct(HttpServletRequest request,String houseId, String productId) {
        return actuaryOperationService.recoveryProduct( houseId, productId);
    }
    @Override
    @ApiMethod
    public ServerResponse selectProduct(HttpServletRequest request, String goodsId, String selectVal, String budgetMaterialId) {
        return actuaryOperationService.selectProduct(goodsId,  selectVal,  budgetMaterialId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCommo(HttpServletRequest request, String gId, String cityId) {
        return actuaryOperationService.getCommo(gId, null);
    }



}
