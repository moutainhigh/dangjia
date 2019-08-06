package com.dangjia.acg.api.sale.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 11:04
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售门店管理页接口", description = "销售门店管理页接口")
public interface StoreManagementAPI {

    @PostMapping(value = "sale/store/storeManagementPage")
    @ApiOperation(value = "门店管理页", notes = "门店管理页")
    ServerResponse storeManagementPage(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping(value = "sale/store/addBuilding")
    @ApiOperation(value = "小区添加楼栋", notes = "小区添加楼栋")
    ServerResponse addBuilding(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("villageId") String villageId,
                               @RequestParam("modifyDate") Date modifyDate,
                               @RequestParam("building") String building,
                               @RequestParam("storeId") String storeId);

    @PostMapping(value = "sale/store/delBuilding")
    @ApiOperation(value = "小区楼栋删除", notes = "小区楼栋删除")
    ServerResponse delBuilding(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("buildingId") String buildingId);

    @PostMapping(value = "sale/store/updatBuilding")
    @ApiOperation(value = "小区楼栋修改", notes = "小区楼栋修改")
    ServerResponse updatBuilding(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("buildingId") String buildingId,
                                 @RequestParam("residentialBuilding") ResidentialBuilding residentialBuilding);


    @PostMapping(value = "sale/store/BuildingList")
    @ApiOperation(value = "小区楼栋列表", notes = "小区楼栋列表")
    ServerResponse BuildingList(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("storeId") String storeId,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("userId")  String userId);

    @PostMapping(value = "sale/store/upDateCusService")
    @ApiOperation(value = "分配销售", notes = "分配销售")
    ServerResponse upDateCusService(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("clueId")String clueId,
                                    @RequestParam("cusService")String cusService,
                                    @RequestParam("mcId")String mcId,
                                    @RequestParam("houseId")String houseId,
                                    @RequestParam("phaseStatus")Integer phaseStatus);


    @PostMapping(value = "sale/store/upDateCustomer")
    @ApiOperation(value = "转出客户", notes = "转出客户")
    ServerResponse upDateCustomer(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("clueId")String clueId,
                                  @RequestParam("mcId")String mcId,
                                  @RequestParam("cityId")String cityId,
                                  @RequestParam("phaseStatus")Integer phaseStatus);
}
