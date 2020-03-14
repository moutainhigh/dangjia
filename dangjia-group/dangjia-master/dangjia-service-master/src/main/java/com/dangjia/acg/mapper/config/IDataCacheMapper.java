package com.dangjia.acg.mapper.config;

import com.dangjia.acg.modle.config.DataCache;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IDataCacheMapper extends Mapper<DataCache> {

}
