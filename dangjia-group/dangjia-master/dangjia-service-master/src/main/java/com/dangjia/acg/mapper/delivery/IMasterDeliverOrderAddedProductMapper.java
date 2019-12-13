package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.refund.DeliverOrderAddedProductDTO;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * Created with IntelliJ IDEA.
 * author: Fzh
 * Date: 28/11/2019
 * Time: 上午 17:00
 */
@Repository
public interface IMasterDeliverOrderAddedProductMapper extends Mapper<DeliverOrderAddedProduct> {
    //根据任意订单号查询对应的增值商吕信息
    String getAddedPrdouctStr(@Param("anyOrderId") String anyOrderId);

}
