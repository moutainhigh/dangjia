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
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_basics_product_material")
@ApiModel(description = "商品材料扩展表实体")
@FieldNameConstants(prefix = "")
public class DjBasicsProductMaterial extends BaseEntity {

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "weight")
    @Desc(value = "重量")
    @ApiModelProperty("重量")
    private Double weight;//重量

    @Column(name = "cost")
    @Desc(value = "平均成本价")
    @ApiModelProperty("平均成本价")
    private Double cost;//平均成本价

    @Column(name = "profit")
    @Desc(value = "利润率")
    @ApiModelProperty("利润率")
    private Double profit;//利润率


    @Column(name = "convert_quality")
    @Desc(value = "换算量")
    @ApiModelProperty("换算量")
    private Double convertQuality;//换算量

    @Column(name = "convert_unit")
    @Desc(value = "换算单位")
    @ApiModelProperty("换算单位")
    private String convertUnit;//换算单位

    @Column(name = "is_influe_warranty_period")
    @Desc(value = "是否影响质保年限（1是，0否）")
    @ApiModelProperty("是否影响质保年限（1是，0否）")
    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）

    @Column(name = "worker_type_id")
    @Desc(value = "关联工序ID")
    @ApiModelProperty("关联工序ID")
    private String workerTypeId;//关联工序ID

    @Column(name = "max_warranty_period_year")
    @Desc(value = "最高质保年限")
    @ApiModelProperty("最高质保年限")
    private Integer maxWarrantyPeriodYear;//最高质保年限

    @Column(name = "min_warranty_period_year")
    @Desc(value = "最低质保年限")
    @ApiModelProperty("最低质保年限")
    private Integer minWarrantyPeriodYear;//最低质保年限

    @Column(name = "marketing_name")
    @Desc(value = "营销名称")
    @ApiModelProperty("营销名称")
    private String marketingName;//营销名称

    @Column(name = "cartage_price")
    @Desc(value = "搬运费(元/层)")
    @ApiModelProperty("搬运费(元/层)")
    private Double cartagePrice;//搬运费(元/层)

    @Column(name = "detail_image")
    @Desc(value = "上传详情图")
    @ApiModelProperty("上传详情图")
    private String detailImage;//上传详情图

    @Column(name = "guaranteed_policy")
    @Desc(value = "保修政策")
    @ApiModelProperty("保修政策")
    private String guaranteedPolicy;//保修政策

    @Column(name = "refund_policy")
    @Desc(value = "退款政策")
    @ApiModelProperty("退款政策")
    private String refundPolicy;//退款政策


}
