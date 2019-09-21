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
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
@Entity
@Table(name = "dj_basics_actuarial_configuration")
@ApiModel(description = "属性选项")
@FieldNameConstants(prefix = "")
public class DjBasicsActuarialConfiguration extends BaseEntity {

    @Column(name = "phase_id")
    @Desc(value = "阶段id")
    @ApiModelProperty("阶段id")
    private String phaseId;

    @Column(name = "goods_id")
    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String goodsId;

    @Column(name = "product_id")
    @Desc(value = "商品id")
    @ApiModelProperty("商品id")
    private String productId;

    @Column(name = "an_actuarial_table")
    @Desc(value = "精算Excel")
    @ApiModelProperty("精算Excel")
    private String anActuarialTable;
}
