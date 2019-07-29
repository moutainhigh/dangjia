package com.dangjia.acg.api.web.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("dangjia-service-master")
@Api(value = "门店人员管理接口", description = "门店人员管理接口")
public interface StoreUserAPI {

    /**
     * showdoc
     *
     * @param userId  必选 string 成员用户ID
     * @param storeId 必选 string 门店ID
     * @param type    必选 int 类别：0:场内销售，1:场外销售
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/门店成员
     * @title 新增门店成员
     * @description 新增门店成员
     * @method POST
     * @url master/storeUser/addStoreUser
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 3:06 PM
     */
    @PostMapping("storeUser/addStoreUser")
    @ApiOperation(value = "新增门店成员", notes = "新增门店成员")
    ServerResponse addStoreUser(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userId") String userId,
                                @RequestParam("storeId") String storeId,
                                @RequestParam("type") Integer type);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param storeId   必选 string 门店ID
     * @param searchKey 可选 string 用户名/用户手机
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/销售模块/门店成员
     * @title 查询门店成员
     * @description 查询门店成员
     * @method POST
     * @url master/storeUser/queryStoreUser
     * @return_param storeUserId string 门店成员ID
     * @return_param userId string 成员用户ID
     * @return_param storeId string 门店ID
     * @return_param type Integer 类别：0:场内销售，1:场外销售
     * @return_param userName string 用户名
     * @return_param userMobile string 手机
     * @return_param userHead string 头像
     * @return_param isJob Boolean 是否在职（0：正常；1，离职）
     * @return_param createDate Date 创建日期
     * @return_param modifyDate Date 修改日期
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 3:10 PM
     */
    @PostMapping("storeUser/queryStoreUser")
    @ApiOperation(value = "查询门店成员", notes = "查询门店成员")
    ServerResponse queryStoreUser(@RequestParam("storeId") String storeId,
                                  @RequestParam("searchKey") String searchKey,
                                  @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param storeUserId 必选 string 门店成员ID
     * @param type        必选 string 类别：0:场内销售，1:场外销售
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/门店成员
     * @title 编辑门店成员
     * @description 编辑门店成员
     * @method POST
     * @url master/storeUser/updateStoreUser
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 3:15 PM
     */
    @PostMapping("storeUser/updateStoreUser")
    @ApiOperation(value = "编辑门店成员", notes = "编辑门店成员")
    ServerResponse updateStoreUser(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("storeUserId") String storeUserId,
                                   @RequestParam("type") Integer type);

    /**
     * showdoc
     *
     * @param storeUserId 必选 string 门店成员ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/门店成员
     * @title 删除门店成员
     * @description 删除门店成员
     * @method POST
     * @url master/storeUser/delStoreUser
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 3:17 PM
     */
    @PostMapping("storeUser/delStoreUser")
    @ApiOperation(value = "删除门店成员", notes = "删除门店成员")
    ServerResponse delStoreUser(@RequestParam("storeUserId") String storeUserId);

    /**
     * showdoc
     *
     * @param storeUserId 必选 string 门店成员ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/门店成员
     * @title 获取门店成员详情
     * @description 获取门店成员详情
     * @method POST
     * @url master/storeUser/getStoreUser
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 3:17 PM
     */
    @PostMapping("storeUser/getStoreUser")
    @ApiOperation(value = "获取门店成员详情", notes = "获取门店成员详情")
    ServerResponse getStoreUser(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userToken") String userToken,
                                @RequestParam("userId") String userId);

}
