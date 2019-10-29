package com.dangjia.acg.mapper.refund;

import com.dangjia.acg.dto.refund.RefundOrderDTO;
import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
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
     * 查询可退款的订单详情
     * @param orderId
     * @return
     */
    List<RefundOrderItemDTO> queryRefundOrderItemList(@Param("orderId") String orderId,@Param("searchKey") String searchKey);

    RefundOrderItemDTO queryRefundOrderItemInfo(@Param("orderItemId") String orderItemId);

}
