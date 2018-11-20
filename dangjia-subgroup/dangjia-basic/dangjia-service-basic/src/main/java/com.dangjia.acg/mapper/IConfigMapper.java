package com.dangjia.acg.mapper;

import com.dangjia.acg.model.Config;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by QiYuXiang on 2017/8/3.
 */
@org.apache.ibatis.annotations.Mapper
public interface IConfigMapper extends Mapper<Config> {

  public List<Config> queryBasicConfig(Config config);
}
