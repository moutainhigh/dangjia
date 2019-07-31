package com.dangjia.acg.controller.sale.store;

import com.dangjia.acg.api.sale.store.EmployeeDetailsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sale.store.EmployeeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@RestController
public class EmployeeDetailsController implements EmployeeDetailsAPI {

    @Autowired
    private EmployeeDetailsService employeeDetailsService;

    @Override
    @ApiMethod
    public ServerResponse setMonthlyTarget(HttpServletRequest request, String userId, String time, Integer target) {
        return employeeDetailsService.setMonthlyTarget(userId, time, target);
    }

    @Override
    @ApiMethod
    public ServerResponse setSalesRange(HttpServletRequest request, String userId, String buildingId) {
        return employeeDetailsService.setSalesRange(userId,buildingId);
    }

    @Override
    @ApiMethod
    public ServerResponse delMonthlyTarget(HttpServletRequest request, String monthlyTargetId) {
        return employeeDetailsService.delMonthlyTarget(monthlyTargetId);
    }

}
