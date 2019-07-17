package com.dangjia.acg.dto.deliver;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/20 0020
 * Time: 16:42
 */
@Data
public class ItemDTO {
    private String name;
    private String image;
    private String price;//价格
    private Double shopCount;//购买总数
    private Integer productType; //0：材料；1：包工包料 2 人工 3空白
}
