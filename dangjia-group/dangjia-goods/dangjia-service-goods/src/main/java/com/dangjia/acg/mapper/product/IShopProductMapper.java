package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IShopProductMapper extends Mapper<StorefrontProduct> {

}
