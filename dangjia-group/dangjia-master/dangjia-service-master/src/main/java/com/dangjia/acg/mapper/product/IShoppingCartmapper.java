package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.ShoppingCart;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IShoppingCartmapper  extends Mapper<ShoppingCart> {
}
