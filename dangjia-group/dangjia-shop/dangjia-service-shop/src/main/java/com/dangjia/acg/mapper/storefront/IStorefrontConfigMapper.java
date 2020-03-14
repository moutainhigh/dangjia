package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.modle.storefront.StorefrontConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IStorefrontConfigMapper extends Mapper<StorefrontConfig> {

     StorefrontConfig getConfig(@Param("storefrontId") String storefrontId,@Param("paramKey")  String paramKey);
}
