package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.product.ShoppingCart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IShoppingCartMapper extends Mapper<ShoppingCart> {

    List<ShoppingCartListDTO> queryCartList(@Param("memberId") String memberId,
                                            @Param("cityId") String cityId,
                                            @Param("storefrontId") String StorefrontId);

    List<String> queryStorefrontIds(@Param("memberId") String memberId,
                                    @Param("cityId") String cityId);

    Integer queryPurchaseRestrictions(@Param("storefrontProductId") String storefrontProductId);

    List<House> queryWhetherThereIsActuarial(@Param("memberId") String memberId);
}
