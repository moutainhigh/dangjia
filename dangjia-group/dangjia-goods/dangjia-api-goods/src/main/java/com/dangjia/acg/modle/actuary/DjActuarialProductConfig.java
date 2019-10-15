package com.dangjia.acg.modle.actuary;

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
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
@Entity
@Table(name = "dj_actuarial_product_config")
@ApiModel(description = "设计精算模板货品商品配置表")
@FieldNameConstants(prefix = "")
public class DjActuarialProductConfig extends BaseEntity {

    @Column(name = "actuarial_template_id")
    @Desc(value = "精算模板ID")
    @ApiModelProperty("精算模板ID")
    private String actuarialTemplateId;

    @Column(name = "goods_id")
    @Desc(value = "货品ID")
    @ApiModelProperty("货品ID")
    private String goodsId;

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "purchase_quantity")
    @Desc(value = "购买数量")
    @ApiModelProperty("购买数量")
    private String purchaseQuantity;

    @Column(name = "worker_type_id")
    @Desc(value = "工种类型（1设计师，2精算师，3大管家,4拆除，6水电，8泥工,9木工，10油漆工）")
    @ApiModelProperty("工种类型（1设计师，2精算师，3大管家,4拆除，6水电，8泥工,9木工，10油漆工）")
    private String workerTypeId;

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;


}
