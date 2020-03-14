package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.refund.ReturnWorkOrderDTO;
import com.dangjia.acg.modle.repair.ChangeOrder;
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
public interface IBillChangeOrderMapper extends Mapper<ChangeOrder> {
    /**
     * 查询申请退人工单
     * @param houseId
     * @param memberId type=1为工匠ID，type=2为业主ID
     * @param type
     * @return
     */
    List<ReturnWorkOrderDTO> queryReturnWorkerList(@Param("houseId") String houseId,@Param("memberId") String memberId,@Param("type") String type);

    /**
     * 退人工详情页面
     * @param repairWorkOrderId
     * @return
     */
    ReturnWorkOrderDTO queryReturnWorkerInfo(@Param("repairWorkOrderId") String repairWorkOrderId);

}
