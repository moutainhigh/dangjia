package com.dangjia.acg.modle.discount;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 红包
 */
@Data
@Entity
@Table(name = "dj_discount_red_packet")
@ApiModel(description = "红包")
public class RedPacket extends BaseEntity {

	@Column(name = "red_packet_activity_id")
	@Desc(value = "优惠券父级类别表id")
	@ApiModelProperty("优惠券父级类别表id")
	private String redPacketActivityId;//

	@Column(name = "numbered")
	@Desc(value = "编号")
	@ApiModelProperty("编号")
	private int numbered;//

	@Column(name = "delete_state")
	@Desc(value = "状态，0正常，1停用")
	@ApiModelProperty("状态，0正常，1停用")
	private int deleteState;//deletestate
}