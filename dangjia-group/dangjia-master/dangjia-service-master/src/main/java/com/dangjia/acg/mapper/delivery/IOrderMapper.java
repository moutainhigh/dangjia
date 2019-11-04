package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderMapper extends Mapper<Order> {
    /**订单号*/
    List<Order> byBusinessOrderNumber(@Param("businessOrderNumber")String businessOrderNumber);

    List<Order> byBusinessOrderNumberAndOrderStatus(@Param("businessOrderNumber")String businessOrderNumber,@Param("orderStatus")String orderStatus);

    /**查询人工订单*/
    Order getWorkerOrder(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

    /**查询所有订单*/
    List<Order> getAllOrders(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

    /**
     * 根据房屋ID查对应的精算订单信息
     * @return
     */
    Order getOrderInfo(@Param("houseId") String houseId);

    //根据房子ID查询对应的订单详情
    List<OrderItem> getOrderDetailInfoList(@Param("houseId") String houseId);

    //修改商品订单对应的信息
    void updateOrderDetail(OrderItem orderDetail);

    //修改商品订单表是否可付款状态
    void updateOrder(@Param("orderId") String orderId);

    /**
     * 修改订单状态为已取消
     * @param houseId
     */
    void updateOrderStatusByHouseId(@Param("houseId") String houseId);
    /**
     * 修改订单详情状态为已取消
     * @param houseId
     */
    void updateOrderDetailStatusByHouseId(@Param("houseId") String houseId);
}
