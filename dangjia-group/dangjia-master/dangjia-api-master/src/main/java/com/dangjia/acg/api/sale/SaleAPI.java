package com.dangjia.acg.api.sale;

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
 * @descrilbe 销售模块
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/7/19 2:07 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售用户模块", description = "销售用户模块")
public interface SaleAPI {
    /**
     * showdoc
     *
     * @param pageNum   必选 int 页码
     * @param pageSize  必选 int 记录数
     * @param userToken 必选 string userToken
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/销售模块/店长门店管理
     * @title 获取店长门店列表
     * @description 获取店长门店列表
     * @method POST
     * @url master/sale/getUserStoreList
     * @return_param id string 门店ID
     * @return_param storeName string 门店名称
     * @return_param storeAddress string 门店地址
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 7:44 PM
     */
    @PostMapping("sale/getUserStoreList")
    @ApiOperation(value = "获取店长门店列表", notes = "获取店长门店列表")
    ServerResponse getUserStoreList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("userToken") String userToken,
                                    @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/店长门店管理
     * @title 获取店长当前门店
     * @description 获取店长当前门店
     * @method POST
     * @url master/sale/getUserStore
     * @return_param id string 门店ID
     * @return_param storeName string 门店名称
     * @return_param storeAddress string 门店地址
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 7:47 PM
     */
    @PostMapping("sale/getUserStore")
    @ApiOperation(value = "获取店长当前门店", notes = "获取店长当前门店")
    ServerResponse getUserStore(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param storeId   必选 string 门店ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/店长门店管理
     * @title 设置店长当前门店
     * @description 设置店长当前门店
     * @method POST
     * @url master/sale/setUserStore
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 7:48 PM
     */
    @PostMapping("sale/setUserStore")
    @ApiOperation(value = "设置店长当前门店", notes = "设置店长当前门店")
    ServerResponse setUserStore(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userToken") String userToken,
                                @RequestParam("storeId") String storeId);
}
