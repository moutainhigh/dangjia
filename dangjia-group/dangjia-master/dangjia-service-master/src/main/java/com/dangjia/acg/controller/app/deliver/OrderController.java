package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.OrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:59
 */
@RestController
public class OrderController implements OrderAPI {

    @Autowired
    private OrderService orderService;

    /**
     * 订单详情
     */
    @Override
    @ApiMethod
    public ServerResponse orderDetail(String orderId) {
        return orderService.orderDetail(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse orderList(String businessOrderId) {
        return orderService.orderList(businessOrderId);
    }


    /**
     * 业务订单列表
     */
    @Override
    @ApiMethod
    public ServerResponse businessOrderList(PageDTO pageDTO, String userToken, String houseId, String queryId) {
        return orderService.businessOrderList(pageDTO, userToken, houseId, queryId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDeliverOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        return orderService.queryDeliverOrderListByStatus(pageDTO,userToken,houseId,cityId,orderStatus);
    }

    /**
     * 删除订单
     * @param userToken
     * @param orderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delBusinessOrderById( String userToken, String orderId) {
        return orderService.delBusinessOrderById(userToken,orderId);
    }

    /**
     * 管家确认要货
     * 提交到后台材料员审核
     */
    @Override
    @ApiMethod
    public ServerResponse confirmOrderSplit(String houseId, String userToken) {
        return orderService.confirmOrderSplit(userToken, houseId);
    }

    /**
     * 补货提交订单接口
     *
     */
    @Override
    @ApiMethod
    public ServerResponse abrufbildungSubmitOrder(String userToken,String cityId, String houseId,
                                                  String mendOrderId, String addressId) {
        return orderService.abrufbildungSubmitOrder(userToken, cityId, houseId, mendOrderId, addressId);
    }


    /**
     * 返回已添加要货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse getOrderItemList(String userToken, String houseId) {
        return orderService.getOrderItemList(userToken, houseId);
    }

    /**
     * 提交到要货
     */
    @Override
    @ApiMethod
    public ServerResponse saveOrderSplit(String productArr, String houseId, String userToken) {
        return orderService.saveOrderSplit(productArr, houseId, userToken);
    }
}
