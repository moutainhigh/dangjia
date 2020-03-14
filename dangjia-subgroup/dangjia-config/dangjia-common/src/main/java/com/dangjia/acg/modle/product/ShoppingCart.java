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
    private Double shopCount;

    @Column(name = "unit_name")
    @Desc(value = "单位(个、条、箱、桶、米)")
    @ApiModelProperty("单位(个、条、箱、桶、米)")
    private String unitName;

    @Column(name = "category_id")
    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String categoryId;

    @Column(name = "product_type")
    @Desc(value = "类型0：实物商品；1：服务商品；2：人工商品；3：体验；4：增值；5：维保")
    @ApiModelProperty("类型0：实物商品；1：服务商品；2：人工商品；3：体验；4：增值；5：维保")
    private Integer productType;


    @Column(name = "storefront_id")
    @Desc(value = "店铺表ID")
    @ApiModelProperty("店铺表ID")
    private String storefrontId;

    @Column(name = "is_reservation_deliver")
    @Desc(value = "是否业主预约发货(1是，0否)")
    @ApiModelProperty("是否业主预约发货(1是，0否)")
    private String isReservationDeliver;

    @Column(name = "unit_type")
    @Desc(value = "单位数值类型 1=整数单位，2=小数单位")
    @ApiModelProperty("单位数值类型 1=整数单位，2=小数单位")
    private Integer unitType;

    @Column(name = "value_id_arr")
    @Desc(value = "属性选项选中值id集合")
    @ApiModelProperty("属性选项选中值id集合")
    private String valueIdArr;
}
