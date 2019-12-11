package com.dangjia.acg.modle.engineer;

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
 * Date: 11/12/2019
 * Time: 上午 10:08
 */
@Data
@Entity
@Table(name = "dj_skill_certification")
@FieldNameConstants(prefix = "")
@ApiModel(description = "技能认证")
public class DjSkillCertification extends BaseEntity {

    @Column(name = "worker_id")
    @Desc(value = "工匠id")
    @ApiModelProperty("工匠id")
    private String workerId;

    @Column(name = "product_id")
    @Desc(value = "人工商品id")
    @ApiModelProperty("人工商品id")
    private String productId;

    @Column(name = "product_sn")
    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productSn;

    @Column(name = "product_name")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String productName;
}
