package com.dangjia.acg.api.sale.store;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/22
 * Time: 14:23
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售员工详情接口", description = "销售员工详情接口")
public interface EmployeeDetailsAPI {

    @PostMapping(value = "sale/store/monthlyTarget")
    @ApiOperation(value = "制定员工月目标", notes = "制定员工月目标")
    ServerResponse setMonthlyTarget(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("time") Date time,
                                    @RequestParam("target") Integer target);

}
