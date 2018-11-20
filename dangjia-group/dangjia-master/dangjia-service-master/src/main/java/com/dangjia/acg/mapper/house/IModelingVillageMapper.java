package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.ModelingVillage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:16
 */
@Repository
public interface IModelingVillageMapper extends Mapper<ModelingVillage> {

    List<Map<String,Object>> getVillageList(@Param("cityId")String cityId);
    List<ModelingVillage> getAllVillage(@Param("cityId")String cityId);
    Integer getAllVillageCount(@Param("cityId")String cityId);
}
