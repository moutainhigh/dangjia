package com.dangjia.acg.mapper.delivery;


import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.DjDeliverOrderDTO;
import com.dangjia.acg.dto.delivery.HouseFlowDataDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.order.DOrderFineInfoDTO;
import com.dangjia.acg.dto.order.DOrderInfoDTO;
import com.dangjia.acg.dto.order.DecorationCostDTO;
import com.dangjia.acg.dto.order.DecorationCostItemDTO;
import com.dangjia.acg.modle.deliver.Order;
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
     * @param orderId
     * @return
     */
    List<AppointmentDTO> queryReserved(@Param("orderId") String orderId);


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

}
