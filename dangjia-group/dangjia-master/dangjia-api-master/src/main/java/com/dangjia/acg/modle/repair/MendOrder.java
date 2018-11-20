package com.dangjia.acg.modle.repair;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 -补人工 补拆料 自购订单
 * 原表名 AppBudgetMaterialReplenishmentOrder
 */
@Data
@Entity
@Table(name = "dj_repair_mend_order")
@ApiModel(description = "补材料表")
public class MendOrder extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "type")
	@Desc(value = "0:补材料;1:补人工")
	@ApiModelProperty("0:补材料;1:补人工")
	private int type;//
	
}