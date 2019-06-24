package com.dangjia.acg.api.app.core;

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
 * Date: 2018/11/5 0005
 * Time: 18:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "工人和工序关联接口", description = "工人和工序关联接口")
public interface HouseWorkerAPI {

    @PostMapping("app/core/houseWorker/task")
    @ApiOperation(value = "根据工人id查询所有房子任务", notes = "根据工人id查询所有房子任务")
    ServerResponse queryWorkerHouse(@RequestParam("userToken") String userToken);

    @PostMapping("app/core/houseWorker/setWorkerGrab")
    @ApiOperation(value = "工匠抢单", notes = "工匠抢单")
    ServerResponse setWorkerGrab(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("houseFlowId") String houseFlowId);

    /**
     * showdoc
     *
     * @param userToken     必选 string userToken
     * @param houseWorkerId 必选 string houseWorkerId
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/房产任务模块
     * @title 业主换工匠
     * @description 业主换工匠
     * @method POST
     * @url master/app/core/houseWorker/setChangeWorker
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 12:01 PM
     */
    @PostMapping("app/core/houseWorker/setChangeWorker")
    @ApiOperation(value = "业主换工匠", notes = "业主换工匠")
    ServerResponse setChangeWorker(@RequestParam("userToken") String userToken,
                                   @RequestParam("houseWorkerId") String houseWorkerId);

    /**
     * showdoc
     *
     * @param userToken   必选 string userToken
     * @param houseFlowId 必选 string houseFlowId
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/房产任务模块
     * @title 获取工匠详情
     * @description 获取工匠详情
     * @method POST
     * @url master/app/core/houseWorker/getHouseWorker
     * @return_param houseWorker Object 当前工匠，返回参数查看member_下的
     * @return_param historyWorkerList List 历史工匠，返回参数查看member_下的
     * @return_param member_id String 工匠ID
     * @return_param member_targetId String 工匠极光账号
     * @return_param member_targetAppKey String 工匠极光key
     * @return_param member_nickName String 工匠昵称
     * @return_param member_name String 工匠姓名
     * @return_param member_mobile String 工匠手机
     * @return_param member_head String 工匠头像
     * @return_param member_workerTypeId String 工种ID
     * @return_param member_workerName String 工种名称
     * @return_param member_houseFlowId String 工序ID
     * @return_param member_houseWorkerId String 工序订单ID
     * @return_param member_isSubstitution int 是否可以更换，0：不可以，1：可以
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 12:03 PM
     */
    @PostMapping("app/core/houseWorker/getHouseWorker")
    @ApiOperation(value = "获取工匠详情", notes = "获取工匠详情")
    ServerResponse getHouseWorker(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/core/houseWorker/getConstructionByWorkerId")
    @ApiOperation(value = "根据工人查询自己的施工界面", notes = "根据工人查询自己的施工界面")
    ServerResponse getConstructionByWorkerId(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("userToken") String userToken,
                                             @RequestParam("cityId") String cityId);

    @PostMapping("app/core/houseWorker/getMyHomePage")
    @ApiOperation(value = "获取我的界面", notes = "获取我的界面")
    ServerResponse getMyHomePage(@RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId);

    @PostMapping("app/core/houseWorker/getHouseFlowApply")
    @ApiOperation(value = "获取申请单明细", notes = "获取申请单明细")
    ServerResponse getHouseFlowApply(@RequestParam("userToken") String userToken, @RequestParam("houseFlowApplyId") String houseFlowApplyId);

    @PostMapping("app/core/houseWorker/setHouseFlowApply")
    @ApiOperation(value = "提交审核、停工", notes = "提交审核、停工")
    ServerResponse setHouseFlowApply(@RequestParam("userToken") String userToken,
                                     @RequestParam("applyType") Integer applyType,
                                     @RequestParam("houseFlowId") String houseFlowId,
                                     @RequestParam("suspendDay") Integer suspendDay,
                                     @RequestParam("applyDec") String applyDec,
                                     @RequestParam("imageList") String imageList,
                                     @RequestParam("houseFlowId2") String houseFlowId2);

    @PostMapping("app/core/houseWorker/getAdvanceInAdvance")
    @ApiOperation(value = "提前进场", notes = "提前进场")
    ServerResponse getAdvanceInAdvance(@RequestParam("userToken") String userToken,
                                       @RequestParam("houseFlowId") String houseFlowId);

    /**
     * TODO 1.4.0后删除此接口
     *
     * @return
     */
    @PostMapping("app/core/houseWorker/getHouseFlowList")
    @ApiOperation(value = "查询工地列表", notes = "查询工地列表")
    ServerResponse getHouseFlowList(@RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选/可选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/房产任务模块
     * @title 查询我的工地列表
     * @description 查询我的工地列表
     * @method POST
     * @url master/app/core/houseWorker/getMyHouseFlowList
     * @return_param houseFlowId string 任务id
     * @return_param houseId string houseId
     * @return_param memberId string 用户ID
     * @return_param workerTypeId string 工种ID
     * @return_param price string 价格
     * @return_param houseName string 地址
     * @return_param releaseTime Date 发布时间
     * @return_param square string 面积
     * @return_param memberName string 业主姓名
     * @return_param isItNormal string 正常施工
     * @return_param houseIsStart string 有没有今日开工记录
     * @return_param taskNumber int 任务数量
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/3 5:59 PM
     */
    @PostMapping("app/core/houseWorker/getMyHouseFlowList")
    @ApiOperation(value = "查询我的工地列表", notes = "查询我的工地列表")
    ServerResponse getMyHouseFlowList(@RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("userToken") String userToken);

    @PostMapping("app/core/houseWorker/setSwitchHouseFlow")
    @ApiOperation(value = "切换工地", notes = "切换工地")
    ServerResponse setSwitchHouseFlow(@RequestParam("userToken") String userToken,
                                      @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/core/houseWorker/setSupervisorApply")
    @ApiOperation(value = "大管家申请验收", notes = "大管家申请验收")
    ServerResponse setSupervisorApply(@RequestParam("userToken") String userToken,
                                      @RequestParam("houseFlowId") String houseFlowId);

}
