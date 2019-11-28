package com.dangjia.acg.modle.user;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "dj_user_sys")
@ApiModel(description = "系统来源表")
public class MainSys extends BaseEntity {


    @Column(name = "name")
    @Desc(value = "系统名称")
    @ApiModelProperty("系统名称")
    private String name;

    @Column(name = "descpt")
    @Desc(value = "系统描述")
    @ApiModelProperty("系统描述")
    private String descpt;

}