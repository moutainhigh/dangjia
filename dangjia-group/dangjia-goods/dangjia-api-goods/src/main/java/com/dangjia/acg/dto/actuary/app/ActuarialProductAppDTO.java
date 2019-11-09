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

    @ApiModelProperty("类别ID")
    private String categoryId;

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

    @ApiModelProperty("是否按面积计算参考价格(1是，0否)")
    private String isCalculatedArea;

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

    @ApiModelProperty("商品类型（类型0：实物商品；1：服务商品；2：人工商品；3：体验；4：增值）")
    private Integer goodsType;

    @ApiModelProperty("购买性质（购买性质0：必买；1：可选；2：自购；3：不可单独购买）")
    private Integer goodsBuy;

    @ApiModelProperty("换算量")
    private Double convertQuality;

    @ApiModelProperty("换算单位")
    private String  convertUnit;

    @ApiModelProperty("是否有优惠卷(1：有，0：否)")
    private Integer  isActivityRedPack=0;




}
