package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.brand.Unit;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IUnitMapper extends Mapper<Unit> {

    List<Unit> getUnit();
}