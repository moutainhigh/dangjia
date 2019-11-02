package com.dangjia.acg.mapper.delivery;


import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.HouseFlowDataDTO;
import com.dangjia.acg.dto.delivery.HouseFlowInfoDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.modle.deliver.Order;
import org.apache.ibatis.annotations.Param;
import com.dangjia.acg.modle.delivery.DjDeliverOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


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

}
