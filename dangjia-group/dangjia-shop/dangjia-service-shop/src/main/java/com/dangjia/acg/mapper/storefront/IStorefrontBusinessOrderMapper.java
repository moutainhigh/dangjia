package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.modle.pay.BusinessOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IStorefrontBusinessOrderMapper extends Mapper<BusinessOrder> {
}
