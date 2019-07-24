package com.dangjia.acg.api.sale;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 销售用户模块
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/7/19 2:07 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售用户模块", description = "销售用户模块")
public interface SaleUserAPI {

    @PostMapping("sale/user/demo")
    @ApiOperation(value = "样例描叙", notes = "样例描叙")
    ServerResponse demo(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("demo") Object demo);
}
