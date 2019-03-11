package com.dangjia.acg.mapper.safe;

import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 19:51
 */
@Repository
public interface IWorkerTypeSafeOrderMapper extends Mapper<WorkerTypeSafeOrder> {

    WorkerTypeSafeOrder getByWorkerTypeId(@Param("workerTypeId")String workerTypeId,@Param("houseId")String houseId);

    WorkerTypeSafeOrder getByNotPay(@Param("workerTypeId")String workerTypeId,@Param("houseId")String houseId);
}
