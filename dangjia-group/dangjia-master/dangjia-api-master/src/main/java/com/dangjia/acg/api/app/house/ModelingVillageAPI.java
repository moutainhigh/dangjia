package com.dangjia.acg.api.app.house;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/2 0002
 * Time: 19:50
 */
@FeignClient("dangjia-service-master")
@Api(value = "房产接口", description = "房产接口")
public interface ModelingVillageAPI {

    /**
     * 根据城市查询小区
     */
    @PostMapping("app/house/modelingVillage/getAllVillageByCity")
    @ApiOperation(value = "切换房产", notes = "切换房产")
    ServerResponse getAllVillageByCity( @RequestParam("cityId") String cityId);

}
