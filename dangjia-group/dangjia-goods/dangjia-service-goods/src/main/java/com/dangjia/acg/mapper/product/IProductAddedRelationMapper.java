package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.ProductAddedRelation;
import com.dangjia.acg.modle.storefront.StorefrontProductAddedRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IProductAddedRelationMapper  extends Mapper<ProductAddedRelation> {

    List<String> getProdTemplateIdsByAddId(@Param("addedProductTemplateId") String addedProductTemplateId);

    List<StorefrontProductAddedRelation> getAddedrelationGoodsData(@Param("productId") String productId);

}

