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

@Data
@Entity
@Table(name = "dj_repair_sale_order_detail")
@ApiModel(description = "购物车-销售订单明细表")
@FieldNameConstants(prefix = "")
public class SaleOrderDetail extends BaseEntity {
    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "order_id")
    @Desc(value = "销售订单ID")
    @ApiModelProperty("销售订单ID")
    private String orderId;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "product_id")
    @Desc(value = "货品id")
    @ApiModelProperty("货品id")
    private String productId;

    @Column(name = "product_sn")
    @Desc(value = "货品编号")
    @ApiModelProperty("货品编号")
    private String productSn;

    @Column(name = "product_name")
    @Desc(value = "货品名称")
    @ApiModelProperty("货品名称")
    private String productName;

    @Column(name = "product_nick_name")
    @Desc(value = "货品昵称")
    @ApiModelProperty("货品昵称")
    private String productNickName;

    @Column(name = "category_id")
    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String categoryId;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "worker_goods_name")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String workerGoodsName;

    @Column(name = "worker_goods_sn")
    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String workerGoodsSn;

    @Column(name = "worker_goods_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String workerGoodsId;

    @Column(name = "product_type")
    @Desc(value = "0：材料；1：服务 2 人工")
    @ApiModelProperty("0：材料；1：服务 2 人工")
    private String productType;

    @Column(name = "price")
    @Desc(value = "销售价")
    @ApiModelProperty("销售价")
    private String price;

    @Column(name = "cost")
    @Desc(value = "成本价")
    @ApiModelProperty("成本价")
    private String cost;

    @Column(name = "shop_count")
    @Desc(value = "购买总数")
    @ApiModelProperty("购买总数")
    private String shopCount;

    @Column(name = "total_price")
    @Desc(value = "总价")
    @ApiModelProperty("总价")
    private String totalPrice;
}
