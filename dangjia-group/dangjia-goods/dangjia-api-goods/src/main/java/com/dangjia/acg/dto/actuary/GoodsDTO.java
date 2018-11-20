package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 19:52
 * 材料类商品
 */
@Data
public class GoodsDTO {

    private String image;//图 1张
    private String price;//价格加单位
    private String name;
    private List<String> imageList;//长图片 多图组合
}
