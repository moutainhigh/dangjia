package com.dangjia.acg.modle.safe;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 保险订单
 */
@Data
@Entity
@Table(name = "dj_safe_worker_type_safe_order")
@ApiModel(description = "保险订单")
@FieldNameConstants(prefix = "")
public class WorkerTypeSafeOrder extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;

	@Column(name = "worker_type_safe_id")
	@Desc(value = "保险服务类型id")
	@ApiModelProperty("保险服务类型id")
	private String workerTypeSafeId;//workertypesafeid

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "member_id")
	@Desc(value = "会员ID")
	@ApiModelProperty("会员ID")
	private String memberId;//memberid

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//workertypeid

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工")
	private Integer workerType;//workertype

	@Column(name = "house_flow_id")
	@Desc(value = "进程ID")
	@ApiModelProperty("进程ID")
	private String houseFlowId;//houseflowid

	@Column(name = "state")
	@Desc(value = "支付状态0未支付1已支付")
	@ApiModelProperty("支付状态0未支付1已支付")
	private Integer state;

	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private BigDecimal price;


	@Column(name = "shop_date")
	@Desc(value = "购买时间")
	@ApiModelProperty("购买时间")
	private Date shopDate;//shopdate

	@Column(name = "force_time")
	@Desc(value = "生效时间")
	@ApiModelProperty("生效时间")
	private Date forceTime; //forcetime

	@Column(name = "expiration_date")
	@Desc(value = "到期时间")
	@ApiModelProperty("到期时间")
	private Date expirationDate;//expirationdate

	@Column(name = "service_state")
	@Desc(value = "1未到期,2已到期,3申请成功,4维保中,维保完成->1")
	@ApiModelProperty("1未到期,2已到期,3申请成功,4维保中,维保完成->1")
	private Integer serviceState;//servicestate
}