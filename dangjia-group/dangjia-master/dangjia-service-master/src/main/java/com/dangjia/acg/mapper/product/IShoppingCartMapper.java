package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IShoppingCartMapper extends Mapper<ShoppingCart> {

    List<ShoppingCartListDTO> queryCartList(@Param("memberId") String memberId,
                                            @Param("cityId") String cityId,
                                            @Param("storefrontId") String StorefrontId,
                                            @Param("productIds")  String[] productIds);

    ShoppingCartListDTO querySingleCartList(@Param("id") String id);

    List<ShoppingCartDTO> queryShoppingCartDTOS(@Param("productIds")  String[] productIds);

    List<ShoppingCartDTO> queryShoppingCartDTOS1(@Param("storeActivityProductId")  String storeActivityProductId);

    List<Storefront> queryStorefrontIds(@Param("memberId") String memberId,
                                        @Param("cityId") String cityId);

    Integer queryPurchaseRestrictions(@Param("storefrontProductId") String storefrontProductId);

}
