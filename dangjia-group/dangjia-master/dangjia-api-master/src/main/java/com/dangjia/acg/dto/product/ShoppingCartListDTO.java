package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @Desc(value = "销售单价")
    @ApiModelProperty("销售单价")
    private Double price;

    @Desc(value = "购买数量")
    @ApiModelProperty("购买数量")
    private Integer shopCount;

    @Desc(value = "单位(个、条、箱、桶、米)")
    @ApiModelProperty("单位(个、条、箱、桶、米)")
    private String unitName;

    @Desc(value = "分类编号")
    @ApiModelProperty("分类编号")
    private String categoryId;

    @Desc(value = "产品类型:0：材料；1：包工包料 2:人工")
    @ApiModelProperty("产品类型:0：材料；1：包工包料 2:人工")
    private Integer productType;

    private String isReservationDeliver;//是否业主预约发货(1是，0否)
    private Double sellPrice;

    private Integer type;


}
