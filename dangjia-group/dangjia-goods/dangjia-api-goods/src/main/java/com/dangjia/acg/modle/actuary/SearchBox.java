package com.dangjia.acg.modle.actuary;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @类 名： 搜索记录SerchBox.java
 */
@Data
@Entity
@Table(name = "dj_actuary_search_box")
@ApiModel(description = "人工精算")
public class SearchBox extends BaseEntity{
	@Column(name = "content")
	private String content;//内容

	@Column(name = "number")
	private Integer number;//次数

}
