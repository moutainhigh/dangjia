package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.OrderSplitItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderSplitItemMapper extends Mapper<OrderSplitItem> {

    void setSupplierId(@Param("id") String id, @Param("splitDeliverId") String splitDeliverId);

    /**确认收货更新收货数量*/
    void affirmSplitDeliver(@Param("splitDeliverId") String splitDeliverId);

}
