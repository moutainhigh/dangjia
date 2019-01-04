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
    ServerResponse<PageInfo> getWorkerGoodses(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO, @RequestParam("workerTypeId")String workerTypeId, @RequestParam("searchKey")String searchKey, @RequestParam("showGoods")String showGoods) ;

//    @PostMapping("/basics/workerGoods/setWorkerGoods")
//    @ApiOperation(value = "新增或更新工价商品", notes = "新增或更新工价商品")
//    ServerResponse setWorkerGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("workerGoods")WorkerGoods workerGoods, @RequestParam("technologyIds")String technologyIds);

    @PostMapping("/basics/workerGoods/setWorkerGoods")
    @ApiOperation(value = "新增或更新工价商品", notes = "新增或更新工价商品")
    ServerResponse setWorkerGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("workerGoods")WorkerGoods workerGoods, @RequestParam("technologyJsonList")String technologyJsonList,  @RequestParam("deleteTechnologyIds")String deleteTechnologyIds);

    @PostMapping("/basics/workerGoods/getWorkertoCheck")
    @ApiOperation(value = "每工种未删除或已支付工钱", notes = "每工种未删除或已支付工钱")
    public ServerResponse getWorkertoCheck(@RequestParam("request") HttpServletRequest request,@RequestParam("houseId")String houseId,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("/basics/workerGoods/getPayedWorker")
    @ApiOperation(value = " 从精算表查工种已支付工钱", notes = " 从精算表查工种已支付工钱")
    public ServerResponse getPayedWorker(@RequestParam("request") HttpServletRequest request,@RequestParam("houseId")String houseId,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("/basics/workerGoods/deleteWorkerGoods")
    @ApiOperation(value = "删除人工商品", notes = " 删除人工商品")
    public ServerResponse deleteWorkerGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("id")String id);


}
