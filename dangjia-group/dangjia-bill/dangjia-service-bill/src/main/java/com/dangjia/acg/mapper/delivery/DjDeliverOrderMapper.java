package com.dangjia.acg.mapper.delivery;


import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
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
public interface DjDeliverOrderMapper extends Mapper<DjDeliverOrder> {

    /**
     * 预约发货店铺对象
     * @param houseId
     * @return
     */
    List<OrderStorefrontDTO> queryDjDeliverOrderStorefront(@Param("houseId") String houseId);


    List<AppointmentDTO> queryAppointment(@Param("orderId") String orderId);



}
