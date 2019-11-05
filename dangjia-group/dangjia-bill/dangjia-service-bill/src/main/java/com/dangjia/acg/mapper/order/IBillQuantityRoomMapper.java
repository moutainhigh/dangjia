package com.dangjia.acg.mapper.order;

import com.dangjia.acg.common.pay.domain.Data;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.design.QuantityRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;



@Repository
public interface IBillQuantityRoomMapper extends Mapper<QuantityRoom> {
    //根据房子和类型查询对应的数据
    QuantityRoom getBillQuantityRoom(@Param("houseId") String houseId,
                                     @Param("type") Integer type);


    List<QuantityRoomDTO> getQuantityRoomList(@Param("houseId") String houseId);


    Date selectMaxEndDate(@Param("houseId") String houseId);


    List<HouseFlowApply> selectApplyInfo(@Param("houseId") String houseId);


    List<HouseWorker> selectWorkerInfo(@Param("houseId") String houseId);


}
