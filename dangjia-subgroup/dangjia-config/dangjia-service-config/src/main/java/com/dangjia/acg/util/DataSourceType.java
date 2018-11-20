package com.dangjia.acg.util;

import org.apache.commons.lang.StringUtils;

public enum DataSourceType {
 

	// 长沙从表
	CS_CHANGSHA("402881882ba8753a012ba93101120116"),
	// 深圳从表
	SZ_CHANGSHA("zxs"),
	// 主表
	DANGJIA("dataSource");
	private String name;
 
	private DataSourceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
}
