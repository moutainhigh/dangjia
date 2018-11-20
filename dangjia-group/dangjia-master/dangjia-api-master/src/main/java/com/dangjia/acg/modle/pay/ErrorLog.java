package com.dangjia.acg.modle.pay;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 支付错误日志
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_pay_error_log")
@ApiModel(description = "房子")
public class ErrorLog extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber; //

	@Column(name = "instructions")
	@Desc(value = "instructions")
	@ApiModelProperty("instructions")
	private String instructions;//说明 
	
}
