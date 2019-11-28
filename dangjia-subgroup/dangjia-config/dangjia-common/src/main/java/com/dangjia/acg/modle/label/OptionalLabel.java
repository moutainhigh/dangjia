package com.dangjia.acg.modle.label;

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
 * Date: 2019/6/17
 * Time: 11:00
 */
@Data
@Entity
@Table(name = "dj_optional_label")
@ApiModel(description = "精算选配标签表")
@FieldNameConstants(prefix = "")
public class OptionalLabel extends BaseEntity {

    @Column(name = "label_name")
    @Desc(value = "标签名称")
    @ApiModelProperty("标签名称")
    private String labelName;
}
