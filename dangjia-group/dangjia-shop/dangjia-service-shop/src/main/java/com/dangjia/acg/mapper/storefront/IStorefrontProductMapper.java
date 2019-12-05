package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.ShoppingCartProductDTO;
import com.dangjia.acg.dto.storefront.*;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IStorefrontProductMapper  extends Mapper<StorefrontProduct> {

    StorefrontProductListDTO querySingleStorefrontProductById(@Param("id") String id);

    List<BasicsStorefrontProductViewDTO> queryStorefrontProductViewDTOList(@Param("keyWord") String keyWord,@Param("storefrontId") String storefrontId,@Param("cityId") String cityId);

    List<BasicsStorefrontProductViewDTO> queryStorefrontProductGroundByKeyWord(@Param("keyWord") String keyWord,@Param("storefrontId") String storefrontId,@Param("cityId") String cityId);

    List<BasicsStorefrontProductMdPriceDTO> queryProductAdjustmentPriceListByKeyWord(@Param("keyWord") String keyWord,@Param("storefrontId") String storefrontId,@Param("cityId") String cityId);

    int selectProductByGoodsType(@Param("id") String id);

    List<ShoppingCartProductDTO> queryCartList(@Param("storefrontId") String storefrontId, @Param("productId") String productId);

    List<MemberCollectDTO> queryCollectGood(@Param("productId") String productId);

    StorefrontProduct queryStorefrontProductById(@Param("id") String id);

    Integer  getStorefrontProductCount(@Param("storefrontId") String storefrontId);

}
