package com.dangjia.acg.dto.product;

import com.dangjia.acg.modle.product.ShoppingCart;
import lombok.Data;

import java.util.List;


@Data
public class ShoppingCartDTO{

    private String  storefrontId;

    private String  storefrontName;

    private List<ShoppingCart> shoppingCarts;

}
