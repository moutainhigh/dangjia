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

    @Column(name = "skill_certification_id")
    @Desc(value = "工匠id/工种id")
    @ApiModelProperty("工匠id/工种id")
    private String skillCertificationId;

    @Column(name = "prod_template_id")
    @Desc(value = "商品id")
    @ApiModelProperty("商品id")
    private String prodTemplateId;

    @Column(name = "product_sn")
    @Desc(value = "商品编号")
    @ApiModelProperty("商品编号")
    private String productSn;

    @Column(name = "product_name")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String productName;

    @Column(name = "type")
    @Desc(value = "类型 1:工匠 2：工种")
    @ApiModelProperty("类型 1:工匠 2：工种")
    private Integer type;

    @Column(name = "product_type")
    @Desc(value = "商品类型 类型0：实物商品；1：服务商品；2：人工商品；3：体验；4：增值；5：维保")
    @ApiModelProperty("商品类型 类型0：实物商品；1：服务商品；2：人工商品；3：体验；4：增值；5：维保")
    private Integer productType;
}
