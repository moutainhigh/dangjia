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
    private String srcGroupId;//该product所在的原关联组id
    private int isSwitch;//可切换性0:可切换；1不可切换
//    private String targetGroupId;//可切换的目标关联组id
    private String productId;
    private String goodsId;
    private String image;//product图 1张
    private String price;//价格加单位
    private String name;
    private String unitName;//单位
    private int productType;//0:材料；1：服务
    private List<String> imageList;//长图片 多图组合
    private List<BrandDTO> brandDTOList;//品牌系列

    private List<AttributeDTO> attrList;//属性
}
