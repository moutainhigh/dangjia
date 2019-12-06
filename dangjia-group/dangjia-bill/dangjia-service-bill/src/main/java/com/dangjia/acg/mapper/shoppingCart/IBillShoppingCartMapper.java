package com.dangjia.acg.mapper.shoppingCart;

import com.dangjia.acg.modle.product.ShoppingCart;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface IBillShoppingCartMapper extends Mapper<ShoppingCart> {
}
