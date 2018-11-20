package com.dangjia.acg.modle.tencent;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 微信用户  相当于订单
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_tencent_we_chat")
@ApiModel(description = "验房订单记录")
public class WeChat extends BaseEntity {

	@Column(name = "code")
	@Desc(value = "code")
	@ApiModelProperty("code")
	private String code;

	@Column(name = "openid")
	@Desc(value = "用户唯一标识")
	@ApiModelProperty("用户唯一标识")
	private String openid;//

	@Column(name = "nickname")
	@Desc(value = "昵称")
	@ApiModelProperty("昵称")
	private String nickname;//

	@Column(name = "sex")
	@Desc(value = "用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
	@ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
	private String sex;//

	@Column(name = "province")
	@Desc(value = "用户个人资料填写的省份")
	@ApiModelProperty("用户个人资料填写的省份")
	private String province;//

	@Column(name = "city")
	@Desc(value = "普通用户个人资料填写的城市")
	@ApiModelProperty("普通用户个人资料填写的城市")
	private String city;//

	@Column(name = "headimgurl")
	@Desc(value = "头像")
	@ApiModelProperty("头像")
	private String headimgurl;//

	@Column(name = "state")
	@Desc(value = "是否支付1已支付0未支付")
	@ApiModelProperty("是否支付1已支付0未支付")
	private int state;//

	@Column(name = "price")
	@Desc(value = "支付价格")
	@ApiModelProperty("支付价格")
	private double price;//

	@Column(name = "type")
	@Desc(value = "1验房活动 2..")
	@ApiModelProperty("1验房活动 2..")
	private int type;//

	@Column(name = "superior_id")
	@Desc(value = "上级id")
	@ApiModelProperty("上级id")
	private String superiorId;//
}
