package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuaryOperationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:24
 */
@RestController
public class ActuaryOperationController implements ActuaryOperationAPI {

    @Autowired
    private ActuaryOperationService actuaryOperationService;


    @Override
    @ApiMethod
    public ServerResponse choiceGoods(HttpServletRequest request,String budgetMaterialId){
        return actuaryOperationService.choiceGoods(budgetMaterialId);
    }

    @Override
    @ApiMethod
    public ServerResponse changeProduct(HttpServletRequest request, String productId, String budgetMaterialId){
        return actuaryOperationService.changeProduct(productId,budgetMaterialId);
    }

    @Override
    @ApiMethod
    public ServerResponse selectProduct(HttpServletRequest request, String goodsId, String brandSeriesId, String attributeIdArr,String budgetMaterialId){
        return actuaryOperationService.selectProduct(goodsId,brandSeriesId,attributeIdArr,budgetMaterialId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCommo(HttpServletRequest request,String gId, String cityId, int type){
        return actuaryOperationService.getCommo(gId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getGoodsDetail(HttpServletRequest request,String gId, String cityId, int type){
        return actuaryOperationService.getGoodsDetail(gId,cityId,type);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmActuaryDetail(HttpServletRequest request,String userToken,String houseId,String workerTypeId,int type,String cityId){
        return actuaryOperationService.confirmActuaryDetail(userToken,houseId,workerTypeId,type,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmActuary(HttpServletRequest request,String userToken, String houseId,String cityId){
        return actuaryOperationService.confirmActuary(userToken,houseId,cityId);
    }
}
