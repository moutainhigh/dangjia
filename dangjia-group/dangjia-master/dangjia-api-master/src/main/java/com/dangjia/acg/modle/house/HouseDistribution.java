package com.dangjia.acg.modle.house;

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
 * 微信用户  相当于订单
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_house_distribution")
@ApiModel(description = "验房分销记录")
@FieldNameConstants(prefix = "")
public class HouseDistribution extends BaseEntity {

	@Column(name = "phone")
	@Desc(value = "联系方式")
	@ApiModelProperty("联系方式")
	private String phone;

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

	@Column(name = "number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String number;

	@Column(name = "city")
	@Desc(value = "用户所在的城市")
	@ApiModelProperty("用户所在的城市")
	private String city;//


	@Column(name = "info")
	@Desc(value = "详细信息")
	@ApiModelProperty("详细信息")
	private String info;//

	@Column(name = "head")
	@Desc(value = "头像")
	@ApiModelProperty("头像")
	private String head;//

	@Column(name = "state")
	@Desc(value = "是否支付1已支付0未支付")
	@ApiModelProperty("是否支付1已支付0未支付")
	private Integer state;//

	@Column(name = "price")
	@Desc(value = "支付价格")
	@ApiModelProperty("支付价格")
	private Double price;//

	@Column(name = "type")
	@Desc(value = "1验房活动 2..")
	@ApiModelProperty("1验房活动 2..")
	private Integer type;//

	@Column(name = "superior_id")
	@Desc(value = "上级id")
	@ApiModelProperty("上级id")
	private String superiorId;//

}
