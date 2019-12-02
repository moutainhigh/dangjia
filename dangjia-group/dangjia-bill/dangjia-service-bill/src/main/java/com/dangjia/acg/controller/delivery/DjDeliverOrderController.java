package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliverOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Override
    @ApiMethod
    public ServerResponse queryOrderInfo(HttpServletRequest request,
                                         PageDTO pageDTO,
                                         String userId,String cityId,
                                         String orderKey,int state) {
        return djDeliverOrderService.queryOrderInfo(pageDTO,userId,cityId,orderKey,state);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOrderFineInfo(HttpServletRequest request,
                                            PageDTO pageDTO,
                                             String orderId) {
        return djDeliverOrderService.queryOrderFineInfo(pageDTO,orderId);
    }
    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        return djDeliverOrderService.queryDeliverOrderListByStatus(pageDTO,userToken,houseId,cityId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderDsdListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        return djDeliverOrderService.queryDeliverOrderDsdListByStatus(pageDTO,userToken,houseId,cityId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse deliverOrderItemDetail(String orderId ,Integer orderStatus) {
        return djDeliverOrderService.deliverOrderItemDetail(orderId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse orderSnapshop(String orderId, Integer orderStatus) {
        return djDeliverOrderService.orderSnapshop(orderId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse shippingDetail(String orderId, Integer orderStatus) {
        return djDeliverOrderService.shippingDetail(orderId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse stevedorageCostDetail(PageDTO pageDTO,String orderId, Integer orderStatus) {
        return djDeliverOrderService.stevedorageCostDetail(pageDTO,orderId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse transportationCostDetail(PageDTO pageDTO,String orderId, Integer orderStatus) {
        return djDeliverOrderService.transportationCostDetail(pageDTO,orderId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderHump(PageDTO pageDTO, String houseId, String state) {
        return djDeliverOrderService.queryDeliverOrderHump(pageDTO,houseId,state);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppOrderList(PageDTO pageDTO, String userToken, String houseId, String cityId, Integer orderStatus,String idList) {
        return djDeliverOrderService.queryAppOrderList(pageDTO,houseId,cityId,orderStatus,idList);
    }


    @Override
    @ApiMethod
    public ServerResponse updateAppOrderStats(String userToken,String lists,String id) {
        return djDeliverOrderService.updateAppOrderStats(lists,id);
    }

    @Override
    @ApiMethod
    public ServerResponse refuseAppOrderStats(String userToken,String id) {
        return djDeliverOrderService.refuseAppOrderStats(id);
    }

    @Override
    @ApiMethod
    public ServerResponse installAppOrderStats(String userToken,String id) {
        return djDeliverOrderService.installAppOrderStats(id);
    }


    @Override
    @ApiMethod
    public ServerResponse queryAppOrderInFoList(String userToken, PageDTO pageDTO,String id,String shippingState) {
        return djDeliverOrderService.queryAppOrderInFoList(pageDTO,id,shippingState);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteAppOrder(String userToken,String id) {
        return djDeliverOrderService.deleteAppOrder(id);
    }
}
