package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderDTO;
import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderItemDTO;
import com.dangjia.acg.dto.deliver.SplitReportSupplierDTO;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
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
public interface IOrderSplitItemMapper extends Mapper<OrderSplitItem> {

    Double getOrderSplitPrice(@Param("orderSplitId")String orderSplitId);

    void setSupplierId(@Param("id") String id, @Param("splitDeliverId") String splitDeliverId);

    /**确认收货更新收货数量*/
    void affirmSplitDeliver(@Param("splitDeliverId") String splitDeliverId);



    /********************查看统计 开始**********************/

    List<SplitReportSupplierDTO> getSplitReportSuppliers(@Param("houseId")String houseId);

    List<SplitReportDeliverOrderDTO> getSplitReportDeliverOrders(@Param("houseId")String houseId,@Param("supplierId")String supplierId);

    List<SplitReportDeliverOrderItemDTO> getSplitReportDeliverOrderItems(@Param("number")String number);

    /*指定供应商所有发货的房子*/
    List<SplitReportSupplierDTO> getSplitReportHouse(@Param("supplierId") String supplierId);


    //商品维度
    List<SplitReportDeliverOrderItemDTO> getSplitReportGoodsOrderItems(@Param("houseId")String houseId);
    List<SplitReportSupplierDTO> getSplitReportGoodsSuppliers(@Param("houseId")String houseId,@Param("productSn")String productSn);
    /********************查看统计 结束**********************/

}
