package com.dangjia.acg.mapper.config;

import com.dangjia.acg.modle.config.ServiceType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**对数据库中服务类型配置表操作的接口*/
@Repository
public interface IServiceTypeMapper extends Mapper<ServiceType> {
	/**根据拿到的Id删除服务务类型配置*/
	void deleteById(String id);

	/**
	 * 查询服务列表
	 * @return
	 */
	List<ServiceType> getServiceTypeList(@Param("cityId") String cityId);
}
