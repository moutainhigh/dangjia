package com.dangjia.acg.api.app.other;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 9:43
 */
@FeignClient("dangjia-service-master")
@Api(value = "首页功能", description = "首页功能")
public interface IndexPageAPI {

    @PostMapping("/app/other/indexPage/houseDetails")
    @ApiOperation(value = "施工现场详情", notes = "施工现场详情")
    ServerResponse houseDetails(@RequestParam("houseId") String houseId);
}
