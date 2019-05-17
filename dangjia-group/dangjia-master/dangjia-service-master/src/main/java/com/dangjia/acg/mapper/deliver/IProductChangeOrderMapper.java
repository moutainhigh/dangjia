package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.ProductChangeOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 */
@Repository
public interface IProductChangeOrderMapper extends Mapper<ProductChangeOrder> {

    /**根据houseId查询商品换货订单*/
    List<ProductChangeOrder> queryOrderByHouseId(@Param("houseId") String houseId, @Param("type") String type);

    /**
     * 按state 和 收货地址 搜索(补退换货流程)
     * @param houseId
     * @param beginDate
     * @param endDate
     * @param likeAddress
     * @return
     */
    List<ProductChangeOrder> queryOrderByStateAndLikeAddress(@Param("houseId") String houseId,
                                                  @Param("beginDate") String beginDate,
                                                  @Param("endDate") String endDate,
                                                  @Param("likeAddress") String likeAddress);
}
