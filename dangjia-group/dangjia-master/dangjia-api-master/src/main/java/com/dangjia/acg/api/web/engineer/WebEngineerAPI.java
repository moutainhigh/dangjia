package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:32
 */
@FeignClient("dangjia-service-master")
@Api(value = "工程部功能", description = "工程部功能")
public interface WebEngineerAPI {

    @PostMapping(value = "web/engineer/getHouseList")
    @ApiOperation(value = "工地列表", notes = "工地列表")
    ServerResponse getHouseList(@RequestParam("pageDTO")PageDTO pageDTO);

    @PostMapping(value = "web/engineer/artisanList")
    @ApiOperation(value = "工匠列表", notes = "工匠列表")
    ServerResponse artisanList(@RequestParam("pageDTO") PageDTO pageDTO);
}
