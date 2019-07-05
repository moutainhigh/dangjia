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
