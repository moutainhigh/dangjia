package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.OrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:59
 */
@RestController
public class OrderController implements OrderAPI {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);
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
     * 删除订单
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delBusinessOrderById(String userToken, String orderId) {
        return orderService.delBusinessOrderById(userToken, orderId);
    }

    /**
     * 取消订单
     * @param userToken
     * @param orderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse cancleBusinessOrderById(String userToken, String orderId) {
        return orderService.cancleBusinessOrderById(userToken,orderId);
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
     */
    @Override
    @ApiMethod
    public ServerResponse abrufbildungSubmitOrder(String userToken, String cityId, String houseId,
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

    /**
     * 补差价订单（补差价订单详情）
     * @param userToken
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getDiffOrderById(String userToken,String houseId){
        return orderService.getDiffOrderById(userToken, houseId);
    }
    /**
     * 设计精算--原订单详情
     * @param userToken
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetOrderById(String userToken,
                                       String houseId){
        return orderService.getBudgetOrderById(userToken, houseId);
    }

    /**
     * 设计精算，退原订单ID,取消补差价订单ID
     * @param userToken 用户TOKEN
     * @param houseId 房子ID
     * @param orderId 订单ID
     * @param diffOrderId 补差价订单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse refundBudgetOrderInfo(String userToken,String houseId,String orderId,String diffOrderId){
        try{
            return orderService.refundBudgetOrderInfo(userToken, houseId, orderId, diffOrderId);
        }catch (Exception e){
            logger.error("退款异常",e);
            return ServerResponse.createByErrorMessage("退款异常");
        }

    }
}
