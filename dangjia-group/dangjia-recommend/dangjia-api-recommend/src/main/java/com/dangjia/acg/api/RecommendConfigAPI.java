package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "推荐配置接口")
@FeignClient("dangjia-service-recommend")
public interface RecommendConfigAPI {

    @PostMapping("/recommend/config/list")
    @ApiOperation(value = "查询推荐配置列表", notes = "")
    ServerResponse queryRecommendConfigList();

    @PostMapping("/recommend/config/update")
    @ApiOperation(value = "设置单个推荐配置值", notes = "")
    ServerResponse updateRecommendConfig(@RequestParam("id") String id,
                                         @RequestParam("configCode") String configCode,
                                         @RequestParam("configValue") Integer configValue);
}
