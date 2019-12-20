package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.deliver.BudgetOrderDTO;
import com.dangjia.acg.dto.deliver.BudgetOrderItemDTO;
import com.dangjia.acg.dto.house.HouseOrderDetailDTO;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

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
    /**查询人工订单*/
    Order getStorefontOrder(@Param("storefontId")String storefontId,@Param("parentOrderId")String parentOrderId);


    /**
     * 查询房子设计、精算的订单信息
     * @param houseId 房子信息
     * @param orderSource 1工序订单，4补差价订单
     * @param state 处理状态  1刚生成(可编辑),2去支付(不修改),3已支付,4已取消"
     * @return
     */
    BudgetOrderDTO getOrderInfoByHouseId(@Param("houseId") String houseId, @Param("orderSource") String orderSource,@Param("state") String state);

    /**
     * 查询房子设计、精算的订单信息
     * @param orderId 房子信息
     * @return
     */
    List<BudgetOrderItemDTO> getOrderInfoItemList(@Param("orderId") String orderId);

    /**
     * 查询所有的设计费用
     * @param orderId
     * @return
     */
    Double getDesgionTotalMoney(@Param("orderId") String orderId);



}
