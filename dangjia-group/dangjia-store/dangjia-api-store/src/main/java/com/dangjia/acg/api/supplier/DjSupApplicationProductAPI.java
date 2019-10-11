package com.dangjia.acg.api.supplier;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

@Api(description = "供应商申请商品表")
@FeignClient("dangjia-service-store")
public interface DjSupApplicationProductAPI {

}
