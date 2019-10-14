package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.storefront.StorefrontProductViewDTO;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IStorefrontProductMapper extends Mapper<StorefrontProduct> {
    List<StorefrontProductViewDTO> queryStorefrontProductViewDTOList(@Param("keyWord") String keyWord);
}
