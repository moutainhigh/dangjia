package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendOrderMapper extends Mapper<MendOrder>{


    List<MendOrder> getByChangeOrderId(@Param("changeOrderId") String changeOrderId);

    /**查询工种未处理补人工*/
    List<MendOrder> unCheckRepWorker(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    /**查询工种未处理退人工*/
    List<MendOrder> unCheckBackWorker(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    /**查询退人工单*/
    List<MendOrder> workerBackState(@Param("houseId") String houseId);

    /**查询补人工单*/
    List<MendOrder> workerOrderState(@Param("houseId") String houseId);

    /**查询未处理退人工*/
    List<MendOrder> backWorker(@Param("houseId") String houseId);

    /**查询未处理补人工*/
    List<MendOrder> untreatedWorker(@Param("houseId") String houseId);

    /**查询业主退货单*/
    List<MendOrder> landlordState(@Param("houseId") String houseId);

    /**查询退货单*/
    List<MendOrder> materialBackState(@Param("houseId") String houseId);

    /**查询补货单*/
    List<MendOrder> materialOrderState(@Param("houseId") String houseId);

    /**
     * 按state 和 收货地址 搜索
     * @param houseId
     * @param type   0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
     * @param beginDate
     * @param endDate
     * @param likeAddress
     * @return
     */
    List<MendOrder> materialByStateAndLikeAddress(@Param("houseId") String houseId,
                                    @Param("type") Integer type,
                                    @Param("beginDate") String beginDate,
                                    @Param("endDate") String endDate,
                                    @Param("likeAddress") String likeAddress);

}