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
@Table(name = "dj_user_role")
@ApiModel(description = "角色表")
public class Role extends BaseEntity {

    @Column(name = "role_name")
    @Desc(value = "角色名称")
    @ApiModelProperty("角色名称")
    private String roleName;

    @Column(name = "descpt")
    @Desc(value = "角色描述")
    @ApiModelProperty("角色描述")
    private String descpt;

    @Column(name = "code")
    @Desc(value = "角色编号")
    @ApiModelProperty("角色编号")
    private String code;

    @Column(name = "insert_uid")
    @Desc(value = "操作用户id")
    @ApiModelProperty("操作用户id")
    private String insertUid;

}