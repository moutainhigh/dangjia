package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.HouseChoiceCase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 案例
 * luof
 */
@Repository
public interface IHouseChoiceCaseMapper extends Mapper<HouseChoiceCase> {

    /** 查询案例列表 */
    List<HouseChoiceCase> queryHouseChoiceCaseList(@Param("title")String title);
}
