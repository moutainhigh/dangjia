package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendOrderMapper extends Mapper<MendOrder>{

    /**查询工种未处理补人工*/
    List<MendOrder> unCheckRepWorker(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    /**查询工种未处理退人工*/
    List<MendOrder> unCheckBackWorker(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    /**查询退人工单*/
    List<MendOrder> workerBackState(@Param("houseId") String houseId);

    /**查询补人工单*/
    List<MendOrder> workerOrderState(@Param("houseId") String houseId);

    /**查询未处理退人工*/
    List<MendOrder> backWorker(@Param("houseId") String houseId);

    /**查询未处理补人工*/
    List<MendOrder> untreatedWorker(@Param("houseId") String houseId);

    /**查询业主退货单*/
    List<MendOrder> landlordState(@Param("houseId") String houseId);

    /**查询退货单*/
    List<MendOrder> materialBackState(@Param("houseId") String houseId);

    /**查询补货单*/
    List<MendOrder> materialOrderState(@Param("houseId") String houseId);

}