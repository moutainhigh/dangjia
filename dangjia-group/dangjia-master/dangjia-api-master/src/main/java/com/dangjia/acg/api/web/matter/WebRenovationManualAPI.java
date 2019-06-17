package com.dangjia.acg.api.web.matter;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("dangjia-service-master")
@Api(value = "后台装修指南接口", description = "后台装修指南接口")
public interface WebRenovationManualAPI {

    /**
     * showdoc
     *
     * @param pageNum      必选 int 页码
     * @param pageSize     必选 int 记录数
     * @param workerTypeId 可选 string 阶段id
     * @param name         可选 string 名称
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/装修攻略
     * @title 查询所有装修指南
     * @description 查询所有装修指南
     * @method POST
     * @url master/web/renovationManual/queryRenovationManual
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
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 7:05 PM
     */
    @PostMapping("web/renovationManual/queryRenovationManual")
    @ApiOperation(value = "查询所有装修指南", notes = "查询所有装修指南")
    ServerResponse queryRenovationManual(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                         @RequestParam("workerTypeId") String workerTypeId,
                                         @RequestParam("name") String name);

    /**
     * showdoc
     *
     * @param name         必选 string 名称
     * @param workerTypeId 必选 string 阶段id
     * @param urlName      必选 string 链接名称
     * @param test         必选 string 指南内容
     * @param url          可选 string 链接地址
     * @param types        可选 string 装修类型
     * @param state        可选 string 状态0:可用；1:不可用
     * @param orderNumber  可选 string 排序序号
     * @param image        必选 string 封面图片
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 新增装修指南
     * @description 新增装修指南
     * @method POST
     * @url master/web/renovationManual/addRenovationManual
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 7:23 PM
     */
    @PostMapping("web/renovationManual/addRenovationManual")
    @ApiOperation(value = "新增装修指南", notes = "新增装修指南")
    ServerResponse addRenovationManual(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("name") String name,
                                       @RequestParam("workerTypeId") String workerTypeId,
                                       @RequestParam("urlName") String urlName,
                                       @RequestParam("test") String test,
                                       @RequestParam("url") String url,
                                       @RequestParam("types") String types,
                                       @RequestParam("state") Integer state,
                                       @RequestParam("orderNumber") Integer orderNumber,
                                       @RequestParam("image") String image);

    /**
     * showdoc
     *
     * @param id           必选string id
     * @param name         必选 string 名称
     * @param workerTypeId 必选 string 阶段id
     * @param urlName      必选 string 链接名称
     * @param test         必选 string 指南内容
     * @param url          可选 string 链接地址
     * @param types        可选 string 装修类型
     * @param state        可选 string 状态0:可用；1:不可用
     * @param orderNumber  可选 string 排序序号
     * @param image        必选 string 封面图片
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 修改装修指南
     * @description 修改装修指南
     * @method POST
     * @url master/web/renovationManual/updateRenovationManual
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 7:26 PM
     */
    @PostMapping("web/renovationManual/updateRenovationManual")
    @ApiOperation(value = "修改装修指南", notes = "修改装修指南")
    ServerResponse updateRenovationManual(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("id") String id,
                                          @RequestParam("name") String name,
                                          @RequestParam("workerTypeId") String workerTypeId,
                                          @RequestParam("urlName") String urlName,
                                          @RequestParam("test") String test,
                                          @RequestParam("url") String url,
                                          @RequestParam("types") String types,
                                          @RequestParam("state") Integer state,
                                          @RequestParam("orderNumber") Integer orderNumber,
                                          @RequestParam("image") String image);

    /**
     * showdoc
     *
     * @param id 必选string id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 删除装修指南
     * @description 删除装修指南
     * @method POST
     * @url master/web/renovationManual/deleteRenovationManual
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 8
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 7:28 PM
     */
    @PostMapping("web/renovationManual/deleteRenovationManual")
    @ApiOperation(value = "删除装修指南", notes = "删除装修指南")
    ServerResponse deleteRenovationManual(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("id") String id);

    /**
     * showdoc
     *
     * @param id 必选string id
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 查询装修指南对象
     * @description 查询装修指南对象
     * @method POST
     * @url master/web/renovationManual/getRenovationManualById
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
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 9
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 7:29 PM
     */
    @PostMapping("web/renovationManual/getRenovationManualById")
    @ApiOperation(value = "查询装修指南对象", notes = "查询装修指南对象")
    ServerResponse getRenovationManualById(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("id") String id);
}
