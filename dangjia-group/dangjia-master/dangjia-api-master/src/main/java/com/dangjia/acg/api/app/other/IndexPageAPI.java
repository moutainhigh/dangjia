package com.dangjia.acg.api.app.other;

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
 * Date: 2019/1/4 0004
 * Time: 9:43
 */
@FeignClient("dangjia-service-master")
@Api(value = "首页功能", description = "首页功能")
public interface IndexPageAPI {


    /**
     * 参考列表 (1.4.0版本)
     *
     * @param request
     * @param userToken
     * @param cityId    城市ID
     * @param villageId 小区ID
     * @param square    面积
     * @param pageDTO   分页
     * @return
     */
    @PostMapping("/app/other/indexPage/queryHouseDistance")
    @ApiOperation(value = "参考列表", notes = "参考列表")
    ServerResponse queryHouseDistance(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("userToken") String userToken,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("villageId") String villageId,
                                      @RequestParam("square") Double square,
                                      @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    @PostMapping("app/house/house/queryHouseByCity")
    @ApiOperation(value = "根据城市，小区，最小最大面积查询房子", notes = "根据城市，小区，最小最大面积查询房子")
    ServerResponse queryHouseByCity(@RequestParam("userToken") String userToken,
                                    @RequestParam("cityId") String cityId,
                                    @RequestParam("villageId") String villageId,
                                    @RequestParam("minSquare") Double minSquare,
                                    @RequestParam("maxSquare") Double maxSquare,
                                    @RequestParam("houseType") Integer houseType,
                                    @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/app/other/house/details")
    @ApiOperation(value = "施工现场详情", notes = "施工现场详情")
    ServerResponse houseOtherDetails(@RequestParam("request") HttpServletRequest request, @RequestParam("houseId") String houseId);

    @PostMapping("/app/other/indexPage/houseDetails")
    @ApiOperation(value = "施工现场详情（带花费）", notes = "施工现场详情（带花费）")
    ServerResponse houseDetails(@RequestParam("request") HttpServletRequest request, @RequestParam("houseId") String houseId);

    @PostMapping("/app/other/indexPage/getHouseLabels")
    @ApiOperation(value = "工地标签详情", notes = "工地标签详情")
    ServerResponse getHouseLabels(@RequestParam("request") HttpServletRequest request, @RequestParam("houseId") String houseId);

    @PostMapping("/app/other/indexPage/jobLocation")
    @ApiOperation(value = "施工现场", notes = "施工现场")
    ServerResponse jobLocation(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("latitude") String latitude,
                               @RequestParam("longitude") String longitude,
                               @RequestParam("limit") Integer limit);


    @PostMapping("/app/other/indexPage/getRecommended")
    @ApiOperation(value = "参考花费推荐", notes = "参考花费推荐")
    ServerResponse getRecommended(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("latitude") String latitude,
                                  @RequestParam("longitude") String longitude,
                                  @RequestParam("limit") Integer limit);
}
