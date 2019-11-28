package com.dangjia.acg.modle.group;


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
 * 实体类 - 群组表
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_group")
@ApiModel(description = "群组表")
public class Group extends BaseEntity {


	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "house_name")
	@Desc(value = "房子名称")
	@ApiModelProperty("房子名称")
	private String houseName;

	@Column(name = "user_id")
	@Desc(value = "业主ID")
	@ApiModelProperty("业主ID")
	private String userId;

	@Column(name = "user_name")
	@Desc(value = "业主姓名")
	@ApiModelProperty("业主姓名")
	private String userName;

	@Column(name = "user_mobile")
	@Desc(value = "业主手机")
	@ApiModelProperty("业主手机")
	private String userMobile;

	@Column(name = "group_id")
	@Desc(value = "极光群组ID")
	@ApiModelProperty("极光群组ID")
	private String groupId;

	@Column(name = "admin_id")
	@Desc(value = "极光群组管理员ID")
	@ApiModelProperty("极光群组管理员ID")
	private String adminId;
}