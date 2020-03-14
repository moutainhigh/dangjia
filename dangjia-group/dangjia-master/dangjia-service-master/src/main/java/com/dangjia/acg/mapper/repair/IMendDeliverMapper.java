package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import com.dangjia.acg.dto.repair.MendDeliverDTO;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface IMendDeliverMapper extends Mapper<MendDeliver> {

    /*供应商查看货单列表*/
    List<SupplierDeliverDTO> mendDeliverList(@Param("supplierId") String supplierId,
                                             @Param("shipAddress") String shipAddress,
                                             @Param("beginDate") String beginDate,
                                             @Param("endDate") String endDate,
                                             @Param("applyState") Integer applyState);

    /*合併結算已结算*/
    MendDeliver selectClsd(@Param("id") String id,
                           @Param("shipAddress") String shipAddress,
                           @Param("beginDate") String beginDate,
                           @Param("endDate") String endDate);

    /**
     * 退货单查看详情
     *
     * @param id
     * @return
     */
    List<MendMateriel> mendDeliverDetail(@Param("id") String id);

    List<MendDeliverDTO> searchReturnRefundSplitList(@Param("storefrontId") String storefrontId,@Param("state") Integer state,@Param("likeAddress") String likeAddress);

    Integer searchReturnRefundSplitCount(@Param("storefrontId") String storefrontId,@Param("state") Integer state);

    /**
     * 查询符合条件的数据信息
     * @param paramNodeKey 获取需处理的判断的时间值的key
     * @return
     */
    List<Map<String,Object>> queryRefundJobList(@Param("paramNodeKey") String paramNodeKey);

    /**
     * 等待平台申请介入时间
     * @param paramNodeKey
     * @return
     */
    List<Map<String,Object>> queryRefundDeliverJobList(@Param("nodeCode") String nodeCode,@Param("queryRefundDeliverJobList") String paramNodeKey);

}