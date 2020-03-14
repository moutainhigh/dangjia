package com.dangjia.acg.mapper;

import com.dangjia.acg.model.Config;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * Created by QiYuXiang on 2017/8/3.
 */
@org.apache.ibatis.annotations.Mapper
@Repository
public interface IStoreConfigMapper extends Mapper<Config> {

}
