package com.dangjia.acg.util;

public enum DataSourceType {
 

	// 长沙从表
	CS_CHANGSHA("402881882ba8753a012ba93101120116"),
	// 深圳从表
	SZ_CHANGSHA("zxs"),
	// 株洲从表
	ZZ_CHANGSHA("961188961562724011757"),
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
