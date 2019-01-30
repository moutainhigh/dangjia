package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 剩余材料的临时仓库
 * ysl at 2019/1/29
 */
@Repository
public interface ISurplusWareHouseMapper extends Mapper<SurplusWareHouse> {
    //所有剩余材料的临时仓库
    List<SurplusWareHouse> getAllSurplusWareHouse(@Param("state") Integer state, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    //所有剩余材料的临时仓库
    SurplusWareHouse getAllSurplusWareHouseByHouseId(String houseId);
}

