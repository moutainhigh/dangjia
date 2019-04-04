package com.dangjia.acg.mapper.core;

import com.dangjia.acg.dto.core.HouseWorkerDTO;
import com.dangjia.acg.modle.core.HouseWorker;
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
    /**根据workerTypeId和workType*/
    HouseWorker getByWorkerTypeId(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId,
                                  @Param("workType")Integer workType);

    Long grabControl(@Param("workerId")String workerId);
    List<HouseWorker> grabOneDayOneTime(@Param("workerId")String workerId);
    int doModifyAllByWorkerId(@Param("workerId")String workerId);
    List<HouseWorker> getDetailHouseWorker(@Param("workerId")String workerId);
    List<HouseWorker> getAllHouseWorker(@Param("workerId")String workerId);
    HouseWorker getHwByHidAndWtype(@Param("houseId")String houseId,@Param("workerType")Integer workerType);
    Long getCountOrderByWorkerId(@Param("workerId")String workerId);
    List<HouseWorker> paidListByHouseId(@Param("houseId")String houseId);
    List<HouseWorkerDTO> queryWorkerHouse(@Param("workerId")String workerId);
    int changeWorkerByHouseIdWorkerId(@Param("houseId") String houseId, @Param("workerId") String workerId);
}
