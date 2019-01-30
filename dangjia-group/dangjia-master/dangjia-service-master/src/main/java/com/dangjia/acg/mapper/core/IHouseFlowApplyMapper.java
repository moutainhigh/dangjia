package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlowApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IHouseFlowApplyMapper extends Mapper<HouseFlowApply> {

    /**删除未审核申请*/
    void deleteNotMemberCheck(@Param("houseId")String houseId,@Param("workerId")String workerId);

    /**待处理申请*/
    List<HouseFlowApply> unCheckByWorkerTypeId(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);
    /**待审核申请*/
    List<HouseFlowApply> checkPendingApply(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    /**管家已审核业主未审核申请*/
    List<HouseFlowApply> getMemberCheckList(@Param("houseId") String houseId);

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
    List<HouseFlowApply> getTodayStartByHouseId(@Param("houseId") String houseId);
    HouseFlowApply getSupervisorCheck(@Param("houseFlowId") String houseFlowId,@Param("workerId") String workerId);
    /**根据houseId查询所有施工记录*/
    List<HouseFlowApply> queryAllHfaByHouseId(@Param("houseId") String houseId);
    /**查询工序记录*/
    List<HouseFlowApply> queryFlowRecord(@Param("houseFlowId")String houseFlowId);
}
