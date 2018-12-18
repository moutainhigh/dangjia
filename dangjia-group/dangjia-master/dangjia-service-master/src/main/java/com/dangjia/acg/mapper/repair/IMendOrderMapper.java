package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendOrderMapper extends Mapper<MendOrder>{

    /**查询未处理退人工*/
    List<MendOrder> backWorker(@Param("houseId") String houseId);

    /**查询未处理补人工*/
    List<MendOrder> untreatedWorker(@Param("houseId") String houseId);

    /**查询退货单*/
    List<MendOrder> materialBackState(@Param("houseId") String houseId);

    /**查询补货单*/
    List<MendOrder> materialOrderState(@Param("houseId") String houseId);

}