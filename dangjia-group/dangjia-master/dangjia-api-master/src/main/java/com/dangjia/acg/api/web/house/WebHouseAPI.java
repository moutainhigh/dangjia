package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:28
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台房产接口", description = "后台房产接口")
public interface WebHouseAPI {

    @PostMapping("web/house/getList")
    @ApiOperation(value = "装修列表", notes = "装修列表")
    ServerResponse getList(@RequestParam("request") HttpServletRequest request,@RequestParam("memberId") String memberId);

    @PostMapping("web/house/startWorkPage")
    @ApiOperation(value = "确认开工页面", notes = "确认开工页面")
    ServerResponse startWorkPage(@RequestParam("request") HttpServletRequest request,@RequestParam("houseId") String houseId);

    @PostMapping("web/house/startWork")
    @ApiOperation(value = "确认开工", notes = "确认开工")
    ServerResponse startWork(@RequestParam("request") HttpServletRequest request,@RequestParam("house") HouseDTO houseDTO);
}
