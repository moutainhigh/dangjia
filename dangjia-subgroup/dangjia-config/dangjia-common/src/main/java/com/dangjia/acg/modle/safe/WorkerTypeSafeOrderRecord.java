package com.dangjia.acg.modle.safe;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 保险订单记录
 */
@Data
@Entity
@Table(name = "dj_safe_worker_type_safe_record")
@ApiModel(description = "保险订单记录")
public class WorkerTypeSafeOrderRecord extends BaseEntity {

	@Column(name = "worker_type_safe_order_id")
	@Desc(value = "保险订单ID")
	@ApiModelProperty("保险订单ID")
	private String workerTypeSafeOrderId;//workertypesafeorderid

	@Column(name = "worker_type_safe_id")
	@Desc(value = "保险服务类型")
	@ApiModelProperty("保险服务类型")
	private String workerTypeSafeId;//workertypesafeid

	@Column(name = "worker_type_safe_name")
	@Desc(value = "保险名")
	@ApiModelProperty("保险名")
	private String workerTypeSafeName;//workertypesafname

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "house_address")
	@Desc(value = "房间详细信息")
	@ApiModelProperty("房间详细信息")
	private String houseAddress;//houseaddress

	@Column(name = "member_id")
	@Desc(value = "会员ID")
	@ApiModelProperty("会员ID")
	private String memberId;//memberid

	@Column(name = "member_username")
	@Desc(value = "会员名")
	@ApiModelProperty("会员名")
	private String memberUsername;//memberusername

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//workertypeid

	@Column(name = "worker_type_name")
	@Desc(value = "工种名")
	@ApiModelProperty("工种名")
	private String workerTypeName;//workertypename

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	private Integer workerType;//workertype

	@Column(name = "house_flow_id")
	@Desc(value = "进程ID")
	@ApiModelProperty("进程ID")
	private String houseFlowId;//houseflowid

	@Column(name = "record_detail")
	@Desc(value = "记录详情")
	@ApiModelProperty("记录详情")
	private String recordDetail;//recorddetail

	@Column(name = "state")
	@Desc(value = "支付状态0未支付1已支付")
	@ApiModelProperty("支付状态0未支付1已支付")
	private Integer state;//

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