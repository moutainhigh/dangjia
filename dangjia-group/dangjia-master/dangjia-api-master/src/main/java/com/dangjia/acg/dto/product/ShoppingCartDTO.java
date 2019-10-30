package com.dangjia.acg.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class ShoppingCartDTO{

    private String  storefrontId;

    private String  storefrontName;

    private  BigDecimal totalPrice;


    private List<ShoppingCartListDTO> shoppingCartListDTOS;

}
