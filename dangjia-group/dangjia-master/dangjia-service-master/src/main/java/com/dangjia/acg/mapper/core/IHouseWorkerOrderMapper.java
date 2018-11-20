package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseWorkerOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 *
 */
@Repository
public interface IHouseWorkerOrderMapper extends Mapper<HouseWorkerOrder> {
    HouseWorkerOrder getHouseWorkerOrder(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
}
