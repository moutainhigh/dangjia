package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:29
 */
@Api(description = "预约发货接口")
@FeignClient("dangjia-service-bill")
public interface BillAppointmentAPI {



    @PostMapping("/app/billAppointment/queryAppointment")
    @ApiOperation(value = "我的预约", notes = "我的预约")
    ServerResponse queryAppointment(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("houseId") String houseId);


    @PostMapping("/app/billAppointment/insertAppointment")
    @ApiOperation(value = "预约发货", notes = "预约发货")
    ServerResponse insertAppointment(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("jsonStr") String jsonStr);

    @PostMapping("/app/billAppointment/queryReserved")
    @ApiOperation(value = "已预约", notes = "已预约")
    ServerResponse queryReserved(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("houseId") String houseId);

    @PostMapping("/app/billAppointment/updateReserved")
    @ApiOperation(value = "取消预约", notes = "取消预约")
    ServerResponse updateReserved(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("orderSplitId") String orderSplitId);
}
