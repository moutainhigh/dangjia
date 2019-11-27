package com.dangjia.acg.mapper.delivery;


import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.order.DOrderFineInfoDTO;
import com.dangjia.acg.dto.order.DOrderInfoDTO;
import com.dangjia.acg.dto.order.DecorationCostDTO;
import com.dangjia.acg.dto.order.DecorationCostItemDTO;
import com.dangjia.acg.modle.deliver.Order;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 14/10/2019
 * Time: 下午 3:57
 */
@Repository
public interface IBillDjDeliverOrderMapper extends Mapper<Order> {

    /**
     * 预约发货店铺对象
     * @param houseId
     * @return
     */
    List<OrderStorefrontDTO> queryDjDeliverOrderStorefront(@Param("houseId") String houseId);


    /**
     * 预约发货商品
     * @param orderId
     * @return
     */
    List<AppointmentDTO> queryAppointment(@Param("orderId") String orderId);


    List<HouseFlowDataDTO> queryApplyDec();


    List<HouseFlowDataDTO> queryApplyPayState(@Param("houseId") String houseId);


    /**
     * 已预约发货店铺对象
     * @param houseId
     * @return
     */
    List<OrderStorefrontDTO> queryReservedStorefront(@Param("houseId") String houseId);

    /**
     * 已预约发货商品
     * @param orderSplitId
     * @return
     */
    List<AppointmentDTO> queryReserved(@Param("orderSplitId") String orderSplitId);


    /**
     * 取消预约
     * @param orderSplitId
     */
    int cancelBooking(@Param("orderSplitId") String orderSplitId);

    /**
     * 查询装修状态
     * @param houseId
     * @return
     */
    List<WorkerTypeDTO> queryType(@Param("houseId") String houseId);

    /**
     * 查询当前花费汇总信息(根据类别汇总)
     * @param houseId 房子ID
     * @param labelValId 标签ID
     * @return
     */
    List<DecorationCostDTO> searchDecorationCostList(@Param("houseId") String  houseId, @Param("labelValId") String  labelValId);

    /**
     * 查询当前房子的总花费
     * @param houseId
     * @return
     */
    DecorationCostDTO searchDecorationTotalCost(@Param("houseId") String  houseId);
    /**
     * 查询当前花费详细花费信息
     * @param categoryId 类别ID
     * @param houseId 房子ID
     * @param labelValId 标签 ID
     * @return
     */
    List<DecorationCostItemDTO> searchDecorationCostDetailList(@Param("categoryId") String  categoryId,@Param("houseId") String  houseId,
                                                               @Param("labelValId") String  labelValId);

    /**
     * 查询按分类标签汇总花费信息
     * @param houseId
     * @return
     */
    List<DecorationCostDTO> searchDecorationCategoryLabelList(@Param("houseId") String  houseId);


    List<DOrderInfoDTO> queryOrderInfo(Map<String,Object> map);


    List<DOrderFineInfoDTO> queryOrderFineInfo(@Param("orderId") String  orderId);


    List<DjDeliverOrderDTO> selectDeliverOrderByHouse(@Param("cityId") String cityId, @Param("houseId") String houseId, @Param("orderStatus") String orderStatus);

    AppOrderDetailDTO  selectOrderDetailById(@Param("houseId") String  houseId, @Param("orderId") String  orderId);

    List<AppOrderItemDetailDTO> selectOrderItemDetailById(@Param("orderId") String  orderId,@Param("orderStatus") Integer orderStatus);

    List<CostDetailDTO> queryStevedorage(@Param("orderId") String  orderId);

    List<CostDetailDTO> queryTransportationCost(@Param("orderId") String  orderId);


    /**
     * 我的订单待发货店铺对象
     * @param houseId
     * @return
     */
    List<OrderStorefrontDTO> queryDeliverOrderHump(@Param("houseId") String houseId);

    /**
     * 我的订单待付款订单店铺对象
     */
    List<OrderStorefrontDTO> queryDeliverOrderObligation(@Param("houseId") String houseId);

    /**
     * 我的订单待发货商品
     * @param orderId
     * @return
     */
    List<AppointmentDTO> queryAppointmentHump(@Param("orderId") String orderId);

    List<AppointmentDTO> queryDeliverOrderItemObligation(@Param("orderId") String orderId);


    List<DjSplitDeliverOrderDTO> querySplitDeliverByHouse(@Param("cityId") String cityId, @Param("houseId") String houseId, @Param("orderStatus") String orderStatus);

    List<OrderStorefrontDTO> queryPaymentToBeMade(@Param("orderId") String orderId);

    List<DjSplitDeliverOrderDTO> queryAppOrderList(@Param("cityId") String cityId,
                                                   @Param("houseId") String houseId,
                                                   @Param("orderStatus") Integer orderStatus,
                                                   @Param("arr") String[] arr);


    String queryValueIdArr(@Param("id") String id);

    List<OrderStorefrontDTO> queryHumpDetail(@Param("orderId") String orderId);
}
