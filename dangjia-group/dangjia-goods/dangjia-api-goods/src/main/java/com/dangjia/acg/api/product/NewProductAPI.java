package com.dangjia.acg.api.product;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

@Api(description = "商品管理接口")
@FeignClient("dangjia-service-goods")
public interface NewProductAPI {
}
