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
 * 实体类 - APP消息通知
 */
@Data
@Entity
@Table(name = "dj_member_information")
@ApiModel(description = "APP消息通知")
public class Information extends BaseEntity {

	@Column(name = "title")
	@Desc(value = "标题")
	@ApiModelProperty("标题")
	private String title;//

	@Column(name = "image")
	@Desc(value = "封面")
	@ApiModelProperty("封面")
	private String image;//

	@Column(name = "type")
	@Desc(value = "0链接,1文章,2红包活动")
	@ApiModelProperty("0链接,1文章,2红包活动")
	private Integer type;//

	@Column(name = "url")
	@Desc(value = "点击跳转地址")
	@ApiModelProperty("点击跳转地址")
	private String url;//

	@Column(name = "content")
	@Desc(value = "内容")
	@ApiModelProperty("内容")
	private String content;//

	@Column(name = "state")
	@Desc(value = "状态 1 为推出去")
	@ApiModelProperty("状态 1 为推出去")
	private Integer state;//
}