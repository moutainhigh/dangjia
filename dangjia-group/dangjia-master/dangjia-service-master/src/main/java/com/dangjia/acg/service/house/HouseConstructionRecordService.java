package com.dangjia.acg.service.house;

import com.dangjia.acg.mapper.core.IHouseConstructionRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HouseConstructionRecordService {

    private IHouseConstructionRecordMapper houseConstructionRecordMapper;

    @Autowired
    public HouseConstructionRecordService(IHouseConstructionRecordMapper houseConstructionRecordMapper) {
        this.houseConstructionRecordMapper = houseConstructionRecordMapper;
    }

}
