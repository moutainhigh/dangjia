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
    ServerResponse setSelectHouse(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseId") String houseId);


    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/房产任务模块
     * @title 获取我的房产列表
     * @description 获取我的房产列表
     * @method POST
     * @url master/app/house/getMyHouseList
     * @return_param houseId string houseId
     * @return_param houseName string houseName
     * @return_param task int 任务数
     * @return_param btName string 按钮提示名
     * @return_param onclick string 按钮点击跳转的URL（为null则不需要跳转）
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/3 3:42 PM
     */
    @PostMapping("app/house/getMyHouseList")
    @ApiOperation(value = "获取我的房产列表", notes = "获取我的房产列表")
    ServerResponse getMyHouseList(@RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("userToken") String userToken);


    /**
     * 我的房子
     */
    @PostMapping("app/house/house/list")
    @ApiOperation(value = "我的房子", notes = "我的房子")
    ServerResponse queryMyHouse(@RequestParam("userToken") String userToken);

    /**
     * @param houseType 装修的房子类型0：新房；1：老房
     * @param drawings  有无图纸0：无图纸；1：有图纸
     */
    @PostMapping("app/house/house/setStartHouse")
    @ApiOperation(value = "app开始装修", notes = "app开始装修")
    ServerResponse setStartHouse(@RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("houseType") Integer houseType,
                                 @RequestParam("drawings") Integer drawings);

    @PostMapping("app/house/house/revokeHouse")
    @ApiOperation(value = "撤销房子装修", notes = "撤销房子装修")
    ServerResponse revokeHouse(@RequestParam("userToken") String userToken);

    /**
     * 修改房子精算状态
     */
    @PostMapping("app/house/house/setHouseBudgetOk")
    @ApiOperation(value = "修改房子精算状态", notes = "修改房子精算状态")
    ServerResponse setHouseBudgetOk(@RequestParam("houseId") String houseId,
                                    @RequestParam("budgetOk") Integer budgetOk);

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


    @PostMapping("app/house/house/saveRenovationManual")
    @ApiOperation(value = "保存装修指南", notes = "保存装修指南")
    ServerResponse saveRenovationManual(@RequestParam("userToken") String userToken,
                                        @RequestParam("saveList") String saveList);

    @PostMapping("app/house/house/manualInfo")
    @ApiOperation(value = "获取装修指南明细", notes = "获取装修指南明细")
    ServerResponse getRenovationManualinfo(String id);


    @PostMapping("app/house/house/queryConstructionRecord")
    @ApiOperation(value = "施工记录", notes = "施工记录")
    ServerResponse queryConstructionRecord(@RequestParam("houseId") String houseId,
                                           @RequestParam("day") String day,
                                           @RequestParam("workerType") String workerType,
                                           @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @param houseId    必选 string 房子ID
     * @param day        可选 string 时间
     * @param workerType 可选 string 工种类型
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/房产任务模块
     * @title 施工记录
     * @description 施工记录(new 包含要补退记录)
     * @method POST
     * @url 新版：master/app/house/house/queryConstructionRecordAll，旧版：app/house/house/queryConstructionRecord
     * @return_param id string id
     * @return_param workerHead string 工人头像
     * @return_param workerTypeName string 工种名称
     * @return_param workerName string 工人名称
     * @return_param content string 内容
     * @return_param sourceType int 进度状态
     * @return_param imgArr List[string] 图片
     * @return_param startDate string 开始时间
     * @return_param endDate string 结束时间
     * @return_param type int 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
     * @return_param number string 订单号
     * @return_param applyType string 进度状态描述
     * @return_param createDate string 创建时间
     * @return_param recordList list 节点数据
     * @return_param recordList_time string 修改时间
     * @return_param recordList_name string 节点名称
     * @return_param recordList_imgArr List[string] 节点图片
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/6/26 3:34 PM
     */
    @PostMapping("app/house/house/queryConstructionRecordAll")
    @ApiOperation(value = "施工记录(new 包含要补退记录)", notes = "施工记录(new 包含要补退记录)")
    ServerResponse queryConstructionRecordAll(@RequestParam("houseId") String houseId,
                                              @RequestParam("ids") String ids,
                                              @RequestParam("day") String day,
                                              @RequestParam("workerType") String workerType,
                                              @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("app/house/queryConstructionRecordType")
    @ApiOperation(value = "施工记录(new 包含要补退记录)分类型", notes = "施工记录(new 包含要补退记录)")
    ServerResponse queryConstructionRecordType(@RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param houseFlowId 必选/可选 string 工序ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/房产任务模块
     * @title 获取房子工序的阶段进度
     * @description 获取房子工序的阶段进度
     * @method POST
     * @url master/app/house/house/getStageProgress
     * @return_param totalDuration int 总工期/天
     * @return_param downtime int 停工天数/天
     * @return_param advanceTime int 提前完工时间/天
     * @return_param stageData List 工序阶段集合
     * @return_param stageData_name string 阶段名
     * @return_param stageData_state int 阶段状态0：未选中，1：当前阶段，2，已过阶段
     * @return_param stageData_msg string 描述
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/6/27 11:02 AM
     */
    @PostMapping("app/house/house/getStageProgress")
    @ApiOperation(value = "获取房子工序的阶段进度", notes = "获取房子工序的阶段进度")
    ServerResponse getStageProgress(@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/house/house/getHouseFlowApply")
    @ApiOperation(value = "获取施工记录详情", notes = "获取施工记录详情")
    ServerResponse getHouseFlowApply(@RequestParam("houseFlowApplyId") String houseFlowApplyId);

    /**
     * 施工记录（首页滚动）
     */
    @PostMapping("app/house/house/queryHomeConstruction")
    @ApiOperation(value = "施工记录(首页文字滚动)", notes = "首页文字滚动")
    ServerResponse queryHomeConstruction();

    @PostMapping("app/house/house/queryFlowRecord")
    @ApiOperation(value = "工序详情", notes = "工序详情")
    ServerResponse queryFlowRecord(@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/house/house/setBudgetOk")
    @ApiOperation(value = "APP修改精算状态", notes = "APP修改精算状态")
    ServerResponse setHouseBudgetOk(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("budgetOk") Integer budgetOk);

    @PostMapping("app/house/house/getHouseById")
    @ApiOperation(value = "根据id查询房子信息", notes = "根据id查询房子信息")
    House getHouseById(@RequestParam("houseId") String houseId);

    @PostMapping("app/house/house/getReferenceBudget")
    @ApiOperation(value = "参考报价", notes = "参考报价")
    ServerResponse getReferenceBudget(@RequestParam("cityId") String cityId,
                                      @RequestParam("villageId") String villageId,
                                      @RequestParam("square") Double square,
                                      @RequestParam("houseType") Integer houseType);


    @PostMapping("app/house/house/updateByHouseId")
    @ApiOperation(value = "业主装修的房子可修改", notes = "业主装修的房子可修改")
    ServerResponse updateByHouseId(@RequestParam("building") String building,                       //楼栋
                                   @RequestParam("houseId") String houseId,                         //小区ID
                                   @RequestParam("unit") String unit,                               //单元号
                                   @RequestParam("number") String number,                           //房间号
                                   @RequestParam("cityId") String cityId,                           //城市Id
                                   @RequestParam("villageId") String villageId,                     //小区Id
                                   @RequestParam("buildSquare") Double buildSquare);

    @PostMapping("app/house/house/updateCustomEdit")
    @ApiOperation(value = "房子申请修改未进场的工序还原", notes = "房子申请修改未进场的工序还原")
    ServerResponse updateCustomEdit(@RequestParam("houseId") String houseId);

    /**
     * 获取房屋精选案例详情
     *
     * @param id
     * @return
     */
    @PostMapping("app/house/house/getHouseChoiceCases")
    @ApiOperation(value = "获取房屋精选案例详情", notes = "获取房屋精选案例详情")
    ServerResponse getHouseChoiceCases(@RequestParam("id") String id);


}
