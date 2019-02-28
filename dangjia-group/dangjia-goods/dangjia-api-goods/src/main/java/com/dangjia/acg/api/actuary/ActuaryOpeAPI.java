package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/2/26 0026
 * Time: 10:44
 */
@Api(description = "新精算确认")
@FeignClient("dangjia-service-goods")
public interface ActuaryOpeAPI {

    /**
     * 精算详情
     */
    @PostMapping("/actuary/actuaryOpe/actuary")
    @ApiOperation(value = "精算详情", notes = "精算详情")
    ServerResponse actuary(@RequestParam("houseId") String houseId
            , @RequestParam("cityId")String cityId, @RequestParam("type")Integer type);

}
