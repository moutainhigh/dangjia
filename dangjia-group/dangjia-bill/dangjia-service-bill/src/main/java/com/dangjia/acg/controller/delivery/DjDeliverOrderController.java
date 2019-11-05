package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliverOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * Date: 25/10/2019
 * Time: 上午 9:30
 */
@RestController
public class DjDeliverOrderController implements DjDeliverOrderAPI {

    @Autowired
    private DjDeliverOrderService djDeliverOrderService;


    @Override
    @ApiMethod
    public ServerResponse queryAllDeliverOrder(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId,String orderStatus) {
        return djDeliverOrderService.queryAllDeliverOrder(pageDTO,userId,cityId,orderStatus);
    }


    @Override
    @ApiMethod
    public ServerResponse queryOrderNumber(HttpServletRequest request,String userToken,String houseId) {
        return djDeliverOrderService.queryOrderNumber(userToken,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getDesignImag(HttpServletRequest request,String houseId) {
        return djDeliverOrderService.getDesignImag(houseId);
    }


    @Override
    @ApiMethod
    public ServerResponse getDesignInfo(HttpServletRequest request,String houseId) {
        return djDeliverOrderService.getDesignInfo(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getActuaryInfo(HttpServletRequest request,String houseId) {
        return djDeliverOrderService.getActuaryInfo(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCollectInfo(HttpServletRequest request,String houseId) {
        return djDeliverOrderService.getCollectInfo(houseId);
    }


}
