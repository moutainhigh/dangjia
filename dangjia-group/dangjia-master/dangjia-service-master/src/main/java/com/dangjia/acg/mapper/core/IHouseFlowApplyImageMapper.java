package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlowApplyImage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 申请的图片
 * zmj
 */
@Repository
public interface IHouseFlowApplyImageMapper extends Mapper<HouseFlowApplyImage> {
    List<HouseFlowApplyImage> getHouseFlowApplyImageList(@Param("workerTypeId") String workerTypeId,
                                                         @Param("workerType") String workerType, @Param("houseId") String houseId,
                                                         @Param("houseFlowId") String houseFlowId, @Param("imageSign") String imageSign);
}
