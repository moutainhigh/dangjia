package com.dangjia.acg.api.actuary;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： BudgetWorkerController.java
 * @功能描述：
 * @作者信息： hb
 * @创建时间： 2018-9-18下午3:52:43
 */
@Api(description = "材料精算")
@FeignClient("dangjia-service-goods")
public interface BudgetWorkerAPI {


    /**
     * 查询所有精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getAllBudgetWorker")
    @ApiOperation(value = "查询所有精算", notes = "查询所有精算")
    ServerResponse getAllBudgetWorker(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/actuary/budgetWorker/queryBudgetWorkerByHouseFlowId")
    @ApiOperation(value = "根据HouseFlowId查询房子人工精算", notes = "根据HouseFlowId查询房子人工精算")
    ServerResponse queryBudgetWorkerByHouseFlowId(@RequestParam("request") HttpServletRequest request,
                                                  @RequestParam("houseFlowId") String houseFlowId);

    /**
     * 根据Id查询精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getBudgetWorkerById")
    @ApiOperation(value = "根据Id查询精算", notes = "根据Id查询精算")
    ServerResponse getBudgetWorkerById(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("id") String id);

    /**
     * 根据houseId和wokerTypeId查询房子人工精算
     *
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getAllBudgetWorkerById")
    @ApiOperation(value = "根据houseId和wokerTypeId查询房子人工精算", notes = "根据houseId和workerTypeId查询房子人工精算")
    ServerResponse getAllBudgetWorkerById(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("workerTypeId") String workerTypeId);

    /**
     * 获取所有人工商品
     *
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getAllWorkerGoods")
    @ApiOperation(value = "获取所有人工商品", notes = "获取所有人工商品")
    ServerResponse getAllWorkerGoods(@RequestParam("request") HttpServletRequest request);

    /**
     * 制作精算模板
     *
     * @param listOfGoods
     * @param workerTypeId
     * @param templateId
     * @return
     */
    @PostMapping("/actuary/budgetWorker/budgetTemplates")
    @ApiOperation(value = "制作精算模板", notes = "制作精算模板")
    ServerResponse budgetTemplates(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("listOfGoods") String listOfGoods,
                                   @RequestParam("workerTypeId") String workerTypeId,
                                   @RequestParam("templateId") String templateId);

    /**
     * 修改精算模板
     *
     * @param listOfGoods
     * @param workerTypeId
     * @param templateId
     * @return
     */
    @PostMapping("/actuary/budgetWorker/updateBudgetTemplates")
    @ApiOperation(value = "修改精算模板", notes = "修改精算模板")
    ServerResponse updateBudgetTemplates(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("listOfGoods") String listOfGoods,
                                         @RequestParam("workerTypeId") String workerTypeId,
                                         @RequestParam("templateId") String templateId);

    /**
     * 查询该风格下所有精算模板
     *
     * @param templateId
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getAllbudgetTemplates")
    @ApiOperation(value = "查询该风格下所有精算模板", notes = "查询该风格下所有精算模板")
    ServerResponse getAllbudgetTemplates(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("templateId") String templateId);

    /**
     * 使用精算
     *
     * @param id
     * @return
     */
    @PostMapping("/actuary/budgetWorker/useTheBudget")
    @ApiOperation(value = "使用精算", notes = "使用精算")
    ServerResponse useTheBudget(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("id") String id);

    /**
     * 生成精算
     *
     * @param houseId
     * @param workerTypeId
     * @param listOfGoods
     * @return
     */
    @PostMapping("/actuary/budgetWorker/makeBudgets")
    @ApiOperation(value = "生成精算", notes = "生成精算")
    ServerResponse makeBudgets(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("actuarialTemplateId") String actuarialTemplateId,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("workerTypeId") String workerTypeId,
                               @RequestParam("listOfGoods") String listOfGoods);

    @PostMapping("/actuary/budgetWorker/importExcelBudgets")
    @ApiOperation(value = "Excel导入精算", notes = "Excel导入精算")
    ServerResponse importExcelBudgets(
            @RequestParam("request") StandardMultipartHttpServletRequest request,
            @RequestParam("file")  MultipartFile[] multipartFiles,
            @RequestParam("workerTypeId") String workerTypeId);
    /**
     * 根据houseId和wokerTypeId查询房子人工精算总价
     *
     * @param houseId
     * @param workerTypeId
     * @return
     */
    @PostMapping("/actuary/budgetWorker/getWorkerTotalPrice")
    @ApiOperation(value = "根据houseId和workerTypeId查询房子人工精算总价", notes = "根据houseId和workerTypeId查询房子人工精算总价")
    ServerResponse getWorkerTotalPrice(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("houseId") String houseId,
                                       @RequestParam("workerTypeId") String workerTypeId);

    /**
     * 业主修改精算
     *
     * @param listOfGoods
     * @return
     */
    @PostMapping("/actuary/budgetWorker/doModifyBudgets")
    @ApiOperation(value = "业主修改精算", notes = "业主修改精算")
    ServerResponse doModifyBudgets(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("listOfGoods") String listOfGoods);

    /**
     * 估价
     *
     * @param houseId
     * @return
     */
    @PostMapping("/actuary/budgetWorker/gatEstimateBudgetByHId")
    @ApiOperation(value = "估价", notes = "估价")
    ServerResponse gatEstimateBudgetByHId(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("houseId") String houseId);


    /**
     * 根据houseId查询所有
     * 已进场未完工工艺节点
     * 和所有材料工艺节点
     */
    @PostMapping("/actuary/budgetWorker/getAllTechnologyByHouseId")
    @ApiOperation(value = "根据houseId查询所有验收节点", notes = "根据houseId查询所有验收节点")
    JSONArray getAllTechnologyByHouseId(@RequestParam("houseId") String houseId);

    @PostMapping("/actuary/budgetWorker/getTecByHouseFlowId")
    @ApiOperation(value = "交底节点", notes = "交底节点")
    JSONArray getTecByHouseFlowId(@RequestParam("houseId") String houseId,
                                  @RequestParam("houseFlowId") String houseFlowId);


    @PostMapping("/actuary/budgetWorker/getTecList")
    @ApiOperation(value = "根据人工商品查询工艺", notes = "根据人工商品查询工艺")
   JSONArray getTecList(@RequestParam("workerGoodsId")String workerGoodsId);

    @PostMapping("/actuary/budgetWorker/getWorkerGoodsList")
    @ApiOperation(value = "精算查询包含工艺的人工商品", notes = "精算查询包含工艺的人工商品")
    JSONArray getWorkerGoodsList(@RequestParam("houseId")String houseId, @RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("/actuary/budgetWorker/workerPatrolList")
    @ApiOperation(value = "是否有巡查工艺节点", notes = "是否有巡查工艺节点")
    boolean workerPatrolList(@RequestParam("workerGoodsId")String workerGoodsId);
}
