package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**发申请
 *zmj
 */
@Repository
public interface IHouseFlowApplyMapper extends Mapper<HouseFlowApply> {
    Long getCountValidPatrolByHouseId(@Param("houseId") String houseId,@Param("workerId") String workerId);
    HouseFlowApply getTodayStart(@Param("houseId") String houseId,@Param("workerId") String workerId);
    HouseFlowApply checkHouseFlowApply(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    List<HouseFlowApply> getSupervisorCheckList(@Param("houseId") String houseId);
    List<HouseFlowApply> getTodayHouseFlowApplyBy56(@Param("houseId") String houseId);
    HouseFlowApply checkSupervisorApply(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    List<HouseFlowApply> getTodayHouseFlowApply(@Param("houseFlowId") String houseFlowId,@Param("applyType") Integer applyType,
                                                @Param("workerId") String workerId);
    List<HouseFlowApply> getEarliestTimeHouseApply(@Param("houseId") String houseId,@Param("workerId") String workerId);
    Long getSuspendApply(@Param("houseId") String houseId,@Param("workerId") String workerId);
    Long getEveryDayApply(@Param("houseId") String houseId,@Param("workerId") String workerId);
    List<HouseFlowApply> getTodayHouseFlowApply2(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    List<HouseFlowApply> waitHouseFlowApply(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    List<HouseFlowApply> getTodayStartByHouseId(@Param("houseId") String houseId);
    HouseFlowApply getSupervisorCheck(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    List<HouseFlowApply> queryAllHfaByHouseId(@Param("houseId") String houseId);
}
