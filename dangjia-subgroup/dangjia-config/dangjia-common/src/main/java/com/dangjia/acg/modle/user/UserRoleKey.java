package com.dangjia.acg.modle.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_user_user_role")
@ApiModel(description = "用户角色关系表")
@FieldNameConstants(prefix = "")
public class UserRoleKey {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "role_id")
    private String roleId;

}