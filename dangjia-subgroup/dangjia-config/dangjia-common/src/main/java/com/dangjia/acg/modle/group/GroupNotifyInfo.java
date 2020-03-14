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
 * 实体类 - 群组后台消息通知记录流水
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_group_notify_info")
@ApiModel(description = "群组后台消息通知记录流水")
public class GroupNotifyInfo extends BaseEntity {


	@Column(name = "user_id")
	@Desc(value = "发送人ID 默认管理员")
	@ApiModelProperty("发送人ID 默认管理员")
	private String userId;

	@Column(name = "group_id")
	@Desc(value = "群组ID")
	@ApiModelProperty("群组ID")
	private String groupId;

	@Column(name = "text")
	@Desc(value = "通知内容")
	@ApiModelProperty("通知内容")
	private String text;

}