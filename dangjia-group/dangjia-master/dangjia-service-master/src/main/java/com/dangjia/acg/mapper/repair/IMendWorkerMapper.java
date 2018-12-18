package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendWorker;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendWorkerMapper extends Mapper<MendWorker>{

    List<MendWorker> byMendOrderId(@Param("mendOrderId") String mendOrderId);
}