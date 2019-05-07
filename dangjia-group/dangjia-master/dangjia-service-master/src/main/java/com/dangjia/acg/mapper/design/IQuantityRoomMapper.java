package com.dangjia.acg.mapper.design;

import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.modle.design.QuantityRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author Ruking.Cheng
 * @descrilbe 设计相关操作记录表（目前就只有量房）
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 11:56 AM
 */
@Repository
public interface IQuantityRoomMapper extends Mapper<QuantityRoom> {
    QuantityRoomDTO getQuantityRoom(@Param("houseId") String houseId, @Param("type") Integer type);

    QuantityRoomDTO getIdQuantityRoom(@Param("quantityRoomId") String quantityRoomId);

    List<QuantityRoomDTO> getQuantityRoomList(@Param("houseId") String houseId, @Param("type") Integer type);
}
