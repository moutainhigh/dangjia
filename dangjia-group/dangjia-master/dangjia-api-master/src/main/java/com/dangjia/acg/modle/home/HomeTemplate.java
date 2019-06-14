package com.dangjia.acg.modle.home;

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
 * @author Ruking.Cheng
 * @descrilbe 首页模版表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 10:59 AM
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_home_template")
@ApiModel(description = "首页模版表")
public class HomeTemplate extends BaseEntity {

    @Column(name = "user_id")
    @Desc(value = "操作人ID")
    @ApiModelProperty("操作人ID")
    private String userId;

    @Column(name = "name")
    @Desc(value = "模版名称")
    @ApiModelProperty("模版名称")
    private String name;

    @Column(name = "enable")
    @Desc(value = "是否启用：0:禁用，1:启用")
    @ApiModelProperty("是否启用：0:禁用，1:启用")
    private Integer enable;
}
