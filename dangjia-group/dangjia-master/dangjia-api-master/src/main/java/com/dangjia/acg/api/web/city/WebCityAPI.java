package com.dangjia.acg.api.web.city;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.other.City;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/1
 * Time: 15:29
 */
@FeignClient("dangjia-service-master")
@Api(value = "城市维护", description = "城市维护")
public interface WebCityAPI {

    @PostMapping("web/city/getCityList")
    @ApiOperation(value = "城市列表", notes = "城市列表")
    ServerResponse getCityList(@RequestParam("cityId") String cityId);

    @PostMapping("web/city/addCity")
    @ApiOperation(value = "添加城市", notes = "添加城市")
    ServerResponse addCity(@RequestParam("city") City city);

    @PostMapping("web/city/delCity")
    @ApiOperation(value = "删除城市", notes = "删除城市")
    ServerResponse delCity(@RequestParam("cityId") String cityId);

    @PostMapping("web/city/updateCity")
    @ApiOperation(value = "修改城市", notes = "修改城市")
    ServerResponse updateCity(@RequestParam("city") City city);


}
