package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.modle.storefront.Storefront;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface IGoodsStorefrontMapper extends Mapper<Storefront> {


    //根据店铺类型，查询店铺信息
    public Storefront selectStoreByTypeCityId(String cityId,String storefrontType);

}
