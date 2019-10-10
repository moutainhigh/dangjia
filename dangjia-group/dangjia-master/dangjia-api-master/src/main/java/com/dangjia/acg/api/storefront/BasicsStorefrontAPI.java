package com.dangjia.acg.api.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.model.storefront.Storefront;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * chenyufeng  2019-10-08  店铺管理
 */
@Api(description = "店铺管理接口")
@FeignClient("dangjia-service-goods")
public interface BasicsStorefrontAPI {

    @PostMapping("/web/addStorefront")
    @ApiOperation(value = "新增店铺信息", notes = "新增店铺信息")
    ServerResponse addStorefront(@RequestParam("userToken") String userToken, Storefront  storefront);


    @PostMapping("/web/updateStorefront")
    @ApiOperation(value = "修改店铺信息", notes = "修改店铺信息")
    ServerResponse updateStorefront(@RequestParam("userToken") String userToken, Storefront  storefront);


}
