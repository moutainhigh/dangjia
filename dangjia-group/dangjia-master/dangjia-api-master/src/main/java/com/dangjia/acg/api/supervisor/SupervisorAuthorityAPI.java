package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "督导权限配置接口")
@FeignClient("dangjia-service-master")
public interface SupervisorAuthorityAPI {
    /**
     * showdoc
     *
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @param cityId     必选 string 城市ID
     * @param memberId   必选 string 督导ID
     * @param visitState 必选 string 0待确认开工,1装修中,2休眠中,3已完工 (注意：-1 查全部)
     * @param searchKey  必选 string 搜索关键字
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导授权待选择列表
     * @description 督导授权待选择列表
     * @method POST
     * @url master/web/supervisor/getStayAuthorityList
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param memberName string 业主名称
     * @return_param memberId string 业主ID
     * @return_param memberPhone string 业主手机号
     * @return_param visitState Integer 0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
     * @return_param constructionDate Date 开工时间
     * @return_param selection Boolean 当前督导是否选中
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:28 PM
     */
    @PostMapping("web/supervisor/getStayAuthorityList")
    @ApiOperation(value = "督导授权待选择列表", notes = "督导授权待选择列表")
    ServerResponse getStayAuthorityList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("memberId") String memberId,
                                        @RequestParam("visitState") Integer visitState,
                                        @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @param memberId   必选 string 督导ID
     * @param visitState 必选 string 0待确认开工,1装修中,2休眠中,3已完工 (注意：-1 查全部)
     * @param searchKey  必选 string 搜索关键字
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/中台/督导
     * @title （web）获取督导授权列表
     * @description 获取督导授权列表
     * @method POST
     * @url master/web/supervisor/getAuthorityList
     * @return_param houseId string 房子ID
     * @return_param houseName string 房子名称
     * @return_param memberName string 业主名称
     * @return_param memberId string 业主ID
     * @return_param memberPhone string 业主手机号
     * @return_param visitState Integer 0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
     * @return_param constructionDate Date 开工时间
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:44 PM
     */
    @PostMapping("web/supervisor/getAuthorityList")
    @ApiOperation(value = "获取督导授权列表", notes = "获取督导授权列表")
    ServerResponse getAuthorityList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("memberId") String memberId,
                                    @RequestParam("visitState") Integer visitState,
                                    @RequestParam("searchKey") String searchKey);

    /**
     * showdoc
     *
     * @param memberId 必选 string 督导ID
     * @param houseId  必选 string 房子ID
     * @param userId   必选 string 中台登录的用户ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导添加授权
     * @description 督导添加授权
     * @method POST
     * @url master/web/supervisor/addAuthority
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:45 PM
     */
    @PostMapping("web/supervisor/addAuthority")
    @ApiOperation(value = "督导添加授权", notes = "督导添加授权")
    ServerResponse addAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("memberId") String memberId,
                                @RequestParam("houseId") String houseId,
                                @RequestParam("userId") String userId);

    /**
     * showdoc
     *
     * @param memberId 必选 string 督导ID
     * @param houseId  必选 string 房子ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/中台/督导
     * @title （web）督导移除授权
     * @description 督导移除授权
     * @method POST
     * @url master/web/supervisor/deleteAuthority
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2020/1/7 8:47 PM
     */
    @PostMapping("web/supervisor/deleteAuthority")
    @ApiOperation(value = "督导移除授权", notes = "督导移除授权")
    ServerResponse deleteAuthority(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("memberId") String memberId,
                                   @RequestParam("houseId") String houseId);

}
