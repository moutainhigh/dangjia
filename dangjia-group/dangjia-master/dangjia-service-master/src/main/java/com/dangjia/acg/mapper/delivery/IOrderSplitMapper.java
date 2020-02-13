package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.deliver.OrderSplitDTO;
import com.dangjia.acg.modle.deliver.OrderSplit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderSplitMapper extends Mapper<OrderSplit> {

    void cancelOrderSplit(@Param("orderSplitId") String orderSplitId);

    List<String> getOrderProduct(@Param("houseId") String houseId, @Param("productType") String productType, @Param("workerTypeId") String workerTypeId, @Param("memberId") String memberId);

    List<String> getOrderCategory(@Param("houseId") String houseId, @Param("productType") String productType, @Param("workerTypeId") String workerTypeId, @Param("memberId") String memberId);

    List<OrderSplitDTO> searchOrderSplistByAddressId(@Param("addressId") String addressId,@Param("houseId") String houseId,@Param("storefrontId") String storefrontId);
}
