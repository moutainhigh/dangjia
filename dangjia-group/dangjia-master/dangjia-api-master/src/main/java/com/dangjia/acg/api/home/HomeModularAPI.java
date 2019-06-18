package com.dangjia.acg.api.home;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 新版首页模块获取数据接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/14 3:55 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版首页模块获取数据接口", description = "新版首页模块获取数据接口")
public interface HomeModularAPI {
    /**
     * showdoc
     *
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 首页获取播报
     * @description 首页获取播报
     * @method POST
     * @url master/home/getBroadcastList
     * @return_param describe string 播报内容
     * @return_param houseId string houseId
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 13
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 4:27 PM
     */
    @PostMapping("home/getBroadcastList")
    @ApiOperation(value = "首页获取播报", notes = "首页获取播报")
    ServerResponse getBroadcastList(@RequestParam("request") HttpServletRequest request);

    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 可选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/首页模块
     * @title 获取攻略列表
     * @description 获取攻略列表
     * @method POST
     * @url master/home/getStrategyList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param name string 名称
     * @return_param workerTypeId string 阶段id
     * @return_param urlName string 链接名称
     * @return_param test string 指南内容
     * @return_param url string 链接地址
     * @return_param types string 装修类型
     * @return_param state Integer 状态0:可用；1:不可用
     * @return_param orderNumber Integer 排序序号
     * @return_param image string 封面图片
     * @return_param imageUrl string 封面图片地址
     * @return_param workerTypeName string 阶段名称
     * @return_param num Integer 观看数
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 14
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 8:09 PM
     */
    @PostMapping("home/getStrategyList")
    @ApiOperation(value = "获取攻略列表", notes = "获取攻略列表")
    ServerResponse getStrategyList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("pageDTO") PageDTO pageDTO);
}
