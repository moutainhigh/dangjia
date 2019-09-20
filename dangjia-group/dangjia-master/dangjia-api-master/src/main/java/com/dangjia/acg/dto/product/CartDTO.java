package com.dangjia.acg.dto.product;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartDTO  implements Serializable {
    /**
     * 商品编号
     */
    private String productId;
    /**
     *  商品单价
     */
    private BigDecimal productPrice;
    /**
     * 商品数量
     */
    private Integer productNum;
    /**
     * 是否勾选
     */
    private String check;
    /**
     * 状态 0 :正常 1:下架
     */
    private Integer productStatus;

    /**
     * 商品小图
     */
    private String productIcon;
}
