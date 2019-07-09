package com.dangjia.acg.mapper.house;

import com.dangjia.acg.dto.house.VillageClassifyDTO;
import com.dangjia.acg.modle.house.House;
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

    List<Map<String, Object>> getVillageList(@Param("cityId") String cityId);

    List<ModelingVillage> getAllVillage(@Param("cityId") String cityId, @Param("likeVillageName") String likeVillageName);

    List<VillageClassifyDTO> getAllVillageDTO(@Param("cityId") String cityId, @Param("likeVillageName") String likeVillageName);

    Integer getAllVillageCount(@Param("cityId") String cityId);

    /*查询指定距离的施工现场*/
    List<House> jobLocation(@Param("latitude") String latitude, @Param("longitude") String longitude, @Param("beginDistance") Integer beginDistance, @Param("endDistance") Integer endDistance, @Param("limit") Integer limit);

//    /*查询指定距离的小区*/
//    List<String> jobModelingVillage(@Param("latitude") String latitude, @Param("longitude") String longitude,@Param("limit") Integer limit);
}
