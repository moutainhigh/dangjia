package com.dangjia.acg.api.refund;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 5/11/2019
 * Time: 下午 4:53
 */
@Api(description = "退款/售后定时任务")
@FeignClient("dangjia-service-bill")
public interface RefundAfterSalesJobAPI {

    @PostMapping("app/refund/job/returnMechantProcessTime")
    @ApiOperation(value = "店铺申请等待商家处理（到期自动处理)", notes = "店铺申请等待商家处理（到期自动处理)")
    void returnMechantProcessTime();

    @PostMapping("app/refund/job/returnPlatformInterventionTime")
    @ApiOperation(value = "店铺拒绝退货，等待申请平台介入(到期自动处理）", notes = "店铺拒绝退货，等待申请平台介入(到期自动处理）")
    void returnPlatformInterventionTime();

    @PostMapping("app/refund/job/returnPlatformProcessTime")
    @ApiOperation(value = "业主申诉后，等待平台处理(到期自动处理）", notes = "业主申诉后，等待平台处理(到期自动处理）")
    void returnPlatformProcessTime();

}
