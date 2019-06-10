package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.dto.deliver.SupplierDeliverDTO;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:25
 */
@Repository
public interface ISplitDeliverMapper extends Mapper<SplitDeliver> {

    /**
     * 授权大管家收货
     */
    void supState(@Param("splitDeliverId") String splitDeliverId);

    List<WebSplitDeliverItemDTO> getWebSplitDeliverList(@Param("applyState") Integer applyState,
                                                        @Param("searchKey") String searchKey,
                                                        @Param("beginDate") String beginDate,
                                                        @Param("endDate") String endDate);
    List<String> getSupplierGoodsId( @Param("houseId") String houseId, @Param("productSn") String productSn);

    /*根据供应商id查询要货列表/模糊查询要货单列表*/
    List<WebSplitDeliverItemDTO> getOrderSplitList(@Param("supplierId") String supplierId,
                                                   @Param("searchKey") String searchKey,
                                                   @Param("beginDate") String beginDate,
                                                   @Param("endDate") String endDate);

    /*供应商查看货单详情*/
    List<WebSplitDeliverItemDTO> splitDeliverList(@Param("splitDeliverId") String splitDeliverId);

    /*供应商查看货单列表*/
    List<SupplierDeliverDTO> mendDeliverList(@Param("supplierId") String supplierId,
                                             @Param("shipAddress")String shipAddress,
                                             @Param("beginDate") String beginDate,
                                             @Param("endDate") String endDate,
                                             @Param("applyState") int applyState);
    /*合併結算已结算*/
    SplitDeliver selectClsd(@Param("id") String id,
                            @Param("shipAddress")String shipAddress,
                            @Param("beginDate") String beginDate,
                            @Param("endDate") String endDate);

}
