package com.dangjia.acg.mapper.storeFront;

import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IBillStorefrontMapper extends Mapper<Storefront> {
}
