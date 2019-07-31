package com.dangjia.acg.mapper.other;

import com.dangjia.acg.modle.other.City;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 16:13
 */
@Repository
public interface ICityMapper extends Mapper<City> {

    //所有城市
    List<Map<String,Object>> getAllCity();

    /**
     * 根据城市id查询名称
     * @param map
     * @return
     */
    String queryCityName(Map<String,Object> map);
}
