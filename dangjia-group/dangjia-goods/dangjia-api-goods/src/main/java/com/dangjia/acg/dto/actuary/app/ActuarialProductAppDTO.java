package com.dangjia.acg.dto.actuary.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialProductAppDTO {



    @ApiModelProperty("货品ID")
    private String goodsId;

    @ApiModelProperty("商品ID")
    private String productId;

    @ApiModelProperty("商品模板ID")
    private String productTemplateId;

    @ApiModelProperty("店铺ID")
    private String storefrontId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("商品图片详细地址")
    private String imageUrl;

    @ApiModelProperty("商品编码")
    private String productSn;

    @ApiModelProperty("商品单位")
    private String unit;

    @ApiModelProperty("商品单价")
    private BigDecimal price;

    @ApiModelProperty("购买总数")
    private Double shopCount;//购买总数 (精算的时候，用户手动填写的购买数量， 该单位是 product 的convertUnit换算单位 )

    //单位换算成 goods 表里的unit_name 后的购买总数
    // （相当于 小单位 转成 大单位后的购买数量  公式：budgetMaterial.setConvertCount(Math.ceil(shopCount / pro.getConvertQuality()));）
    @ApiModelProperty("换算后购买总数")
    private Double convertCount;

    @ApiModelProperty("商品单位名称")
    private String unitName;

    @ApiModelProperty("商品属性规格ID")
    private String valueIdArr;

    @ApiModelProperty("商品属性规格名称")
    private String valueNameArr;

    @ApiModelProperty("品牌ID")
    private String brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;



}
