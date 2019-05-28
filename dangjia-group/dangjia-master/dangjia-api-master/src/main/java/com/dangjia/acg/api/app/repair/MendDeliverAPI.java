package com.dangjia.acg.api.app.repair;


import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/24
 * Time: 16:03
 */
@FeignClient("dangjia-service-master")
@Api(value = "退货web管理接口", description = "退货web管理接口")
public interface MendDeliverAPI {

    @PostMapping(value = "app/repair/mendOrder/mendDeliverList")
    @ApiOperation(value = "供应商退货单列表", notes = "供应商退货单列表")
    ServerResponse mendDeliverList(@RequestParam("supplierId") String supplierId);

    @PostMapping(value = "app/repair/mendOrder/mendDeliverDetail")
    @ApiOperation(value = "供应商退货单详情", notes = "供应商退货单详情")
    ServerResponse mendDeliverDetail(@RequestParam("mendDeliverId") String mendDeliverId);


}
