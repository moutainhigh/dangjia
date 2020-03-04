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
    public ServerResponse queryOrderNumber(HttpServletRequest request, String userToken) {
        return djDeliverOrderService.queryOrderNumber(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse getDesignImag(HttpServletRequest request, String houseId, Integer type) {
        return djDeliverOrderService.getDesignImag(houseId, type);
    }


    @Override
    @ApiMethod
    public ServerResponse getDesignInfo(HttpServletRequest request, String houseId) {
        return djDeliverOrderService.getDesignInfo(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getActuaryInfo(HttpServletRequest request, String houseId) {
        return djDeliverOrderService.getActuaryInfo(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getCollectInfo(HttpServletRequest request, String houseId) {
        return djDeliverOrderService.getCollectInfo(houseId);
    }

    /**
     * 店铺--订单管理--列表
     * @param request
     * @param pageDTO 查询分页
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param orderKey 查询
     * @param state 状态：3已支付
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryOrderInfo(HttpServletRequest request,
                                         PageDTO pageDTO,
                                         String userId, String cityId,
                                         String orderKey, Integer state) {
        return djDeliverOrderService.queryOrderInfo(pageDTO, userId, cityId, orderKey, state);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOrderFineInfo(HttpServletRequest request,
                                             PageDTO pageDTO,
                                             String orderId) {
        return djDeliverOrderService.queryOrderFineInfo(pageDTO, orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        return djDeliverOrderService.queryDeliverOrderListByStatus(pageDTO, userToken, houseId, cityId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderDsdListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        return djDeliverOrderService.queryDeliverOrderDsdListByStatus(pageDTO, userToken, houseId, cityId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse deliverOrderItemDetail(String orderId, Integer orderStatus) {
        return djDeliverOrderService.deliverOrderItemDetail(orderId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse orderSnapshop(String orderId, Integer orderStatus) {
        return djDeliverOrderService.orderSnapshop(orderId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse shippingDetail(String orderId, Integer orderStatus) {
        return djDeliverOrderService.shippingDetail(orderId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse stevedorageCostDetail(PageDTO pageDTO, String orderId, Integer orderStatus) {
        return djDeliverOrderService.stevedorageCostDetail(pageDTO, orderId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse transportationCostDetail(PageDTO pageDTO, String orderId, Integer orderStatus) {
        return djDeliverOrderService.transportationCostDetail(pageDTO, orderId, orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderHump(PageDTO pageDTO, String houseId, String state, String userToken) {
        return djDeliverOrderService.queryDeliverOrderHump(pageDTO, houseId, state,userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppOrderList(PageDTO pageDTO, String userToken, String houseId, String cityId, Integer orderStatus, String idList) {
        return djDeliverOrderService.queryAppOrderList(pageDTO,userToken, houseId, cityId, orderStatus, idList);
    }


    @Override
    @ApiMethod
    public ServerResponse queryAppHairOrderList(String userToken, PageDTO pageDTO, String houseId, String cityId) {
        return djDeliverOrderService.queryAppHairOrderList(userToken,pageDTO, houseId, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppHairOrderInFo(String userToken, String id) {
        return djDeliverOrderService.queryAppHairOrderInFo(id);
    }


    @Override
    @ApiMethod
    public ServerResponse updateAppOrderStats(String userToken, String lists, String id) {
        return djDeliverOrderService.updateAppOrderStats(lists, id);
    }

    @Override
    @ApiMethod
    public ServerResponse refuseAppOrderStats(String userToken, String id) {
        return djDeliverOrderService.refuseAppOrderStats(id);
    }

    @Override
    @ApiMethod
    public ServerResponse installAppOrderStats(String userToken, String id) {
        return djDeliverOrderService.installAppOrderStats(id);
    }


    @Override
    @ApiMethod
    public ServerResponse queryAppOrderInFoList(String userToken, PageDTO pageDTO, String id, String shippingState) {
        return djDeliverOrderService.queryAppOrderInFoList(id, shippingState);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppOrderWorkerInFoList(String userToken, String id, String shippingState) {
        return djDeliverOrderService.queryAppOrderWorkerInFoList(id, shippingState);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteAppOrder(String userToken, String id) {
        return djDeliverOrderService.deleteAppOrder(id);
    }

    @Override
    @ApiMethod
    public ServerResponse setConfirmReceipt(String id) {
        return djDeliverOrderService.setConfirmReceipt(id);
    }

    @Override
    @ApiMethod
    public ServerResponse  queryWorkerGoods(String houseId,Integer orderStatus) {
        return djDeliverOrderService.queryWorkerGoods(houseId,orderStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryWorkerGoodsInFo(String id) {
        return djDeliverOrderService.queryWorkerGoodsInFo(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCostDetailsAfterCompletion(String houseId) {
        return djDeliverOrderService.queryCostDetailsAfterCompletion(houseId);
    }

}
