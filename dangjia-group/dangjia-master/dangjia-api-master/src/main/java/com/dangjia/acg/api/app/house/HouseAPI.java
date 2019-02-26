package com.dangjia.acg.api.app.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.House;
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
public interface HouseAPI {

    /**
     * 切换房产
     */
    @PostMapping("app/house/house/setSelectHouse")
    @ApiOperation(value = "切换房产", notes = "切换房产")
    ServerResponse setSelectHouse(@RequestParam("userToken")String userToken, @RequestParam("cityId")String cityId,
                                  @RequestParam("houseId")String houseId);

    /**
     * 房产列表
     */
    @PostMapping("app/house/house/getHouseList")
    @ApiOperation(value = "房产列表", notes = "房产列表")
    ServerResponse getHouseList(@RequestParam("userToken")String userToken, @RequestParam("cityId")String cityId);

    /**
     * 我的房产
     */
    @PostMapping("app/house/house/getMyHouse")
    @ApiOperation(value = "我的房产", notes = "我的房产")
    ServerResponse getMyHouse(@RequestParam("userToken")String userToken, @RequestParam("cityId")String cityId);

    /**
     * 我的房子
     */
    @PostMapping("app/house/house/list")
    @ApiOperation(value = "我的房子", notes = "我的房子")
    ServerResponse queryMyHouse(@RequestParam("userToken")String userToken);

    /**
     * @param houseType 装修的房子类型0：新房；1：老房
     * @param drawings 有无图纸0：无图纸；1：有图纸
     */
    @PostMapping("app/house/house/setStartHouse")
    @ApiOperation(value = "app开始装修", notes = "app开始装修")
    ServerResponse setStartHouse(@RequestParam("userToken")String userToken, @RequestParam("cityId")String cityId,
                                 @RequestParam("houseType")int houseType, @RequestParam("drawings")int drawings);

    @PostMapping("app/house/house/revokeHouse")
    @ApiOperation(value = "撤销房子装修", notes = "撤销房子装修")
    ServerResponse revokeHouse(@RequestParam("userToken")String userToken);

    /**
     * 修改房子精算状态
     */
    @PostMapping("app/house/house/setHouseBudgetOk")
    @ApiOperation(value = "修改房子精算状态", notes = "修改房子精算状态")
    ServerResponse setHouseBudgetOk(@RequestParam("houseId")String houseId, @RequestParam("budgetOk")Integer budgetOk);

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    @PostMapping("app/house/house/queryHouseByCity")
    @ApiOperation(value = "根据城市，小区，最小最大面积查询房子", notes = "根据城市，小区，最小最大面积查询房子")
    ServerResponse queryHouseByCity(@RequestParam("userToken")String userToken,@RequestParam("cityId")String cityId,@RequestParam("villageId") String villageId,@RequestParam("minSquare") Double minSquare,
                                    @RequestParam("maxSquare") Double maxSquare,@RequestParam("houseType")  Integer houseType , @RequestParam("pageDTO") PageDTO pageDTO);
    /**
     * 装修指南
     */
    @PostMapping("app/house/house/getRenovationManual")
    @ApiOperation(value = "装修指南", notes = "装修指南")
    public ServerResponse getRenovationManual(@RequestParam("userToken")String userToken,@RequestParam("type")Integer type);

    @PostMapping("app/house/house/saveRenovationManual")
    @ApiOperation(value = "保存装修指南", notes = "保存装修指南")
    public ServerResponse saveRenovationManual(@RequestParam("userToken")String userToken,@RequestParam("saveList")String saveList);

    @PostMapping("app/house/house/manualInfo")
    @ApiOperation(value = "获取装修指南明细", notes = "获取装修指南明细")
    ServerResponse getRenovationManualinfo(String id);


    @PostMapping("app/house/house/queryConstructionRecord")
    @ApiOperation(value = "施工记录", notes = "施工记录")
    ServerResponse queryConstructionRecord(@RequestParam("houseId")String houseId,@RequestParam("pageDTO")  PageDTO pageDTO);

    /**
     * 施工记录（首页滚动）
     */
    @PostMapping("app/house/house/queryHomeConstruction")
    @ApiOperation(value = "施工记录(首页文字滚动)", notes = "首页文字滚动")
    ServerResponse queryHomeConstruction();

    @PostMapping("app/house/house/queryFlowRecord")
    @ApiOperation(value = "工序详情", notes = "工序详情")
    ServerResponse queryFlowRecord(@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("app/house/house/setBudgetOk")
    @ApiOperation(value = "APP修改精算状态", notes = "APP修改精算状态")
    ServerResponse setHouseBudgetOk(@RequestParam("userToken")String userToken,
                                    @RequestParam("houseId")String houseId, @RequestParam("budgetOk")Integer budgetOk);

    @PostMapping("app/house/house/getHouseById")
    @ApiOperation(value = "根据id查询房子信息", notes = "根据id查询房子信息")
    House getHouseById(@RequestParam("houseId")String houseId);

    @PostMapping("app/house/house/getReferenceBudget")
    @ApiOperation(value = "参考报价", notes = "参考报价")
    ServerResponse getReferenceBudget( @RequestParam("villageId")String villageId,@RequestParam("square")Double square,
                                           @RequestParam("houseType")Integer houseType);
}
