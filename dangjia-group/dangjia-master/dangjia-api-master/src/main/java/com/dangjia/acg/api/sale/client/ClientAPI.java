package com.dangjia.acg.api.sale.client;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 9:45
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售端客户页接口", description = "销售端客户页接口")
public interface ClientAPI {


    @PostMapping(value = "sale/client/clientPage")
    @ApiOperation(value = "客户页", notes = "客户页")
    ServerResponse clientPage(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userToken") String userToken);

    /**
     * showdoc
     * @param clue      必选/可选 string TODO
     * @param clue      必选/可选 string TODO
     * @param clue      必选/可选 string TODO
     * @param clue      必选/可选 string TODO
     * @param userToken 必选 string userToken
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/客户模块
     * @title 录入客户
     * @description 录入客户
     * @method POST
     * @url  master/sale/client/enterCustomer
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: Ruking 18075121944
     * @Date: 2019/7/24 9:43
     */
    @PostMapping(value = "sale/client/enterCustomer")
    @ApiOperation(value = "录入客户", notes = "录入客户")
    ServerResponse enterCustomer(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("clue") Clue clue,
                                 @RequestParam("userToken") String userToken);


    /**
     * 跨域下单
     * @param request
     * @param clue
     * @param userToken
     * @return
     */
    @PostMapping(value = "sale/client/crossDomainOrder")
    @ApiOperation(value = "跨域下单", notes = "跨域下单")
    ServerResponse crossDomainOrder(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("clue") Clue clue,
                                    @RequestParam("userToken") String userToken,
                                    @RequestParam("cityId") String cityId,
                                    @RequestParam("villageId") String villageId);


    /**
     * 跟进列表
     *
     * @param request
     * @param label
     * @param time    排序 "desc":降序  不传升序
     * @param stage   0:新线索（线索阶段） 1：继续跟进（线索阶段）  4：转客服 （客户阶段）
     * @return
     */
    @PostMapping(value = "sale/client/followList")
    @ApiOperation(value = "跟进列表", notes = "跟进列表")
    ServerResponse followList(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userToken") String userToken,
                              @RequestParam("pageDTO") PageDTO pageDTO,
                              @RequestParam("label") String label,
                              @RequestParam("time") String time,
                              @RequestParam("stage") Integer stage,
                              @RequestParam("searchKey") String searchKey);

    /**
     * @param request
     * @param userToken
     * @param visitState 1:已下单  3：已竣工
     * @param pageDTO
     * @param time
     * @param searchKey
     * @return
     * @param type 1:待分配客户 2:沉睡客户
     */
    @PostMapping(value = "sale/client/ordersCustomer")
    @ApiOperation(value = "已下单/竣工/待分配/沉睡列表", notes = "已下单/竣工/待分配/沉睡列表")
    ServerResponse ordersCustomer(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("visitState") String visitState,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("time") String time,
                                  @RequestParam("searchKey") String searchKey,
                                  @RequestParam("type") Integer type,
                                  @RequestParam("userId") String userId);

    @PostMapping(value = "sale/client/updateCustomer")
    @ApiOperation(value = "编辑客户", notes = "编辑客户")
    ServerResponse updateCustomer(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("clue") Clue clue);
}
