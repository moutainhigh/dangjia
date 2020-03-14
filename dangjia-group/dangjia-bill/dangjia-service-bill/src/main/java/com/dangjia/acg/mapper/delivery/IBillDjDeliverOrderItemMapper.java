package com.dangjia.acg.mapper.delivery;


import com.dangjia.acg.dto.delivery.DjDeliverOrderItemDTO;
import com.dangjia.acg.dto.delivery.MaterialNumberDTO;
import com.dangjia.acg.dto.delivery.NodeNumberDTO;
import com.dangjia.acg.modle.deliver.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 14/10/2019
 * Time: 下午 3:57
 */
@Repository
public interface IBillDjDeliverOrderItemMapper extends Mapper<OrderItem> {

    int updateDjDeliverOrderItemByOrderId(@Param("orderId") String orderId);

    int updateReserved(@Param("orderId") String orderId,
                       @Param("productId") String productId);

    List<DjDeliverOrderItemDTO > orderItemList(@Param("houseId")String houseId, @Param("orderId")String orderId);

}
