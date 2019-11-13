package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.actuary.BudgetLabelDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelGoodsDTO;
import com.dangjia.acg.dto.actuary.ShopGoodsDTO;
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

    List<OrderItem> queryDeliverOrderItemDetail(@Param("orderId")String orderId);

    List<ShopGoodsDTO> queryShopGoods(@Param("orderId")String orderId);
    List<BudgetLabelDTO>  queryBudgetLabel(@Param("orderId")String orderId ,@Param("storefontId")String storefontId);
    List<BudgetLabelGoodsDTO>  queryBudgetLabelGoods(@Param("orderId")String orderId ,@Param("storefontId")String storefontId);

}
