package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface IMasterStorefrontProductMapper extends Mapper<StorefrontProduct> {

    List<MemberCollectDTO> queryCollectGood(@Param("productId") String productId);

    List<StorefrontProductDTO> queryProfessionalHomeInspector(@Param("cityId") String cityId);
}
