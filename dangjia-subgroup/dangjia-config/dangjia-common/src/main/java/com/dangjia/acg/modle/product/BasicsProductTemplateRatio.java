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
@Table(name = "dj_basics_product_template_ratio")
@FieldNameConstants(prefix = "")
@ApiModel(description = " 商品责任方占比表")
public class BasicsProductTemplateRatio extends BaseEntity {

    @Column(name = "product_template_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productTemplateId;//商品ID

    @Column(name = "product_responsible_id")
    @Desc(value = "商品责任方ID(0店铺，其它为工种ID)")
    @ApiModelProperty("商品责任方ID(0店铺，其它为工种ID)")
    private String productResponsibleId;//商品责任方ID(0店铺，其它为工种ID)

    @Column(name = "product_ratio")
    @Desc(value = "商品责任方占比（%）")
    @ApiModelProperty("商品责任方占比（%）")
    private Double productRatio;//商品责任方占比（%）

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;//备注

}
