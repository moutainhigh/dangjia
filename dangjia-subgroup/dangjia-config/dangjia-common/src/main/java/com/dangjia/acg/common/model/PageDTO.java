package com.dangjia.acg.common.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 分页实体
 * @author QiYuXiang
 * @date:  2015年12月18日
 */
public class PageDTO implements Serializable {

	private static final long serialVersionUID = 7754159643145458588L;

	/**当前页*/
	@ApiModelProperty("当前页")
	private Integer pageNum;
	
	/**查询数量*/
	@ApiModelProperty(value = "数量")
	private Integer pageSize;
	
	/**排序字段*/
	@ApiModelProperty(value = "排序字段")
	private String orderBy;


	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum == null ? 0 : pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize == null ? 10 : pageSize;
	}


	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
