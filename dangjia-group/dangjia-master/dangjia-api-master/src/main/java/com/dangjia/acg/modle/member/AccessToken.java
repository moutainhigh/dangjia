package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;


@Data
@Entity
@ApiModel(description = "用户token信息")
public class AccessToken extends BaseEntity {

	@ApiModelProperty("用户名")
	private String memberId;//用户id
	private String userId;
	@ApiModelProperty("用户电话")
	private String phone;//用户电话
	@ApiModelProperty("工匠类型(0:大管家；1：普通工匠;2:业主)")
	private Integer memberType;//工匠类型(0:大管家；1：普通工匠;2:业主)
	@ApiModelProperty("工匠类型名称")
	private String workerTypeName;//工匠类型名称
	@ApiModelProperty("时间戳")
	private String timestamp;// 时间戳
	@ApiModelProperty("系统userToken")
	private String userToken;// 系统userToken

	@ApiModelProperty("用户信息")
	private Member member;// 用户信息

}
