package com.dangjia.acg.controller.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.BudgetWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BudgetWorkerController implements BudgetWorkerAPI {
    @Autowired
    private BudgetWorkerService budgetWorkerService;

    //根据HouseFlowId查询房子材料精算
    @Override
    @ApiMethod
    public ServerResponse queryBudgetWorkerByHouseFlowId(HttpServletRequest request,String houseFlowId){
        return budgetWorkerService.queryBudgetWorkerByHouseFlowId(houseFlowId);
    }
    /**
     * 查询所有精算
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllBudgetWorker(HttpServletRequest request) {
        return budgetWorkerService.getAllBudgetWorker();
    }

    /**
     * 根据Id查询精算
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetWorkerById(HttpServletRequest request, String id) {
        return budgetWorkerService.getBudgetWorkerByMyId(id);
    }

    /**
     * 根据houseId和wokerTypeId查询房子人工精算
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllBudgetWorkerById(HttpServletRequest request, String houseId, String workerTypeId) {
        return budgetWorkerService.getAllBudgetWorkerById(houseId, workerTypeId);
    }

    /**
     * 获取所有人工商品
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllWorkerGoods(HttpServletRequest request) {
        return budgetWorkerService.getAllWorkerGoods();
    }

    /**
     * 制作精算模板
     *
     * @param listOfGoods
     * @param workerTypeId
     * @param templateId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse budgetTemplates(HttpServletRequest request, String listOfGoods, String workerTypeId, String templateId) {
        return budgetWorkerService.budgetTemplates(listOfGoods, workerTypeId, templateId);
    }

    /**
     * 修改精算模板
     *
     * @param listOfGoods
     * @param workerTypeId
     * @param templateId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateBudgetTemplates(HttpServletRequest request, String listOfGoods, String workerTypeId, String templateId) {
        return budgetWorkerService.updateBudgetTemplate(listOfGoods, workerTypeId, templateId);
    }

    /**
     * 查询该风格下所有精算模板
     *
     * @param templateId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllbudgetTemplates(HttpServletRequest request, String templateId) {
        return budgetWorkerService.getAllbudgetTemplates(templateId);
    }

    /**
     * 使用精算
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse useTheBudget(HttpServletRequest request, String id) {
        return budgetWorkerService.useuseTheBudget(id);
    }

    /**
     * 生成精算
     *
     * @param houseId
     * @param workerTypeId
     * @param listOfGoods
     * @return
     */
    @SuppressWarnings("static-access")
    @Override
    @ApiMethod
    public ServerResponse makeBudgets(HttpServletRequest request, String actuarialTemplateId, String houseId, String workerTypeId, String listOfGoods) {
        return budgetWorkerService.makeBudgets(actuarialTemplateId, houseId, workerTypeId, listOfGoods);
    }

    /**
     * 根据houseId和wokerTypeId查询房子人工精算总价
     *
     * @param houseId
     * @param workerTypeId
     * @return
     */
    @SuppressWarnings("static-access")
    @Override
    @ApiMethod
    public ServerResponse getWorkerTotalPrice(HttpServletRequest request, String houseId, String workerTypeId) {
        return budgetWorkerService.getWorkerTotalPrice(houseId, workerTypeId);
    }

    /**
     * 业主修改精算
     *
     * @param listOfGoods
     * @return
     */
    @SuppressWarnings("static-access")
    @Override
    @ApiMethod
    public ServerResponse doModifyBudgets(HttpServletRequest request, String listOfGoods) {
        return budgetWorkerService.doModifyBudgets(listOfGoods);
    }

    /**
     * 估价
     *
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse gatEstimateBudgetByHId(HttpServletRequest request, String houseId) {
        return budgetWorkerService.gatEstimateBudgetByHId(houseId);
    }

    /**
     * 根据houseId查询所有
     * 已进场未完工工艺节点
     * 和所有材料工艺节点
     */
    @Override
    @ApiMethod
    public JSONArray getAllTechnologyByHouseId(String houseId) {
        return budgetWorkerService.getAllTechnologyByHouseId(houseId);
    }
}
