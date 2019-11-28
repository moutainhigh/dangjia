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
@Table(name = "dj_user_permission")
@ApiModel(description = "功能表")
public class Permission extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "菜单名称")
    @ApiModelProperty("菜单名称")
    private String name;

    @Column(name = "pid")
    @Desc(value = "父菜单id")
    @ApiModelProperty("父菜单id")
    private String pid;

    @Column(name = "zindex")
    @Desc(value = "菜单排序")
    @ApiModelProperty("菜单排序")
    private Integer zindex;

    @Column(name = "istype")
    @Desc(value = "权限分类（0 菜单；1 功能）")
    @ApiModelProperty("权限分类（0 菜单；1 功能）")
    private Integer istype;

    @Column(name = "descpt")
    @Desc(value = "描述")
    @ApiModelProperty("描述")
    private String descpt;

    @Column(name = "code")
    @Desc(value = "菜单编号")
    @ApiModelProperty("菜单编号")
    private String code;

    @Column(name = "icon")
    @Desc(value = "菜单图标名称")
    @ApiModelProperty("菜单图标名称")
    private String icon;

    @Column(name = "page")
    @Desc(value = "菜单url")
    @ApiModelProperty("菜单url")
    private String page;

    @Column(name = "sys_id")
    @Desc(value = "系统来源ID")
    @ApiModelProperty("系统来源ID")
    private String sysId;

    @Column(name = "domain_id")
    @Desc(value = "域名来源ID")
    @ApiModelProperty("域名来源ID")
    private String  domainId;
}