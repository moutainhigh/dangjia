package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/9 0009
 * Time: 17:40
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台小区接口", description = "后台小区接口")
public interface WebModelingVillageAPI {

    @PostMapping("web/village/getCityList")
    @ApiOperation(value = "城市列表", notes = "城市列表")
    ServerResponse getCityList();

    @PostMapping("web/village/getVillageList")
    @ApiOperation(value = "根据城市获取小区列表", notes = "根据城市获取小区列表")
    ServerResponse getVillageList(@RequestParam("request") HttpServletRequest request, @RequestParam("cityId") String cityId);

    @PostMapping("web/village/getVillageAllListByCityId")
    @ApiOperation(value = "指定城市id查询小区", notes = "指定城市id查询小区")
    ServerResponse getVillageAllListByCityId(@RequestParam("request") HttpServletRequest request,@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId,@RequestParam("likeVillageName")String likeVillageName);

    @PostMapping("web/village/setVillage")
    @ApiOperation(value = "新增或更新小区", notes = "新增或更新小区")
    ServerResponse setVillage(@RequestParam("request") HttpServletRequest request,@RequestParam("jsonStr")String jsonStr);

    @PostMapping("web/village/getLayoutList")
    @ApiOperation(value = "根据小区获取户型列表", notes = "根据小区获取户型列表")
    ServerResponse getLayoutList(@RequestParam("request") HttpServletRequest request, @RequestParam("villageId") String villageId);

    @PostMapping("web/village/getHouseList")
    @ApiOperation(value = "根据户型获取房子列表", notes = "根据户型获取房子列表")
    ServerResponse getHouseList(@RequestParam("request") HttpServletRequest request, @RequestParam("modelingLayoutId") String modelingLayoutId);

}
