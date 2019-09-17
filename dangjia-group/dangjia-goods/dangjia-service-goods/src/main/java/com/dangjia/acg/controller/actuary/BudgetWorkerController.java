package com.dangjia.acg.controller.actuary;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.service.actuary.BudgetWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BudgetWorkerController implements BudgetWorkerAPI {
    @Autowired
    private BudgetWorkerService budgetWorkerService;

    //根据HouseFlowId查询房子材料精算
    @Override
    @ApiMethod
    public ServerResponse queryBudgetWorkerByHouseFlowId(String cityId,String houseFlowId){
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

    @Override
    public BudgetWorker getHouseBudgetWorkerId(String cityId, String houseId, String workerGoodsId) {
        return budgetWorkerService.getHouseBudgetWorkerId(houseId,workerGoodsId);
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
   /* @Override
    @ApiMethod
    public ServerResponse getAllWorkerGoods(HttpServletRequest request) {
        return budgetWorkerService.getAllWorkerGoods();
    }*/

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
     * 生成精算（xls导入）
     */
    @Override
    @ApiMethod
    public ServerResponse importExcelBudgets(StandardMultipartHttpServletRequest request, MultipartFile[] multipartFiles, String workerTypeId){
        MultipartFile file=null;
        if(multipartFiles.length==0){
            List<MultipartFile> allimg=new ArrayList<>();
            List<MultipartFile> images=request.getFiles("image");
            List<MultipartFile> files=request.getFiles("file");
            List<MultipartFile> imgFile=request.getFiles("imgFile");
            allimg.addAll(images);
            allimg.addAll(files);
            allimg.addAll(imgFile);
            multipartFiles=new MultipartFile[allimg.size()];
            multipartFiles=allimg.toArray(multipartFiles);
            if(multipartFiles.length>0){
                file=multipartFiles[0];
            }
        }else{
            file=multipartFiles[0];
        }
        return budgetWorkerService.importExcelBudgets( workerTypeId, file);
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
    public ServerResponse getWorkerTotalPrice(String cityId, String houseId, String workerTypeId) {
        return budgetWorkerService.getWorkerTotalPrice(houseId, workerTypeId);
    }

    /**
     * 估价
     *
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse gatEstimateBudgetByHId(String  cityId, String houseId) {
        return budgetWorkerService.gatEstimateBudgetByHId(houseId);
    }

    /**
     * 根据houseId查询所有
     * 已进场未完工工艺节点
     * 和所有材料工艺节点
     */
    @Override
    @ApiMethod
    public JSONArray getAllTechnologyByHouseId(String cityId,
                                               String houseId) {
        return budgetWorkerService.getAllTechnologyByHouseId(houseId);
    }

    /**
     * 工种施工节点
     */
    public JSONArray getTecByHouseFlowId(String cityId,
                                         String houseId,String houseFlowId) {
        return budgetWorkerService.getTecByHouseFlowId(houseId, houseFlowId);
    }

    /**
     * 查询精算工序所有工艺
     */
    public JSONArray getTecList(String cityId,
                                int workerType,String workerGoodsId) {
        return budgetWorkerService.getTecList(workerType,workerGoodsId);
    }

    public JSONArray getWorkerGoodsList(String cityId,
                                        String houseId, String houseFlowId){
        return budgetWorkerService.getWorkerGoodsList(houseId, houseFlowId);
    }

    public boolean workerPatrolList(String cityId,
                                    String workerGoodsId){
        return budgetWorkerService.workerPatrolList(workerGoodsId);
    }

    public boolean patrolList(String cityId,
                              String workerGoodsId){
        return budgetWorkerService.patrolList(workerGoodsId);
    }
}
