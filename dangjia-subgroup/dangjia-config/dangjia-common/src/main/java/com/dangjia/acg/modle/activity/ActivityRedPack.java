package com.dangjia.acg.modle.activity;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 实体类 - 优惠券
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_red_pack")
@ApiModel(description = "优惠券")
public class ActivityRedPack extends BaseEntity {


	@Column(name = "name")
	@Desc(value = "优惠券名")
	@ApiModelProperty("优惠券名")
	private String name;

	@Column(name = "from_object")
	@Desc(value = "来源数据ID")
	@ApiModelProperty("来源数据ID")
	private String fromObject;//workertypeid

	@Column(name = "from_object_type")
	@Desc(value = "来源数据类型 0  人工 1  材料 2  货品")
	@ApiModelProperty("来源数据类型 0  人工 1  材料 2  货品 ")
	private Integer fromObjectType;//


	@Column(name = "start_date")
	@Desc(value = "有效开始时间")
	@ApiModelProperty("有效开始时间")
	private Date startDate;//

	@Column(name = "end_date")
	@Desc(value = "有效结束时间")
	@ApiModelProperty("有效结束时间")
	private Date endDate;//

	@Column(name = "city_id")
	@Desc(value = "可用城市ID")
	@ApiModelProperty("可用城市ID")
	private String cityId;//

	@Column(name = "num")
	@Desc(value = "发行总数数量")
	@ApiModelProperty("发行总数数量")
	private Integer num;//

	@Column(name = "surplus_nums")
	@Desc(value = "优惠券剩余总数量")
	@ApiModelProperty("优惠券剩余总数量")
	private Integer surplusNums ;//


	@Column(name = "receive_count")
	@Desc(value = "单人领取次数 默认1")
	@ApiModelProperty("单人领取次数 默认1")
	private Integer receiveCount;

	@Column(name = "is_share")
	@Desc(value = "是否可以与其他优惠券共用，0代表可以共用，1代表不可以共用")
	@ApiModelProperty("是否可以与其他优惠券共用，0代表可以共用，1代表不可以共用")
	private Integer isShare;//isshare

	@Column(name = "type")
	@Desc(value = "优惠券类型 0为减免金额券 1 为折扣券 2代金券")
	@ApiModelProperty("优惠券类型 0为减免金额券 1 为折扣券 2代金券")
	private Integer type; //

	@Column(name = "remake")
	@Desc(value = "备注说明适用范围")
	@ApiModelProperty("备注说明适用范围")
	private String remake;//

	@Column(name = "delete_state")
	@Desc(value = "状态，0正常，1停用")
	@ApiModelProperty("状态，0正常，1停用")
	private Integer deleteState;//deletestate

}