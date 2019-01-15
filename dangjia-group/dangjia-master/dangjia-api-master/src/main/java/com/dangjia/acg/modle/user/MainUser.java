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
	@Desc(value = "是否坐席（0：不是坐席；1，是坐席）")
	@ApiModelProperty("是否坐席（0：不是坐席；1，是坐席）")
	private Boolean isReceive;

	@Column(name = "department")
	@Desc(value = "部门")
	@ApiModelProperty("部门")
	private String department;


}