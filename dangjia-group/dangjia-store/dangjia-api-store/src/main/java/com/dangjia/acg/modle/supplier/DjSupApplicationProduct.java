package com.dangjia.acg.modle.supplier;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:20
 */
@Data
@Entity
@Table(name = "dj_sup_application_product")
@ApiModel(description = "供应商申请商品表")
@FieldNameConstants(prefix = "")
public class DjSupApplicationProduct extends BaseEntity {

    @Column(name = "sup_id")
    @Desc(value = "供应商id")
    @ApiModelProperty("供应商id")
    private String supId;


    @Column(name = "shop_id")
    @Desc(value = "店铺ID")
    @ApiModelProperty("店铺ID")
    private String shopId;

    @Column(name = "product_id")
    @Desc(value = "商品模板ID")
    @ApiModelProperty("商品模板ID")
    private String productId;


    @Column(name = "goods_id")
    @Desc(value = "货品id")
    @ApiModelProperty("货品id")
    private String goodsId;

    @Column(name = "price")
    @Desc(value = "价格")
    @ApiModelProperty("价格")
    private Double price;


    @Column(name = "stock")
    @Desc(value = "库存")
    @ApiModelProperty("库存")
    private Integer stock;


    @Column(name = "porterage")
    @Desc(value = "搬运费")
    @ApiModelProperty("搬运费")
    private Double porterage;

    @Column(name = "is_cartage_price")
    @Desc(value = "是否收取上楼费 0=否，1=是")
    @ApiModelProperty("是否收取上楼费 0=否，1=是")
    private String isCartagePrice;

    @Column(name = "supply_relationship")
    @Desc(value = " 供应关系 0:供应 1:停供")
    @ApiModelProperty("供应关系 0:供应 1:停供")
    private String supplyRelationShip;

    @Column(name = "application_status")
    @Desc(value = "申请状态 0:审核中 1:通过 2:不通过")
    @ApiModelProperty("申请状态 0:审核中 1:通过 2:不通过")
    private String applicationStatus;

    @Column(name = "fail_reason")
    @Desc(value = "失败原因")
    @ApiModelProperty("失败原因")
    private String failReason;

}
