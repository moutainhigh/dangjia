package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.model.Config;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by QiYuXiang on 2017/8/3.
 */
@Repository
public interface IStoreConfigMapper extends Mapper<Config> {

}
