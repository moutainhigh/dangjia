package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 工价商品Controller
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/12 上午11:09
 */
@Api(description = "人工商品管理接口")
@FeignClient("dangjia-service-goods")
public interface WorkerGoodsAPI {

    @PostMapping("/basics/workerGoods/getWorkerGoodses")
    @ApiOperation(value = "查询所有商品单位", notes = "查询所有商品单位")
    ServerResponse<PageInfo> getWorkerGoodses(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("pageDTO") PageDTO pageDTO,
                                              @RequestParam("workerTypeId") String workerTypeId,
                                              @RequestParam("searchKey") String searchKey,
                                              @RequestParam("showGoods") String showGoods);

//    @PostMapping("/basics/workerGoods/setWorkerGoods")
//    @ApiOperation(value = "新增或更新工价商品", notes = "新增或更新工价商品")
//    ServerResponse setWorkerGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("workerGoods")WorkerGoods workerGoods, @RequestParam("technologyIds")String technologyIds);

    @PostMapping("/basics/workerGoods/setWorkerGoods")
    @ApiOperation(value = "新增或更新工价商品", notes = "新增或更新工价商品")
    ServerResponse setWorkerGoods(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("workerGoods") WorkerGoods workerGoods,
                                  @RequestParam("technologyJsonList") String technologyJsonList,
                                  @RequestParam("deleteTechnologyIds") String deleteTechnologyIds);

    @PostMapping("/basics/workerGoods/getWorkertoCheck")
    @ApiOperation(value = "每工种未删除或已支付工钱", notes = "每工种未删除或已支付工钱")
    ServerResponse getWorkertoCheck(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("/basics/workerGoods/getPayedWorker")
    @ApiOperation(value = " 从精算表查工种已支付工钱", notes = " 从精算表查工种已支付工钱")
    ServerResponse getPayedWorker(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("/basics/workerGoods/deleteWorkerGoods")
    @ApiOperation(value = "删除人工商品", notes = " 删除人工商品")
    ServerResponse deleteWorkerGoods(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id);

    /**
     * showdoc
     *
     * @param cityId 必选 string 城市ID
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 首页当家商品模块
     * @description 首页当家商品模块
     * @method POST
     * @url goods/home/getHomeProductList
     * @return_param id string id
     * @return_param image string 图片
     * @return_param price Double 销售价
     * @return_param unitName string 单位
     * @return_param type Integer 0:货品，1：人工商品
     * @return_param goodsType Integer 0:材料；1：服务
     * @return_param name string 名称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 15
     * @Author: Ruking 18075121944
     * @Date: 2019/6/18 5:20 PM
     */
    @PostMapping("home/getHomeProductList")
    @ApiOperation(value = "首页当家商品模块", notes = " 首页当家商品模块")
    ServerResponse getHomeProductList(@RequestParam("request") HttpServletRequest request);

}
