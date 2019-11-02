package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.refund.RefundOrderDTO;
import com.dangjia.acg.modle.deliver.OrderSplit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 6:06
 */
@Repository
public interface BillDjDeliverOrderSplitMapper extends Mapper<OrderSplit> {
    /**
     * 查询可退货退款列表
     * @param houseId
     * @param searchKey
     * @return
     */
    List<RefundOrderDTO> queryReturnRefundOrderList(@Param("houseId") String houseId, @Param("searchKey") String searchKey);
    /**
     * 查询可退货退款信息，根据ID
     * @param orderSplitId
     * @return
     */
    RefundOrderDTO queryReturnRefundOrderInfo(@Param("orderSplitId") String orderSplitId);
}
