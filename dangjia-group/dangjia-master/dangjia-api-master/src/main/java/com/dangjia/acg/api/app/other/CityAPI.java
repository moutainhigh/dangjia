package com.dangjia.acg.api.app.other;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 16:22
 */
@FeignClient("dangjia-service-master")
@Api(value = "城市地区接口", description = "城市地区接口")
public interface CityAPI {

    @PostMapping("/app/other/city/getAllCity")
    @ApiOperation(value = "获取所有城市", notes = "获取所有城市列表")
    ServerResponse getAllCity();
}
