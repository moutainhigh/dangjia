package com.dangjia.acg.api.sale.royalty;


import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 提成配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@FeignClient("dangjia-service-master")
@Api(value = "提成配置模块", description = "提成配置模块")
public interface RoyaltyAPI {


    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param pageNum      必选 int 页码
     * @param pageSize     必选 int 记录数
     * @return  {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/26 0026 16:54
     */
    @PostMapping(value = "sale/royalty/queryRoyaltySurface")
    @ApiOperation(value = "查询提成配置", notes = "查询提成配置")
    ServerResponse queryRoyaltySurface(@RequestParam("request")HttpServletRequest request,
                                       @RequestParam("pageDTO")PageDTO pageDTO) ;

    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param djRoyaltyDetailsSurface 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/26 0026 17:43
     */
    @PostMapping(value = "sale/royalty/addRoyaltyData")
    @ApiOperation(value = "新增提成配置", notes = "新增提成配置")
    ServerResponse addRoyaltyData(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("lists") String lists);


    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/26 0026 18:00
     */
    @PostMapping(value = "sale/royalty/queryRoyaltyData")
    @ApiOperation(value = "查询提成详细信息", notes = "查询提成详细信息")
    ServerResponse queryRoyaltyData(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("id") String id);

}
