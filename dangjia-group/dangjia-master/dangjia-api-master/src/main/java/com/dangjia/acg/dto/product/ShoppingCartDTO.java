package com.dangjia.acg.dto.product;

import lombok.Data;

import java.util.List;


@Data
public class ShoppingCartDTO{

    private String  storefrontId;

    private String  storefrontName;

    private List<ShoppingCartListDTO> shoppingCartListDTOS;

}
