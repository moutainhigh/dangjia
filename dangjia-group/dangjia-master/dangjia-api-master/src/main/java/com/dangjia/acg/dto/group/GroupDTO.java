package com.dangjia.acg.dto.group;


import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

/**
 * 实体类 - 群组表
 */
@Data
@FieldNameConstants(prefix = "")
public class GroupDTO extends BaseEntity {


	@ApiModelProperty("房子ID")
	private String houseId;

	@ApiModelProperty("房子名称")
	private String houseName;

	@ApiModelProperty("业主ID")
	private String userId;

	@ApiModelProperty("业主姓名")
	private String userName;

	@Column(name = "user_mobile")
	private String userMobile;

	@ApiModelProperty("极光群组ID")
	private String groupId;

	@ApiModelProperty("极光群组管理员ID")
	private String adminId;

	@ApiModelProperty("群成员")
	private List<Map> members;
}