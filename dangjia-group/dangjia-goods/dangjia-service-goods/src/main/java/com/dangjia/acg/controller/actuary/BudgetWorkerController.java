package com.dangjia.acg.controller.actuary;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.BudgetWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 
   * @类 名： BudgetWorkerController.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-18下午3:52:43
 */
@RestController
public class BudgetWorkerController implements BudgetWorkerAPI {
	/**
	 * 注入service
	 */
	 @Autowired
	 private BudgetWorkerService budgetWorkerService;
	 @Autowired
	 private GetForBudgetAPI getForBudgetAPI;
	 /**
	  * 查询所有精算
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse getAllBudgetWorker(){
		 return budgetWorkerService.getAllBudgetWorker();
	 }
	 /**
	  * 根据Id查询精算
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse getBudgetWorkerById(String id){
		 return budgetWorkerService.getBudgetWorkerByMyId(id);
	 }
	 /**
	  * 根据houseId和wokerTypeId查询房子人工精算
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse getAllBudgetWorkerById(String houseId,String workerTypeId){
		 return budgetWorkerService.getAllBudgetWorkerById(houseId,workerTypeId);
	 }
	 /**
	  * 获取所有人工商品
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse getAllWorkerGoods(){
		 return budgetWorkerService.getAllWorkerGoods();
	 }
	 /**
	  * 制作精算模板
	  * @param listOfGoods
	  * @param workerTypeId
	  * @param templateId
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse budgetTemplates(String listOfGoods,String workerTypeId,String templateId){
		 return budgetWorkerService.makeBudgetTemplate(listOfGoods,workerTypeId,templateId);
	 }
	 /**
	  * 修改精算模板
	  * @param listOfGoods
	  * @param workerTypeId
	  * @param templateId
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse updateBudgetTemplates(String listOfGoods,String workerTypeId,String templateId){
		 return budgetWorkerService.updateBudgetTemplate(listOfGoods, workerTypeId, templateId);
	 }
	 /**
	  * 查询该风格下所有精算模板
	  * @param templateId
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse getAllbudgetTemplates(String templateId){
		 return  budgetWorkerService.getAllbudgetTemplates(templateId);
	 }
	 /**
	  * 使用精算
	  * @param id
	  * @return
	  */
	 @Override
	 @ApiMethod
	 public ServerResponse useTheBudget(String id){
		 return budgetWorkerService.useuseTheBudget(id);
	 }
	 /**
	  * 生成精算
	  * @param houseId
	  * @param workerTypeId
	  * @param listOfGoods
	  * @return
	  */
	 @SuppressWarnings("static-access")
	 @Override
	 @ApiMethod
	 public ServerResponse makeBudgets(String houseId,String workerTypeId,String listOfGoods){
		 ServerResponse serverResponse = getForBudgetAPI.actuarialForBudget(houseId, workerTypeId);
		 if(serverResponse.isSuccess()){
			 JSONObject obj=JSONObject.parseObject(serverResponse.getResultObj().toString());
			 String houseFlowId=obj.getString("houseFlowId");
			 return budgetWorkerService.makeBudgets(houseFlowId, houseId, workerTypeId, listOfGoods);
		 }else{
			 return ServerResponse.createByErrorMessage("新增人工精算失败。原因:查询houseFlow失败！");
		 }
	 }

	/**
	 * 根据houseId和wokerTypeId查询房子人工精算总价
	 * @param houseId
	 * @param workerTypeId
	 * @return
	 */
	@SuppressWarnings("static-access")
	@Override
	@ApiMethod
	public ServerResponse getWorkerTotalPrice(String houseId,String workerTypeId){
			return budgetWorkerService.getWorkerTotalPrice(houseId, workerTypeId);
	}

	/**
	 * 业主修改精算
	 * @param listOfGoods
	 * @return
	 */
	@SuppressWarnings("static-access")
	@Override
	@ApiMethod
	public ServerResponse doModifyBudgets(String listOfGoods){
			return budgetWorkerService.doModifyBudgets(listOfGoods);
	}

	/**
	 *  估价
	 * @param houseId
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse gatEstimateBudgetByHId(String houseId){
		return budgetWorkerService.gatEstimateBudgetByHId(houseId);
	}

	/**
	 *  房子精算
	 * @param houseId
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse gatBudgetResultByHouse(String houseId){
		return budgetWorkerService.gatBudgetResultByHouse(houseId);
	}
}
