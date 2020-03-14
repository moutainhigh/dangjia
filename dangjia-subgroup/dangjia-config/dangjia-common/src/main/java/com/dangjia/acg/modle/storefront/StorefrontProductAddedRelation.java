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
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "dj_basics_storefront_product_addedrelation")
@ApiModel(description = "店铺上架增值商品关联关系表")
@FieldNameConstants(prefix = "")
public class StorefrontProductAddedRelation extends BaseEntity {
    @Column(name = "product_id")
    @Desc(value = " 商品ID")
    @ApiModelProperty(" 商品ID")
    private String productId;

    @Column(name = "added_product_id")
    @Desc(value = " 增值商品ID")
    @ApiModelProperty(" 增值商品ID")
    private String addedProductId;

    @Transient
    private String addedProductName;//增值商品名称

    @Transient
    private Double sellPrice;//增值商品单价

}
