package com.dangjia.acg.modle.storefront;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
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
    private String storefrontId;

    /**
     * 商品模板ID
     */
    @Column(name = "prod_template_id")
    private String prodTemplateId;

    /**
     * 商品名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 上传商品图片
     */
    @Column(name = "goods_img")
    private String goodsImg;
    /**
     * 上传商品详情图
     */
    @Column(name = "goods_detail_img")
    private String goodsDetailImg;

    /**
     * 营销名称
     */
    @Column(name = "market_name")
    private String marketName;

    /**
     * 销售价格
     */
    @Column(name = "sell_price")
    private String sellPrice;

    /**
     * 供货数量
     */
    @Column(name = "supplied_num")
    private String suppliedNum;

    /**
     *  师傅是否按一层收取上楼费
     */
    @Column(name = "is_upstairs_cost")
    private String isUpstairsCost;

    /**
     * 搬运费
     */
    @Column(name = "move_cost")
    private String moveCost;

    /**
     * 是否上架
     */
    @Column(name = "is_shelf_status")
    private String isShelfStatus;

}
