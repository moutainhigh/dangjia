package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 31/10/2019
 * Time: 上午 10:34
 */
@Repository
public interface BillDjDeliverOrderSplitItemMapper extends Mapper<OrderSplitItem> {

    /**
     * 查询可退货单详情，根据退货订单ID
     * @param orderSplitId
     * @param searchKey
     * @return
     */
    List<RefundOrderItemDTO> queryReturnRefundOrderItemList(@Param("orderSplitId") String orderSplitId, @Param("searchKey") String searchKey);
    /**
     * 查询可退货单详情，根据退货订单详情ID
     * @param orderSplitItemId
     * @return
     */
    RefundOrderItemDTO queryReturnRefundOrderItemInfo(@Param("orderSplitItemId") String orderSplitItemId);

    List<String> querySplitDeliverId(@Param("orderId") String orderId);
}
