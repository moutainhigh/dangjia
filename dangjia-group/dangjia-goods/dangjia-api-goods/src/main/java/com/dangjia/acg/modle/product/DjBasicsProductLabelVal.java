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

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 11:30
 */
@Data
@Entity
@Table(name = "dj_basics_product_label_val")
@ApiModel(description = "商品标签表")
@FieldNameConstants(prefix = "")
public class DjBasicsProductLabelVal extends BaseEntity {

    @Column(name = "product_id")
    @Desc(value = "商品id")
    @ApiModelProperty("商品id")
    private String productId;

    @Column(name = "label_id")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String labelId;

    @Column(name = "label_val_id")
    @Desc(value = "标签值id")
    @ApiModelProperty("标签值id")
    private String labelValId;

    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

}
