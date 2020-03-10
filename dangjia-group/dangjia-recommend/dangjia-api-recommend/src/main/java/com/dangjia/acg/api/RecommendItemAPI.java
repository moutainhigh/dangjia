package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "推荐参考主项接口")
@FeignClient("dangjia-service-recommend")
public interface RecommendItemAPI {

    @PostMapping("/recommend/item/list")
    @ApiOperation(value = "查询推荐参考主项列表", notes = "")
    ServerResponse queryRecommendItemList(@RequestParam("itemName") String itemName);
}
