package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.house.House;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 房子
 * luof
 */
@Repository
public interface IHouseMapper extends Mapper<House> {

    /** 查询房子列表 - 根据小区名 */
    List<House> queryHouseListByResidential(@Param("residential") String residential);
}
