package com.dangjia.acg.dto.activity;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/19
 * Time: 13:49
 */
@Data
public class DjStoreActivityProductDTO extends BaseEntity {

    @Desc(value = "货品名称")
    @ApiModelProperty("货品名称")
    private String goodsName;

    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String productName;

    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productSn;

    @Desc(value = "店铺商品表id")
    @ApiModelProperty("店铺商品表id")
    private String productId;

    @Desc(value = "店铺活动配置id")
    @ApiModelProperty("店铺活动配置id")
    private String image;

    @Desc(value = "销售价")
    @ApiModelProperty("销售价")
    private Double sellPrice;

    @Desc(value = "库存")
    @ApiModelProperty("库存")
    private Integer inventory;

    @Desc(value = "抢购价格")
    @ApiModelProperty("抢购价格")
    private Double rushPurchasePrice;

    @Desc(value = "状态 报名状态 1:已报名 2:申请中 3:审核中 4：被打回")
    @ApiModelProperty("状态 报名状态 1:已报名 2:申请中 3:审核中 4：被打回")
    private Integer registrationStatus;


}
