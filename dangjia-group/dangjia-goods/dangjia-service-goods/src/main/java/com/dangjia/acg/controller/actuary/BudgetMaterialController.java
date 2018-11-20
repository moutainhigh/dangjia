package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.BudgetMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
   * @类 名： BudgetMaterialController.java
   * @功能描述：  
   * @创建时间： 2018-9-18下午3:53:07
 */
@RestController
public class BudgetMaterialController implements BudgetMaterialAPI {
	/**
	 * 注入service
	 */
	@Autowired
	private BudgetMaterialService budgetMaterialService;
	@Autowired
	private GetForBudgetAPI getForBudgetAPI;
	/**
	 * 查询所有精算
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getAllBudgetMaterial(){
		return budgetMaterialService.getAllBudgetMaterial();
	}
	//根据HouseFlowId查询房子材料精算
	@Override
	@ApiMethod
	public ServerResponse queryBudgetMaterialByHouseFlowId(String houseFlowId){
		return budgetMaterialService.queryBudgetMaterialByHouseFlowId(houseFlowId);
	}
	/**
	 * 根据houseId和wokerTypeId查询房子材料精算
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getAllBudgetMaterialById(String houseId,String workerTypeId){
		return budgetMaterialService.getAllBudgetMaterialById(houseId,workerTypeId);
	}
	/**
	 * 根据id查询精算
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getBudgetMaterialById(String id){
		return budgetMaterialService.getBudgetMaterialByMyId(id);
	}
	/**
	 * 根据类别Id查询所属商品
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getAllGoodsByCategoryId(String categoryId){
		return budgetMaterialService.getAllGoodsByCategoryId(categoryId);
	}
	/**
	 * 根据商品Id查询货品
	 * @param goodsId
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse  getAllProductByGoodsId(String goodsId){
		return budgetMaterialService.getAllProductByGoodsId(goodsId);
	}
}
