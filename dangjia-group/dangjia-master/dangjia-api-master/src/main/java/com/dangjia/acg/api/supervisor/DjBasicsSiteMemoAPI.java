package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Api(description = "备忘录管理接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsSiteMemoAPI {
    /**
     * showdoc
     *
     * @param userToken       必选 string userToken
     * @param houseId         必选 string 房子ID
     * @param type            必选 string 0=备忘录,1=周计划
     * @param remark          必选 string 内容
     * @param remindMemberIds 可选 string 被提醒人ID，","分割
     * @param reminderTime    可选 string 指定时间提醒我
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/备忘录
     * @title 添加备忘录or周报
     * @description 添加备忘录or周报
     * @method POST
     * @url master/app/memo/addSiteMemo
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:27 PM
     */
    @PostMapping("app/memo/addSiteMemo")
    @ApiOperation(value = "添加备忘录/周报", notes = "添加备忘录/周报")
    ServerResponse addSiteMemo(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("type") Integer type,
                               @RequestParam("remark") String remark,
                               @RequestParam("remindMemberIds") String remindMemberIds,
                               @RequestParam("reminderTime") String reminderTime);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param memoId    必选 string 备忘录ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/备忘录
     * @title 删除备忘录
     * @description 删除备忘录
     * @method POST
     * @url master/app/memo/deleteSiteMemo
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:37 PM
     */
    @PostMapping("app/memo/deleteSiteMemo")
    @ApiOperation(value = "删除备忘录", notes = "删除备忘录")
    ServerResponse deleteSiteMemo(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("memoId") String memoId);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/备忘录
     * @title 获取备忘录消息数量
     * @description 获取备忘录消息数量
     * @method POST
     * @url master/app/memo/getMemoMessage
     * @return_param size int 备忘录未读数
     * @return_param memberImage string 第一条提示你的用户土坯昂
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:39 PM
     */
    @PostMapping("app/memo/getMemoMessage")
    @ApiOperation(value = "获取备忘录消息数量", notes = "获取备忘录消息数量")
    ServerResponse getMemoMessage(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/备忘录
     * @title 获取备忘录消息列表
     * @description 获取备忘录消息列表
     * @method POST
     * @url master/app/memo/getMemoMessageList
     * @return_param id string 备忘录ID
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param remark string 内容
     * @return_param houseId string 房子id
     * @return_param memberId string 创建人ID
     * @return_param workerType int 工种类别0业主,1设计师,2精算师,3大管家,4拆除,6水电工,7防水,8泥工,9木工,10油漆工
     * @return_param workerTypeName string 工种名称
     * @return_param type string 0=普通,1=周计划
     * @return_param remind int 0:自己的，1：被提醒的
     * @return_param memberName string 创建人名称
     * @return_param memberImage string 创建人头像
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:42 PM
     */
    @PostMapping("app/memo/getMemoMessageList")
    @ApiOperation(value = "获取备忘录消息列表", notes = "获取备忘录消息列表")
    ServerResponse getMemoMessageList(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/备忘录
     * @title 获取备忘录列表
     * @description 获取备忘录列表
     * @method POST
     * @url master/app/memo/getMemoList
     * @return_param id string 备忘录ID
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param remark string 内容
     * @return_param houseId string 房子id
     * @return_param memberId string 创建人ID
     * @return_param workerType int 工种类别0业主,1设计师,2精算师,3大管家,4拆除,6水电工,7防水,8泥工,9木工,10油漆工
     * @return_param workerTypeName string 工种名称
     * @return_param type string 0=普通,1=周计划
     * @return_param remind int 0:自己的，1：被提醒的
     * @return_param memberName string 创建人名称
     * @return_param memberImage string 创建人头像
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:55 PM
     */
    @PostMapping("app/memo/getMemoList")
    @ApiOperation(value = "获取备忘录列表", notes = "获取备忘录列表")
    ServerResponse getMemoList(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("pageDTO") PageDTO pageDTO,
                               @RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param memoId    必选 string 备忘录ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/备忘录
     * @title 获取备忘录详情
     * @description 获取备忘录详情
     * @method POST
     * @url master/app/memo/getSiteMemo
     * @return_param id string 备忘录ID
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param remark string 内容
     * @return_param houseId string 房子id
     * @return_param memberId string 创建人ID
     * @return_param workerType int 工种类别0业主,1设计师,2精算师,3大管家,4拆除,6水电工,7防水,8泥工,9木工,10油漆工
     * @return_param workerTypeName string 工种名称
     * @return_param type string 0=普通,1=周计划
     * @return_param memberName string 创建人名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:56 PM
     */
    @PostMapping("app/memo/getSiteMemo")
    @ApiOperation(value = "获取备忘录详情", notes = "获取备忘录详情")
    ServerResponse getSiteMemo(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken,
                               @RequestParam("memoId") String memoId);

    @PostMapping("app/memo/remindSiteMemo")
    @ApiOperation(value = "备忘录提醒", notes = "备忘录提醒")
    ServerResponse remindSiteMemo();

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string 房子ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/备忘录
     * @title 获取当前房子参与人员（除开自己）
     * @description 获取当前房子参与人员（除开自己）
     * @method POST
     * @url master/app/memo/getHouseMemberList
     * @return_param memberId string 用户ID
     * @return_param memberName string 用户昵称
     * @return_param memberImage string 用户头像
     * @return_param workerTypeName string 用户工种名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2020/1/11 5:59 PM
     */
    @PostMapping("app/memo/getHouseMemberList")
    @ApiOperation(value = "获取当前房子参与人员（除开自己）", notes = "获取当前房子参与人员（除开自己）")
    ServerResponse getHouseMemberList(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("userToken") String userToken,
                                      @RequestParam("houseId") String houseId);

}
