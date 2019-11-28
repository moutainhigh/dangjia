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
 * 实体类 - 群组客服成员配置表
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_group_user_config")
@ApiModel(description = "群组客服成员配置表")
public class GroupUserConfig extends BaseEntity {


	@Column(name = "name")
	@Desc(value = "姓名")
	@ApiModelProperty("姓名")
	private String name;

	@Column(name = "user_id")
	@Desc(value = "成员ID")
	@ApiModelProperty("成员ID")
	private String userId;

	@Column(name = "is_admin")
	@Desc(value = "是否为管理员")
	@ApiModelProperty("是否为管理员 0=是  1=否")
	private String isAdmin;

}