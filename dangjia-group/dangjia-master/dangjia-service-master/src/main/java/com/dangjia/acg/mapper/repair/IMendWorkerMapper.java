package com.dangjia.acg.mapper.repair;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.modle.repair.MendWorker;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendWorkerMapper extends Mapper<MendWorker>{

    List<MendWorker> byMendOrderId(@Param("mendOrderId") String mendOrderId);

    List<MendWorker> mendWorkerList(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    List<MendWorker> houseMendWorkerList(@Param("houseId") String houseId);

    /*更新人工商品*/
    void updateMendWorkerById(@Param("lists") JSONArray lists);
}