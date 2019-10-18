package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDetailsDTO;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlipDetails;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:57
 */
@Repository
public interface DjDeliveryReturnSlipDetailsMapper extends Mapper<DjDeliveryReturnSlipDetails> {

//    List<DjDeliveryReturnSlipDetailsDTO> queryOrderInformation(@Param("id") String id);

    List<DjDeliveryReturnSlipDetailsDTO> queryDeliverOrderInformation(@Param("orderSplitId") String orderSplitId);

    List<DjDeliveryReturnSlipDetailsDTO> queryRepairOrderInformation(@Param("mendOrderId") String mendOrderId);

    DjDeliveryReturnSlipDetailsDTO queryWorkerInfByHouseId(@Param("houseId") String houseId);
}
