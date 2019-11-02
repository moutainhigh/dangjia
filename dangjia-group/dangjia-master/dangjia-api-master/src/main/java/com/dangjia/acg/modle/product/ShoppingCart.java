package com.dangjia.acg.modle.product;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_repair_shopping_cart")
@FieldNameConstants(prefix = "")
@ApiModel(description = " 新版购物车表")
public class ShoppingCart extends BaseEntity {
    @Column(name = "city_id")
    @Desc(value = "城市编号")
    @ApiModelProperty("城市编号")
    private String cityId;

    @Column(name = "member_id")
    @Desc(value = "用户编号")
    @ApiModelProperty("用户编号")
    private String memberId;

    @Column(name = "product_id")
    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productId;

    @Column(name = "product_sn")
    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productSn;
    @Column(name = "product_name")
    @Desc(value = "货品名称")
    @ApiModelProperty("货品名称")
    private String productName;

    @Column(name = "image")
    @Desc(value = "商品图片")
    @ApiModelProperty("商品图片")
    private String image;

    @Column(name = "price")
    @Desc(value = "销售单价")
    @ApiModelProperty("销售单价")
    private BigDecimal price;

    @Column(name = "shop_count")
    @Desc(value = "购买数量")
    @ApiModelProperty("购买数量")
    private Integer shopCount;

    @Column(name = "unit_name")
    @Desc(value = "单位(个、条、箱、桶、米)")
    @ApiModelProperty("单位(个、条、箱、桶、米)")
    private String unitName;

    @Column(name = "category_id")
    @Desc(value = "分类编号")
    @ApiModelProperty("分类编号")
    private String categoryId;

    @Column(name = "product_type")
    @Desc(value = "产品类型:0：材料；1：包工包料 2:人工")
    @ApiModelProperty("产品类型:0：材料；1：包工包料 2:人工")
    private Integer productType;


    @Column(name = "storefront_id")
    @Desc(value = "店铺表ID")
    @ApiModelProperty("店铺表ID")
    private String storefrontId;
}
