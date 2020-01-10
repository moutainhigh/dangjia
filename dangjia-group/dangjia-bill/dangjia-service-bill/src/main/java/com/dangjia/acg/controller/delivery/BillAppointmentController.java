package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.BillAppointmentAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.BillAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:30
 */
@RestController
public class BillAppointmentController implements BillAppointmentAPI {

    @Autowired
    private BillAppointmentService billAppointmentService;

    @Override
    @ApiMethod
    public ServerResponse queryAppointment(HttpServletRequest request, PageDTO pageDTO, String houseId, String userToken) {
        return billAppointmentService.queryAppointment(pageDTO, houseId, userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse insertAppointment(HttpServletRequest request,String userToken,String jsonStr,String reservationDeliverTime,String orderIds) {
        return billAppointmentService.insertAppointment(userToken,jsonStr,reservationDeliverTime,orderIds);
    }

    @Override
    @ApiMethod
    public ServerResponse queryReserved(HttpServletRequest request,PageDTO pageDTO, String houseId) {
        return billAppointmentService.queryReserved(pageDTO,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateReserved(HttpServletRequest request, String orderSplitItemId, String productId) {
        return billAppointmentService.updateReserved(orderSplitItemId,productId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateReservationDeliverTime(HttpServletRequest request, String orderSplitItemId, Date reservationDeliverTime) {
        return billAppointmentService.updateReservationDeliverTime(orderSplitItemId, reservationDeliverTime);
    }
}
