package com.dangjia.acg.mapper.sale.residential;

import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 16:16
 */
@Repository
public interface ResidentialBuildingMapper extends Mapper<ResidentialBuilding> {

    ResidentialBuilding selectSingleResidentialBuilding(@Param("storeId") String storeId,@Param("building") String building,@Param("villageId") String villageId);

}
