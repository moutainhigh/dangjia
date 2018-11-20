package com.dangjia.acg.mapper.design;

import com.dangjia.acg.modle.design.HouseStyleType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:54
 * 装修风格
 */
@Repository
public interface IHouseStyleTypeMapper extends Mapper<HouseStyleType> {

    HouseStyleType getStyleByName(@Param("style") String style);
}
