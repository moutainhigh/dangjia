package com.dangjia.acg.modle.storefront;

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
@Table(name = "dj_basics_storefront_product")
@ApiModel(description = "店铺商品表")
@FieldNameConstants(prefix = "")
public class StorefrontProduct extends BaseEntity {

    /**
     * 店铺表ID
     */
    @Column(name = "storefront_id")
    @Desc(value = "店铺表ID")
    @ApiModelProperty("店铺表ID")
    private String storefrontId;

    /**
     * 商品模板ID
     */
    @Column(name = "prod_template_id")
    @Desc(value = "商品模板ID")
    @ApiModelProperty("商品模板ID")
    private String prodTemplateId;


    /**
     * 商品名称
     */
    @Column(name = "product_name")
    @Desc(value = "货品名称")
    @ApiModelProperty("货品名称")
    private String productName;


    /**
     * 商品名称
     */
    @Column(name = "goods_id")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String goodsId;


    /**
     * 上传商品图片
     */
    @Column(name = "image")
    @Desc(value = "上传商品图片")
    @ApiModelProperty("上传商品图片")
    private String image;
    /**
     * 上传商品详情图
     */
    @Column(name = "detail_image")
    @Desc(value = "上传商品详情图")
    @ApiModelProperty("上传商品详情图")
    private String detailImage;

    /**
     * 营销名称
     */
    @Column(name = "market_name")
    @Desc(value = "营销名称")
    @ApiModelProperty("营销名称")
    private String marketName;

    /**
     * 销售价格
     */
    @Column(name = "sell_price")
    @Desc(value = "销售价格")
    @ApiModelProperty("销售价格")
    private String sellPrice;

    /**
     * 供货数量
     */
    @Column(name = "supplied_num")
    @Desc(value = "供货数量")
    @ApiModelProperty("供货数量")
    private String suppliedNum;

    /**
     *  师傅是否按一层收取上楼费
     */
    @Column(name = "is_upstairs_cost")
    @Desc(value = " 师傅是否按一层收取上楼费")
    @ApiModelProperty(" 师傅是否按一层收取上楼费")
    private String isUpstairsCost;

    /**
     *  是否送货与安装/施工分开
     */
    @Column(name = "is_delivery_install")
    @Desc(value = " 是否送货与安装/施工分开")
    @ApiModelProperty(" 是否送货与安装/施工分开")
    private String isDeliveryInstall;

    /**
     * 搬运费
     */
    @Column(name = "move_cost")
    @Desc(value = " 搬运费")
    @ApiModelProperty("搬运费")
    private String moveCost;

    /**
     * 是否上架
     */
    @Column(name = "is_shelf_status")
    @Desc(value = " 是否上架")
    @ApiModelProperty("是否上架")
    private String isShelfStatus;

}
