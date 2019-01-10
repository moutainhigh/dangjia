package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseWorkerOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 *
 */
@Repository
public interface IHouseWorkerOrderMapper extends Mapper<HouseWorkerOrder> {

    /**查询精算人工订单*/
    HouseWorkerOrder getByHouseIdAndWorkerTypeId(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

    HouseWorkerOrder getHouseWorkerOrder(@Param("houseId") String houseId,@Param("workerId") String workerId,@Param("workerTypeId")String workerTypeId);

}
