package com.dangjia.acg.api.delivery;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

@Api(description = "订单明细表接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliverOrderItemAPI {

}
