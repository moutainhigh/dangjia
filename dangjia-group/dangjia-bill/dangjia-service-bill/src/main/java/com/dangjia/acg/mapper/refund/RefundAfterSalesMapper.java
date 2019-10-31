package com.dangjia.acg.mapper.refund;

import com.dangjia.acg.dto.refund.RefundOrderDTO;
import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
import com.dangjia.acg.dto.refund.RefundRepairOrderDTO;
import com.dangjia.acg.dto.refund.RefundRepairOrderMaterialDTO;
import com.dangjia.acg.modle.deliver.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 14/10/2019
 * Time: 下午 3:57
 */
@Repository
public interface RefundAfterSalesMapper extends Mapper<Order> {

    /**
     * 查询可退款的订单
     * @param houseId 房子ID
     * @return
     */
    List<RefundOrderDTO> queryRefundOrderList(@Param("houseId") String houseId,@Param("searchKey") String searchKey);

    /**
     * 根据订单ID查询订单信息
     * @param orderId
     * @return
     */
    RefundOrderDTO queryRefundOrderInfoById(@Param("orderId") String orderId);
    /**
     * 查询可退款的订单详情
     * @param orderId
     * @return
     */
    List<RefundOrderItemDTO> queryRefundOrderItemList(@Param("orderId") String orderId,@Param("searchKey") String searchKey);
    /**
     * 查询可退款的订单详情 根据订单详情ID
     * @param orderItemId
     * @return
     */
    RefundOrderItemDTO queryRefundOrderItemInfo(@Param("orderItemId") String orderItemId);


    /**
     * 查询仅退款历史退款记录
     * @param houseId
     * @return
     */
    List<RefundRepairOrderDTO> queryRefundOnlyHistoryOrderList(@Param("houseId") String houseId);

    /**
     * 查询退款退货详情信息
     * @param repairMendOrderId
     * @return
     */
    List<RefundRepairOrderMaterialDTO> queryRefundOnlyHistoryOrderMaterialList(@Param("repairMendOrderId") String repairMendOrderId);

    /**
     * 根据退货单ID查询退货详情信息
     * @param repairMendOrderId
     * @return
     */
    RefundRepairOrderDTO queryRefundOnlyHistoryOrderInfo(@Param("repairMendOrderId") String repairMendOrderId);

}
