package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.modle.design.QuantityRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;



@Repository
public interface IBillQuantityRoomMapper extends Mapper<QuantityRoom> {
    //根据房子和类型查询对应的数据
    QuantityRoom getBillQuantityRoom(@Param("houseId") String houseId,
                                     @Param("type") Integer type);


    List<QuantityRoomDTO> getQuantityRoomList(@Param("houseId") String houseId);

}
