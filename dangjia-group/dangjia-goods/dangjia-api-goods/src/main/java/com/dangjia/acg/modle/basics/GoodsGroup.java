package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 商品关联组
 * @author Ronalcheng
 */
@Data
@Entity
@Table(name = "dj_basics_goods_group")
@ApiModel(description = "商品关联组")
public class GoodsGroup extends BaseEntity {

	@Column(name = "name")
	private String name;//组名

	@Column(name = "state")
	private Integer state;//是否启用 1启用2不启用
	
}
