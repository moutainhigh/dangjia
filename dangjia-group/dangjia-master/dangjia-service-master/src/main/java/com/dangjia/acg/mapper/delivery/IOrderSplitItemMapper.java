package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderDTO;
import com.dangjia.acg.dto.deliver.SplitReportDeliverOrderItemDTO;
import com.dangjia.acg.dto.deliver.SplitReportSupplierDTO;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.sup.SupplierProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderSplitItemMapper extends Mapper<OrderSplitItem> {

    Double getOrderSplitPrice(@Param("orderSplitId")String orderSplitId);
    Double getSplitDeliverSellPrice(@Param("splitDeliverId")String splitDeliverId);

    //查询当前订单下的总收货量，计算运费
    Double getOrderSplitReceiverNum(@Param("splitDeliverId")String splitDeliverId);


    void setSupplierId(@Param("id") String id, @Param("splitDeliverId") String splitDeliverId);

    /**确认收货更新收货数量*/
    void affirmSplitDeliver(@Param("splitDeliverId") String splitDeliverId);



    /********************查看统计 开始**********************/

    List<SplitReportSupplierDTO> getReportAdressSuppliers(@Param("addressId") String addressId,@Param("storefrontId") String storefrontId);

    List<SplitReportSupplierDTO> getSplitReportSuppliers(@Param("houseId")String houseId,@Param("storefrontId") String storefrontId);

    List<SplitReportDeliverOrderDTO> getSplitReportDeliverOrders(@Param("houseId")String houseId,@Param("supplierId")String supplierId);

    List<SplitReportDeliverOrderItemDTO> getSplitReportDeliverOrderItems(@Param("number")String number);

    /*指定供应商所有发货的房子*/
    List<SplitReportSupplierDTO> getSplitReportHouse(@Param("supplierId") String supplierId);


    //商品维度
    List<SplitReportDeliverOrderItemDTO> getSplitReportGoodsOrderItems(@Param("houseId")String houseId);
    List<SplitReportSupplierDTO> getSplitReportGoodsSuppliers(@Param("houseId")String houseId,@Param("productSn")String productSn);
    /********************查看统计 结束**********************/

    /**
     * 查当前房子下对应工匠的要货记录
     * @param houseId
     * @param workerId
     * @return
     */
    List<OrderSplitItemDTO> getOrderItemListByhouseMemberId(@Param("houseId")String houseId,@Param("workerId") String workerId,@Param("searchKey") String searchKey);

    List<OrderSplitItemDTO> getSplitOrderItemBySplitOrderId(@Param("orderSplitId") String orderSplitId,@Param("splitDeliverId") String splitDeliverId);

    SupplierProduct getsupplierProductById(@Param("storefrontId") String storefrontId,@Param("supplierId") String supplierId,@Param("productId") String productId);

    List<Map<String,Object>> selectSupListBySplitId(@Param("orderSplitId") String orderSplitId,@Param("splitDeliverId") String splitDeliverId);

    //修改发货单号到发货单明细表中去
    void updateSplitDeliverIdByInfo(@Param("splitDeliverId") String splitDeliverId,@Param("orderSplitId") String orderSplitId,
                                    @Param("supplierId") String supplierId,@Param("oldSplitDeliverId") String oldSplitDeliverId);

    Double getSupPorterage(@Param("storefrontId") String storefrontId,@Param("supId") String supId,@Param("productId") String productId);
}
