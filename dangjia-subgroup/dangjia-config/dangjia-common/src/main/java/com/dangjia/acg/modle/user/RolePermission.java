package com.dangjia.acg.modle.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_user_role_permission")
@ApiModel(description = "角色和功能关系表")
public class RolePermission {
    @Column(name = "permit_id")
    private String permitId;
    @Column(name = "role_id")
    private String roleId;

}