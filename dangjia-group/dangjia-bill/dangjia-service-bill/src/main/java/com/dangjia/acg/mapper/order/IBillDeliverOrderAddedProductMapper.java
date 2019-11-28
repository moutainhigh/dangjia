package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.refund.DeliverOrderAddedProductDTO;
import com.dangjia.acg.dto.refund.ReturnWorkOrderDTO;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.repair.ChangeOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * author: Fzh
 * Date: 28/11/2019
 * Time: 上午 17:00
 */
@Repository
public interface IBillDeliverOrderAddedProductMapper extends Mapper<DeliverOrderAddedProduct> {
    /**
     * 根据任意订单号查询对应的信息
     * @return
     */
    List<DeliverOrderAddedProductDTO> queryOrderListByAnyOrderId(@Param("anyOrderId") String anyOrderId);



}
