package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(description = "推荐查询接口")
@FeignClient("dangjia-service-recommend")
public interface RecommendQueryAPI {

    @PostMapping("/recommend/query/page")
    @ApiOperation(value = "推荐查询", notes = "")
    ServerResponse queryRecommendPage(@RequestParam("userToken")String userToken,
                                  @RequestParam("pageDTO") PageDTO pageDTO);
}
