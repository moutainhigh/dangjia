package com.dangjia.acg.mapper.config;

import com.dangjia.acg.model.Config;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IBillConfigMapper extends Mapper<Config> {

    Config selectConfigInfoByParamKey(@Param("paramKey") String paramKey);

}
