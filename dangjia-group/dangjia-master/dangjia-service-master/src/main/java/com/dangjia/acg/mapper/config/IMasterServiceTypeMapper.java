package com.dangjia.acg.mapper.config;

import com.dangjia.acg.modle.config.ServiceType;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Ruking.Cheng
 * @descrilbe 对数据库中服务类型配置表操作的接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/24 6:01 PM
 */
@Repository
public interface IMasterServiceTypeMapper extends Mapper<ServiceType> {
}
