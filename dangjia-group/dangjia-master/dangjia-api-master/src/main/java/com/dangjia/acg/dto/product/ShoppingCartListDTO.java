package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 29/10/2019
 * Time: 下午 5:43
 */
@Data
public class ShoppingCartListDTO {

    private String id;

    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productId;

    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productSn;

    @Desc(value = "货品名称")
    @ApiModelProperty("货品名称")
    private String productName;

    @Desc(value = "商品图片")
    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("商品图片(单张)")
    private String imageSingle;

    @ApiModelProperty("商品图片详细地址")
    private String imageUrl;
    @Desc(value = "销售单价")
    @ApiModelProperty("销售单价")
    private Double price;

    @Desc(value = "购买数量")
    @ApiModelProperty("购买数量")
    private Double shopCount;

    @Desc(value = "单位(个、条、箱、桶、米)")
    @ApiModelProperty("单位(个、条、箱、桶、米)")
    private String unitName;

    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String categoryId;

    @Desc(value = "产品类型:0：材料；1：包工包料 2:人工")
    @ApiModelProperty("产品类型:0：材料；1：包工包料 2:人工")
    private Integer productType;

    private String isReservationDeliver;//是否业主预约发货(1是，0否)
    private Double sellPrice;

    private Integer unitType;//单位数值类型 1=整数单位，2=小数单位

    private String valueNameArr;
    private String valueIdArr;
    private String storefrontId;


    /**
     * 订单类型（1设计,2精算，2其它）
     */
    private String orderType;
    /**
     * 调后价格
     */
    private Double adjustedPrice;

    /**
     * 调价时间
     */
    private Date modityPriceTime;

    //增值商品集合(逗号分隔)
    private String addedProductIds;
    //增值商品集合
    private  List<DeliverOrderAddedProduct> addedProducts;

}
