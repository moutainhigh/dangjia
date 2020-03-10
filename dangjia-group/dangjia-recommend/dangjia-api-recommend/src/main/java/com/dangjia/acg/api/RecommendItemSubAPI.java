package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "推荐参考子项接口")
@FeignClient("dangjia-service-recommend")
public interface RecommendItemSubAPI {

    @PostMapping("/recommend/item/sub/list")
    @ApiOperation(value = "查询推荐参考子项列表", notes = "")
    ServerResponse queryRecommendItemSubList(@RequestParam("itemId") String itemId,
                                             @RequestParam("itemSubName") String itemSubName);
}
