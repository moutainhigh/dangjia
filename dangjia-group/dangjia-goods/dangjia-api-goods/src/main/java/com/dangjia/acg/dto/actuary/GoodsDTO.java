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

    private String budgetMaterialId;
    private String productId;
    private String goodsId;
    private String image;//product图 1张
    private String price;//价格加单位
    private String name;
    private String unitName;//单位
    private int productType;//0:材料；1：服务
    private List<String> imageList;//长图片 多图组合
    private List<BrandSeriesDTO> brandDTOList;//品牌系列
    private List<AttributeDTO> attributeDTOList;//属性

}
