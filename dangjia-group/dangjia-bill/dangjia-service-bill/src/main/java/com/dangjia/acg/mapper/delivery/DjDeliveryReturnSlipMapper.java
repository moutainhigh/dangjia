package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDTO;
import com.dangjia.acg.dto.delivery.SupplierSettlementManagementDTO;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlip;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:38
 */
@Repository
public interface DjDeliveryReturnSlipMapper extends Mapper<DjDeliveryReturnSlip> {

    List<DjDeliveryReturnSlipDTO> querySupplyTaskList(@Param("supId") String supId,
                                                      @Param("searchKey") String searchKey,
                                                      @Param("invoiceStatus") String invoiceStatus);

    int setDeliveryTask(@Param("id") String id,
                        @Param("invoiceStatus") String invoiceStatus);

    List<SupplierSettlementManagementDTO> querySupplierSettlementManagement(@Param("supId") String supId,
                                                                            @Param("applyState") Integer applyState);

    List<DjDeliveryReturnSlip> querySupplierSettlementList(@Param("supId") String supId,
                                                           @Param("shopId") String shopId,
                                                           @Param("applyState") Integer applyState);
}
