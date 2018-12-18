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
 * 实体类 - 材料仓库统计流水
 */
@Data
@Entity
@Table(name = "dj_house_warehouse_detail")
@ApiModel(description = "仓库统计流水")
@FieldNameConstants(prefix = "")
public class WarehouseDetail extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "relation_id")
	@Desc(value = "支付的orderId,要货单splitOrderId,补材料mendOrderId，退材料订单mendOrderId")
	@ApiModelProperty("关联操作id")
	private String relationId;

	@Column(name = "record_type")
	@Desc(value = "记录类型 0支付精算;1要货;2补货;3退货")
	@ApiModelProperty("记录类型")
	private Integer recordType;

}