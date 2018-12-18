package com.dangjia.acg.api.member;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Customer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2018/11/09
 * Time: 11:00
 */
@FeignClient("dangjia-service-master")
@Api(value = "客服接口", description = "客服接口")
public interface CustomerAPI {

    @PostMapping("/member/customer/add")
    @ApiOperation(value = "添加客服提交信息", notes = "添加客服提交信息")
    ServerResponse addCustomer(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("customer") Customer customer,
            @RequestParam("imageurl") String imageurl);
}
