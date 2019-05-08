package com.dangjia.acg.modle.basics;

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
 * 关联组和商品关系
 * @author Ronalcheng
 */
@Data
@Entity
@Table(name = "dj_basics_group_link")
@ApiModel(description = "商品关联组")
@FieldNameConstants(prefix = "")
public class GroupLink extends BaseEntity {

	//关联组id
	@Column(name = "group_id")
	@Desc(value = "关联组id")
	@ApiModelProperty("关联组id")
	private String groupId;

	//商品id
	@Column(name = "product_id")
	private String productId;
	//商品名称
	@Column(name = "product_name")
	private String productName;

	//货品id
	@Column(name = "goods_id")
	private String goodsId;

	//货品名称
	@Column(name = "goods_name")
	private String goodsName;

	//关联组名称
	@Column(name = "group_name")
	private String groupName;

	//状态0：启用；1：禁用
	@Column(name = "state")
	private Integer state;

	//可切换性0:可切换；1不可切换
	@Column(name = "is_switch")
	private Integer isSwitch;
	
}
