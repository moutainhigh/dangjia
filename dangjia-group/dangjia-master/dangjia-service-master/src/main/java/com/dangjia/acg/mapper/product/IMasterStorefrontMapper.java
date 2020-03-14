package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.storefront.Storefront;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface IMasterStorefrontMapper extends Mapper<Storefront> {
}
