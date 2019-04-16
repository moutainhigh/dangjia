package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseConstructionRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IHouseConstructionRecordMapper extends Mapper<HouseConstructionRecord> {

    List<HouseConstructionRecord> getHouseConstructionRecordByHouseId(@Param("houseId") String houseId);

    HouseConstructionRecord selectHcrByHouseFlowApplyId(@Param("houseFlowApplyId") String houseFlowApplyId);
}
