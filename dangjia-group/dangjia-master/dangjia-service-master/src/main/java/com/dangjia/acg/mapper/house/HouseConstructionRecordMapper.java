package com.dangjia.acg.mapper.house;

import com.dangjia.acg.dto.house.HouseConstructionRecordTypeDTO;
import com.dangjia.acg.modle.house.HouseConstructionRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface HouseConstructionRecordMapper extends Mapper<HouseConstructionRecord> {

    List<HouseConstructionRecordTypeDTO> getHouseConstructionRecordTypeDTO(@Param("houseId") String houseId);

}
