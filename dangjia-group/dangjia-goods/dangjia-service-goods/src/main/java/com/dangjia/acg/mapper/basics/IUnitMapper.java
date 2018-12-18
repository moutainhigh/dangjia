package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.brand.Unit;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IUnitMapper extends Mapper<Unit> {

    /**c查找所有的单位*/
    List<Unit> getUnit();
    /**根据拿到的name拿到单位对象*/
    List<Unit> getUnitByName(String name);
}