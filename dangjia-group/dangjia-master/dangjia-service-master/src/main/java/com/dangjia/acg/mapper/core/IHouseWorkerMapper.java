package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:59
 */
@Repository
public interface IHouseWorkerMapper extends Mapper<HouseWorker> {
    List<HouseWorker> grabControl(@Param("workerId")String workerId);
    List<HouseWorker> grabOneDayOneTime(@Param("workerId")String workerId);
    int doModifyAllByWorkerId(@Param("workerId")String workerId);
    HouseWorker getDetailHouseWorker(@Param("workerId")String workerId);
    List<HouseWorker> getAllHouseWorker(@Param("workerId")String workerId);
    HouseWorker getHwByHidAndWtype(@Param("houseId")String houseId,@Param("workerType")Integer workerType);
    Long getCountOrderByWorkerId(@Param("workerId")String workerId);
    List<HouseWorker> getWorktype6ByHouseid(@Param("houseId")String houseId);
}
