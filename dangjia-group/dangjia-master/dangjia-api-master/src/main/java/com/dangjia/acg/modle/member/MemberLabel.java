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
 * 标签表 （客服根据业主的性格特点打的标签）
 * at ysl 2019-1-5
 */
@Data
@Entity
@Table(name = "dj_member_label")
@ApiModel(description = "业主标签表")
public class MemberLabel extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "标签名字")
	@ApiModelProperty("标签名字")
	private String name;

	@Column(name = "valueArr")
	@Desc(value = "标签值")
	@ApiModelProperty("标签值")
	private String valueArr;
}