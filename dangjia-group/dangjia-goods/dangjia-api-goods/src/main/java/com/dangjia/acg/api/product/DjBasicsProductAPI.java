package com.dangjia.acg.api.product;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Api(description = "商品表接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsProductAPI {
}
