package com.dangjia.acg.api.product;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Api(description = "商品标签接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsLabelAPI {
}
