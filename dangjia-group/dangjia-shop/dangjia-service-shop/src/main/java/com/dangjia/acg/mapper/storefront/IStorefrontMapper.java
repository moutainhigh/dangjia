package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IStorefrontMapper extends Mapper<Storefront> {


    List<StorefrontListDTO> querySupplierApplicationShopList(@Param("searchKey") String searchKey,
                                                             @Param("supId") String supId,
                                                             @Param("applicationStatus") String applicationStatus);


    List<Storefront> queryLikeSingleStorefront(@Param("searchKey") String searchKey);
}
