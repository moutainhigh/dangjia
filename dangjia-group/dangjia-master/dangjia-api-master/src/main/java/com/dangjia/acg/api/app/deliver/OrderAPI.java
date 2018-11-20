package com.dangjia.acg.api.app.deliver;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * author: Ronalcheng
 * Date: 2018/11/9 0009
 * Time: 10:55
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主订单接口", description = "业主订单接口")
public interface OrderAPI {
}
