package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.refund.ReturnWorkOrderDTO;
import com.dangjia.acg.modle.repair.MendOrderCheck;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 15:10
 */
@Repository
public interface IBillMendOrderCheckMapper extends Mapper<MendOrderCheck> {
    /**
     * 查询申请退人工单
     * @param houseId
     * @param type
     * @return
     */
    List<ReturnWorkOrderDTO> queryReturnWorkerList(@Param("houseId") String houseId,@Param("type") String type);

    /**
     * 退人工详情页面
     * @param repairWorkOrderId
     * @return
     */
    ReturnWorkOrderDTO queryReturnWorkerInfo(@Param("repairWorkOrderId") String repairWorkOrderId);

}
