package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.matter.RenovationManual;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 装修指南
 * luof
 */
@Repository
public interface IRenovationManualMapper {

    /** 根据名称查询攻略 */
    List<RenovationManual> getRenovationManualByName(@Param("name")String name);
}
