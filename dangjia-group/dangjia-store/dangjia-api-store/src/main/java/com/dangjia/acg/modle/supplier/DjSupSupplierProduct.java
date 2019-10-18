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
 * Date: 11/10/2019
 * Time: 下午 1:50
 */
@Data
@Entity
@Table(name = "dj_sup_supplier_product")
@ApiModel(description = "供应商商品表")
@FieldNameConstants(prefix = "")
public class DjSupSupplierProduct extends BaseEntity {

    @Column(name = "product_id")
    @Desc(value = "商品id")
    @ApiModelProperty("商品id")
    private String productId;//商品id

    @Column(name = "supplier_id")
    @Desc(value = "供应商id")
    @ApiModelProperty("供应商id")
    private String supplierId;//供应商id

    @Column(name = "goods_id")
    @Desc(value = "商品id")
    @ApiModelProperty("商品id")
    private String goodsId;//商品id

    @Column(name = "price")
    @Desc(value = "价格")
    @ApiModelProperty("价格")
    private Double price;//价格

    @Column(name = "stock")
    @Desc(value = "库存")
    @ApiModelProperty("库存")
    private Double stock;//库存

    @Column(name = "is_supply")
    @Desc(value = "是否供应")
    @ApiModelProperty("是否供应")
    private Integer isSupply;//是否供应；0停供，1供应
}
