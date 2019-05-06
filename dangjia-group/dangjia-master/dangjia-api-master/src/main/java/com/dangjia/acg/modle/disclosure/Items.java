package com.dangjia.acg.modle.disclosure;

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
@Table(name = "dj_items")
@ApiModel(description = "开工完工事项")
@FieldNameConstants(prefix = "")
public class Items extends BaseEntity {
    @Column(name = "name")
    @Desc(value = "事项名字")
    @ApiModelProperty("事项名字")
    private String name;

    @Column(name = "type")
    @Desc(value = "类型")
    @ApiModelProperty("类型 0:开工 1：完工")
    private int type;
}
