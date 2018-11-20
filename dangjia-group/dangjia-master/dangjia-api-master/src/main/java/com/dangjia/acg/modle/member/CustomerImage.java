package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 
 */
@Data
@Entity
@Table(name = "dj_member_customer_image")
@ApiModel(description = "客服图片")
public class CustomerImage extends BaseEntity {


	@Column(name = "customer_id")
	@Desc(value = "customerId")
	@ApiModelProperty("customerId")
	private String customerId;//customerid

	@Column(name = "imageurl")
	@Desc(value = "图片路径")
	@ApiModelProperty("图片路径")
	private String imageurl;
}