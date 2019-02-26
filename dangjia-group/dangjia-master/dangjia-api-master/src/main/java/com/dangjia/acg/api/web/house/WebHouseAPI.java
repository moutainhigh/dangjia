package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.modle.house.House;
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
    ServerResponse getList(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("pageDTO") PageDTO pageDTO,
                           @RequestParam("searchKey") String searchKey,
                           @RequestParam("memberId") String memberId);

    @PostMapping("web/house/startWorkPage")
    @ApiOperation(value = "确认开工页面", notes = "确认开工页面")
    ServerResponse startWorkPage(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId);

    @PostMapping("web/house/startWork")
    @ApiOperation(value = "确认开工", notes = "确认开工")
    ServerResponse startWork(@RequestParam("request") HttpServletRequest request, @RequestParam("house") HouseDTO houseDTO, @RequestParam("members") String members, @RequestParam("prefixs") String prefixs);

    @PostMapping("web/house/setHouseInfo")
    @ApiOperation(value = "设置房子为精选或者休眠", notes = "设置房子为精选或者休眠")
    ServerResponse setHouseInfo(@RequestParam("request") HttpServletRequest request, @RequestParam("house") House house);

    @PostMapping("web/house/queryConstructionRecord")
    @ApiOperation(value = "施工记录", notes = "施工记录")
    ServerResponse queryConstructionRecord(@RequestParam("houseId") String houseId, @RequestParam("pageDTO") PageDTO pageDTO, @RequestParam("workerTypeId") String workerTypeId);

    /**
     * 根据房子装修状态查询所有的房子
     *
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工
     * @return
     */
    @PostMapping("web/house/getAllHouseByVisitState")
    @ApiOperation(value = "根据房子装修状态查询所有的房子", notes = "根据房子装修状态查询所有的房子")
    ServerResponse getAllHouseByVisitState(@RequestParam("visitState") Integer visitState);
}
