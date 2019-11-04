package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.modle.core.HouseFlowApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface IBillHouseFlowApplyMapper extends Mapper<HouseFlowApply> {

    /**
     * 管家已审核业主未审核申请
     */
    List<HouseFlowApply> getMemberCheckList(@Param("houseId") String houseId);

}
