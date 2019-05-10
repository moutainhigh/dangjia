package com.dangjia.acg.mapper.deliver;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.modle.deliver.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderItemMapper extends Mapper<OrderItem> {

    List<OrderItem> orderItemList(@Param("houseId")String houseId,@Param("orderId")String orderId, @Param("categoryId")String categoryId,
                                  @Param("name")String name);

    List<OrderItem> byOrderIdList(@Param("orderId")String orderId);

    //更新名称
    void updateOrderItemById(@Param("lists") JSONArray lists, @Param("brandSeriesId") String brandSeriesId, @Param("brandId") String brandId,
                             @Param("goodsId") String goodsId, @Param("id") String id);
}
