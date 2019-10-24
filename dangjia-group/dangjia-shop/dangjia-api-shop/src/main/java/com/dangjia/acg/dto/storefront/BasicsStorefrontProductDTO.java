package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasicsStorefrontProductDTO {


    /**
     * 店铺表ID
     */
    private String storefrontId;

    /**
     * 商品模板ID
     */
    private String prodTemplateId;


    /**
     * 商品名称
     */
    private String productName;


    /**
     * 商品名称
     */
   private String goodsId;


    /**
     * 上传商品图片
     */
    private String image;
    /**
     * 上传商品详情图
     */
    private String detailImage;

    /**
     * 营销名称
     */
    private String marketName;

    /**
     * 销售价格
     */
    private Double sellPrice;

    /**
     * 供货数量
     */
    private Double suppliedNum;

    /**
     * 师傅是否按一层收取上楼费
     */
    private String isUpstairsCost;

    /**
     * 是否送货与安装/施工分开
     */
    private String isDeliveryInstall;

    /**
     * 搬运费
     */
    private BigDecimal moveCost;

    /**
     * 是否上架
     */
    private String isShelfStatus;

}
