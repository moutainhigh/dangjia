package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName: Goods
 * @Description: 货品对象
 * @author: fzh
 * @date: 2018-9-12
 */
@Data
@Entity
@Table(name = "dj_basics_product_added_relation")
@ApiModel(description = "货品")
@FieldNameConstants(prefix = "")
public class ProductAddedRelation extends BaseEntity {

	@Column(name = "product_template_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productTemplateId;

	@Column(name = "added_product_template_id")
    @Desc(value = "增值商品ID")
    @ApiModelProperty("增值商品ID")
    private String addedProductTemplateId;
}