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
@Table(name = "dj_website_visit")
@ApiModel(description = "网站访问量")
@FieldNameConstants(prefix = "")
public class WebsiteVisit extends BaseEntity {

	@Column(name = "ip")
	@Desc(value = "访问者IP")
	@ApiModelProperty("访问者IP")
	private String ip;

	@Column(name = "route")
	@Desc(value = "访问的路径")
	@ApiModelProperty("访问的路径")
	private String route;

	@Column(name = "count")
	@Desc(value = "访问次数")
	@ApiModelProperty("访问次数")
	private Integer count;//

}
