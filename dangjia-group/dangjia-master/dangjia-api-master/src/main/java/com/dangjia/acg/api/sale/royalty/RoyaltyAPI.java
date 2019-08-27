package com.dangjia.acg.api.sale.royalty;


import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 提成配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@FeignClient("dangjia-service-master")
@Api(value = "提成配置模块", description = "提成配置模块")
public interface RoyaltyAPI {



    @PostMapping(value = "sale/royalty/queryRoyaltySurface")
    @ApiOperation(value = "查询提成配置", notes = "查询提成配置")
    ServerResponse queryRoyaltySurface(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO);


    @PostMapping(value = "sale/royalty/addRoyaltyData")
    @ApiOperation(value = "新增提成配置", notes = "新增提成配置")
    ServerResponse addRoyaltyData(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("lists") String lists);

    @PostMapping(value = "sale/royalty/queryRoyaltyData")
    @ApiOperation(value = "查询提成详细信息", notes = "查询提成详细信息")
    ServerResponse queryRoyaltyData(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String id);


    @PostMapping(value = "sale/royalty/addAreaMatch")
    @ApiOperation(value = "新增楼栋提成配置", notes = "新增楼栋提成配置")
    ServerResponse addAreaMatch(@RequestParam("request")HttpServletRequest request,
                                @RequestParam("lists")String lists,
                                @RequestParam("villageId")String villageId,
                                @RequestParam("villageName")String villageName,
                                @RequestParam("buildingName")String buildingName,
                                @RequestParam("buildingId")String buildingId,
                                @RequestParam("resourceId")String resourceId);


    @PostMapping(value = "sale/royalty/queryAreaMatchInFo")
    @ApiOperation(value = "查询楼栋配置详情", notes = "查询楼栋配置详情")
    ServerResponse queryAreaMatchInFo(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("resourceId")String resourceId);



    @PostMapping(value = "sale/royalty/queryAreaMatch")
    @ApiOperation(value = "查询楼栋提成配置", notes = "查询楼栋提成配置")
    ServerResponse queryAreaMatch(@RequestParam("villageId") String villageId);


    @PostMapping(value = "sale/royalty/delAreaMatch")
    @ApiOperation(value = "删除楼栋提成配置", notes = "删除楼栋提成配置")
    ServerResponse delAreaMatch(@RequestParam("id") String id);
}
