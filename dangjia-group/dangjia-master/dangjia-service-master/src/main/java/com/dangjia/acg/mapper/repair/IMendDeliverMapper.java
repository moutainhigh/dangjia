package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import com.dangjia.acg.dto.repair.MendDeliverDTO;
import com.dangjia.acg.modle.repair.MendDeliver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendDeliverMapper extends Mapper<MendDeliver>{

    /*供应商查看货单列表*/
    List<SupplierDeliverDTO> mendDeliverList(@Param("supplierId") String supplierId,
                                             @Param("shipAddress")String shipAddress,
                                             @Param("beginDate") String beginDate,
                                             @Param("endDate") String endDate,
                                             @Param("applyState") int applyState);

}