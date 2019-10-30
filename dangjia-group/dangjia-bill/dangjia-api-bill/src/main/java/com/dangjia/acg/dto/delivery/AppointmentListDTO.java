package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 上午 10:56
 */
@Data
public class AppointmentListDTO {

    private OrderStorefrontDTO orderStorefrontDTO;

    private List<AppointmentDTO> appointmentDTOS;
}
