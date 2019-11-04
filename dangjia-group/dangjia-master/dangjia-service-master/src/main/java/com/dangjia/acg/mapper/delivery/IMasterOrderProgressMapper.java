package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.modle.order.OrderProgress;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMasterOrderProgressMapper extends Mapper<OrderProgress> {
    //更新数据状态
    void updateOrderStatusByNodeCode(@Param("repairMendOrderId") String repairMendOrderId,
                                     @Param("nodeType") String nodeType,
                                     @Param("nodeCode") String nodeCode);
}
