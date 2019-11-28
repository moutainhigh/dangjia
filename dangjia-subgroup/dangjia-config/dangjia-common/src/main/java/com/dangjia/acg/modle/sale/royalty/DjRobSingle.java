package com.dangjia.acg.modle.sale.royalty;

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
@Table(name = "dj_rob_single")
@ApiModel(description = "抢单时间配置")
@FieldNameConstants(prefix = "")
public class DjRobSingle extends BaseEntity {

    @Column(name = "rob_date")
    @Desc(value = "配置时间")
    @ApiModelProperty("配置时间")
    private String robDate;

}
