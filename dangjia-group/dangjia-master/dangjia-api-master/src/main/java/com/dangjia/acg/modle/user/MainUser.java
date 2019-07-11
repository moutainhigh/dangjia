package com.dangjia.acg.modle.user;

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
@Table(name = "dj_user")
@ApiModel(description = "用户表")
@FieldNameConstants(prefix = "")
public class MainUser extends BaseEntity {

	@Column(name = "username")
	@Desc(value = "用户名")
	@ApiModelProperty("用户名")
	private String username;

	@Column(name = "mobile")
	@Desc(value = "手机号")
	@ApiModelProperty("手机号")
	private String mobile;

	@Column(name = "email")
	@Desc(value = "邮箱")
	@ApiModelProperty("邮箱")
	private String email;

	@Column(name = "password")
	@Desc(value = "密码")
	@ApiModelProperty("密码,MD5加密")
	private String password;

	@Column(name = "insert_uid")
	@Desc(value = "添加该用户的用户id")
	@ApiModelProperty("添加该用户的用户id")
	private String insertUid;

	@Column(name = "is_del")
	@Desc(value = "是否删除（0：正常；1：已删）")
	@ApiModelProperty("是否删除（0：正常；1：已删）")
	private Boolean isDel;

	@Column(name = "is_job")
	@Desc(value = "是否在职（0：正常；1，离职）")
	@ApiModelProperty("是否在职（0：正常；1，离职）")
	private Boolean isJob;

	@Column(name = "is_receive")
	@Desc(value = "是否坐席（0：不是坐席；1，售前客服坐席 2，售中客服坐席 3，材料顾问坐席4，工程顾问坐席）")
	@ApiModelProperty("是否坐席（0：不是坐席；1，售前客服坐席 2，售中客服坐席 3，材料顾问坐席4，工程顾问坐席）")
	private Integer isReceive;

	@Column(name = "department")
	@Desc(value = "部门")
	@ApiModelProperty("部门")
	private String department;

	@Column(name = "job_id")
	@Desc(value = "岗位ID")
	@ApiModelProperty("岗位ID")
	private String jobId;

	@Column(name = "department_id")
	@Desc(value = "部门ID")
	@ApiModelProperty("部门ID")
	private String departmentId;

	@Column(name = "member_id")
	@Desc(value = "当家用户ID")
	@ApiModelProperty("当家用户ID")
	private String memberId;

}