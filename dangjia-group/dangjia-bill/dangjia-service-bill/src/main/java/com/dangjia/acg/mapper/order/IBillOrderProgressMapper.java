package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.modle.order.OrderProgress;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IBillOrderProgressMapper extends Mapper<OrderProgress> {
    /**
     * 根据订单详情查询对应的流水记录
     * @param repairMendOrderId
     * @return
     */
    List<OrderProgressDTO> queryOrderProgressListByOrderId(@Param("repairMendOrderId") String repairMendOrderId);

}
