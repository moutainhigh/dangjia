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
import java.util.Date;

/**
 * 人工商品
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_basics_product_worker")
@ApiModel(description = "商品人工扩展表实体")
@FieldNameConstants(prefix = "")
public class DjBasicsProductWorker extends BaseEntity {

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "work_explain")
    @Desc(value = "工作说明")
    @ApiModelProperty("工作说明")
    private String workExplain;//工作说明

    @Column(name = "worker_dec")
    @Desc(value = "商品介绍图片")
    @ApiModelProperty("商品介绍图片")
    private String workerDec;//商品介绍图片

    @Column(name = "worker_standard")
    @Desc(value = "工艺标准")
    @ApiModelProperty("工艺标准")
    private String workerStandard;//工艺标准    暂不用(预留)

    @Column(name = "worker_type_id")
    @Desc(value = "关联工种id")
    @ApiModelProperty("关联工种id")
    private String workerTypeId;//关联工种id



    @Column(name = "other_name")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String otherName;//人工商品别名


    @Column(name = "last_price")
    @Desc(value = "调后单价")
    @ApiModelProperty("调后单价")
    private Double LastPrice;//调后单价

    @Column(name = "last_time")
    @Desc(value = "调价时间")
    @ApiModelProperty("调价时间")
    private Date LastTime;//调价时间

    @Column(name = "technology_ids")
    @Desc(value = "关联的工艺ID")
    @ApiModelProperty("关联的工艺ID，多个逗号分割")
    private String technologyIds;//关联的工艺ID，多个逗号分割

    @Column(name = "considerations")
    @Desc(value = "注意事项")
    @ApiModelProperty("注意事项")
    private String considerations;//注意事项

    @Column(name = "calculate_content")
    @Desc(value = "计价说明")
    @ApiModelProperty("计价说明，json串")
    private String calculateContent;

    @Column(name = "build_content")
    @Desc(value = "施工说明")
    @ApiModelProperty("施工说明，json串")
    private String buildContent;

    @Column(name = "is_agency_purchase")
    @Desc(value = " 是否为代买（1是，0否）")
    @ApiModelProperty(" 是否为代买（1是，0否）")
    private String isAgencyPurchase;
}