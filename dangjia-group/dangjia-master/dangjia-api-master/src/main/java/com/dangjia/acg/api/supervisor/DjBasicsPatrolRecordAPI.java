package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 巡查记录管理接口
 * author:chenyufeng
 * time:2019.12.11
 */
@Api(description = "巡查记录管理接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsPatrolRecordAPI {
    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string 房子ID
     * @param content   必选 string 巡查内容
     * @param images    必选 string 巡查图片","分割
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title 督导添加巡查
     * @description 督导添加巡查
     * @method POST
     * @url master/app/supervisor/addPatrolRecord
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 31
     * @Author: Ruking 18075121944
     * @Date: 2020/1/14 8:10 PM
     */
    @PostMapping("app/supervisor/addPatrolRecord")
    @ApiOperation(value = "督导添加巡查", notes = "督导添加巡查")
    ServerResponse addPatrolRecord(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("content") String content,
                                   @RequestParam("images") String images);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param type      必选 Integer -1:全部;0:奖励;1:处罚,2:巡查
     * @param searchKey 可选 string 搜索值
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （web）中台查询督导工作记录
     * @description 中台查询督导工作记录
     * @method POST
     * @url master/web/supervisor/getPatrolRecordList
     * @return_param patrolRecordId int 记录ID
     * @return_param operatorId string 用户ID
     * @return_param operatorName string 用户名称
     * @return_param operatorMobile string 用户手机
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param type int 0:奖励;1:处罚,2:巡查
     * @return_param createDate string 创建日期
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 32
     * @Author: Ruking 18075121944
     * @Date: 2020/1/14 8:13 PM
     */
    @PostMapping("web/supervisor/getPatrolRecordList")
    @ApiOperation(value = "中台查询督导工作记录", notes = "中台查询督导工作记录")
    ServerResponse getPatrolRecordList(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("type") Integer type,
                                       @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @param type      必选 Integer 2：巡查，0：奖罚
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （App）查询督导工作记录
     * @description App查询督导工作记录
     * @method POST
     * @url master/app/supervisor/getAppPatrolRecordList
     * @return_param patrolRecordId int 记录ID
     * @return_param operatorId string 用户ID
     * @return_param operatorName string 用户名称
     * @return_param operatorMobile string 用户手机
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param type int 0:奖励;1:处罚,2:巡查
     * @return_param createDate string 创建日期
     * @return_param content string 巡查内容or奖罚说明
     * @return_param imageList List<String> 巡查图片or奖罚图片全地址
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 33
     * @Author: Ruking 18075121944
     * @Date: 2020/1/14 8:19 PM
     */
    @PostMapping("app/supervisor/getAppPatrolRecordList")
    @ApiOperation(value = "App查询督导工作记录", notes = "App查询督导工作记录")
    ServerResponse getAppPatrolRecordList(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("userToken") String userToken,
                                          @RequestParam("pageDTO") PageDTO pageDTO,
                                          @RequestParam("type") Integer type);

    /**
     * showdoc
     *
     * @param userToken      必选 string userToken
     * @param patrolRecordId 必选 string 记录ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title 获取督导记录详情
     * @description 获取督导记录详情
     * @method POST
     * @url master/app/supervisor/getPatrolRecordDetails
     * @return_param patrolRecordId int 记录ID
     * @return_param operatorId string 用户ID
     * @return_param operatorName string 用户名称
     * @return_param operatorMobile string 用户手机
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param type int 0:奖励;1:处罚,2:巡查
     * @return_param createDate string 创建日期
     * @return_param content string 巡查内容or奖罚说明
     * @return_param imageList List<String> 巡查图片or奖罚图片全地址
     * @return_param memberId string 被奖惩的用户ID
     * @return_param memberName string 被奖惩的用户名称
     * @return_param memberMobile string 被奖惩的用户手机
     * @return_param memberHead string 被奖惩的用户头像
     * @return_param workerTypeName string 被奖惩的工种名称
     * @return_param workerTypeType string 被奖惩的工种
     * @return_param rewardPunishCorrelation string 奖罚原因
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 34
     * @Author: Ruking 18075121944
     * @Date: 2020/1/14 8:21 PM
     */
    @PostMapping("app/supervisor/getPatrolRecordDetails")
    @ApiOperation(value = "获取督导记录详情", notes = "获取督导记录详情")
    ServerResponse getPatrolRecordDetails(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("userToken") String userToken,
                                          @RequestParam("patrolRecordId") String patrolRecordId);

}
