package com.dangjia.acg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
 
	private Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
	@Override
	protected Object determineCurrentLookupKey() {
		if(JdbcContextHolder.getDataSource()!=null) {
			logger.info("数据源为{}", JdbcContextHolder.getDataSource());
		}
		return JdbcContextHolder.getDataSource();
	}
	
}
