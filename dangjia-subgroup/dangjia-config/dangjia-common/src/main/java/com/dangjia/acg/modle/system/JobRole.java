package com.dangjia.acg.modle.system;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_system_job_role")
@ApiModel(description = "岗位/角色关系表")
@FieldNameConstants(prefix = "")
public class JobRole {
    @Column(name = "job_id")
    private String jobId;

    @Column(name = "role_id")
    private String roleId;

}