package com.dangjia.acg.modle.repair;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类 - 补材料申请
 * 原表名 ReplenishmentApply
 */
@Data
@Entity
@Table(name = "dj_repair_mend_material_apply")
@ApiModel(description = "补材料申请")
public class MendMaterialApply extends BaseEntity {

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	private int workerType; //workertype

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;// workertypeid

	@Column(name = "goods_id")
	@Desc(value = "商品id")
	@ApiModelProperty("商品id")
	private String goodsId;//goodsid

	@Column(name = "goods_sn")
	@Desc(value = "商品编号")
	@ApiModelProperty("商品编号")
	private String goodsSn;//

	@Column(name = "mend_money")
	@Desc(value = "补材料申请金额")
	@ApiModelProperty("补材料申请金额")
	private BigDecimal mendMoney; //replenishmentmoney

	@Column(name = "dispose_money")
	@Desc(value = "已处理补货申请金额")
	@ApiModelProperty("已处理补货申请金额")
	private BigDecimal disposeMoney; //disposemoney

	@Column(name = "agree_money")
	@Desc(value = "已同意补货申请金额")
	@ApiModelProperty("已同意补货申请金额")
	private BigDecimal agreeMoney; //agreemoney

	@Column(name = "disagree_money")
	@Desc(value = "未同意补货申请金额")
	@ApiModelProperty("未同意补货申请金额")
	private BigDecimal disagreeMoney;//noagreemoney

	@Column(name = "mend_state")
	@Desc(value = "1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付")
	@ApiModelProperty("1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付")
	private Integer mendState;//replenishmentstate

	@Column(name = "state")
	@Desc(value = "0代表删除这件商品5代表业主未支付6代表业主已支付")
	@ApiModelProperty("0代表删除这件商品5代表业主未支付6代表业主已支付")
	private Integer state;

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;//houseid

	@Column(name = "product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String productId; //productid

	@Column(name = "product_sn")
	@Desc(value = "货品编号")
	@ApiModelProperty("货品编号")
	private String productSn;//

	@Column(name = "mend_material_id")
	@Desc(value = "补材料单Id")
	@ApiModelProperty("补材料单Id")
	private String mendMaterialId; // abmrid

	@Column(name = "shop_count")
	@Desc(value = "购买总数")
	@ApiModelProperty("购买总数")
	private BigDecimal shopCount;//shopcount
}
