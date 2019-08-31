package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 16:16
 */
@Repository
public interface ResidentialBuildingMapper extends Mapper<ResidentialBuilding> {

    ResidentialBuilding selectSingleResidentialBuilding(@Param("storeId") String storeId, @Param("building") String building, @Param("villageId") String villageId);

    List<ResidentialBuilding> getvillageIdGroupBy(@Param("buildingId") String[] buildingId);

    List<ResidentialBuilding> getBuildingByVillageId(@Param("villageId") String villageId);

    int setBuildingInformation(Map<String,Object> map);
}
