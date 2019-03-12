package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.dto.actuary.BudgetStageCostDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
   * @类 名： BudgetMaterialDao.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-15下午4:28:28
 */
@Repository
public interface IBudgetMaterialMapper extends Mapper<BudgetMaterial> {

	/**精算阶段花费统计*/
	List<BudgetStageCostDTO> getHouseBudgetStageCost(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	List<BudgetMaterial> getCategoryAllList(@Param("houseId")String houseId, @Param("categoryId")String categoryId);
	Double getCategoryAllPrice(@Param("houseId")String houseId, @Param("categoryId")String categoryId);
	Double getHouseCaiPrice(@Param("houseId")String houseId);
	List<String> categoryIdList(@Param("houseId")String houseId);

	/**查询精算内商品*/
	List<BudgetMaterial> repairBudgetMaterial(@Param("workerTypeId")String workerTypeId,@Param("houseId")String houseId,
											  @Param("categoryId")String categoryId,@Param("productName")String productName);
	/**修改业主取消的精算去自购*/
	int updateSelf(@Param("houseFlowId") String houseFlowId);
	/**支付时工种服务总价*/
	Double getBudgetSerPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**支付时工种未选择服务总价*/
	Double getNotSerPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**支付时工种材料总价*/
	Double getBudgetCaiPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**支付时工种未选择材料总价*/
	Double getNotCaiPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	/**未支付材料精算*/
	List<BudgetMaterial> getBudgetCaiList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	/**未支付材料精算*/
	BudgetMaterial getBudgetCaiListByGoodsId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId,@Param("goodsId")String goodsId);

	/**未支付服务精算*/
	List<BudgetMaterial> getBudgetSerList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**查询所有商品精算*/
	List<Map<String,Object>> getBudgetMaterial();
	/**根据风格查询所有精算*/
	List<Map<String,Object>> getAllbudgetTemplates(String template_id);
	/**根据house_id查询所有商品精算*/
	List<Map<String,Object>> getBudgetMaterialById(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	/**根据house_id查询所有商品精算*/
	List<BudgetMaterial> getBudgetMaterialByHouseIdAndWorkerTypeId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	/**根据拿到的Id删除精算*/
	void deleteById(String id);
	/**根据拿到的Id删除精算*/
	void deleteBytemplateId(String template_id);
	/**根据houseId删除材料精算*/
	void deleteByhouseId(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);
	/**根据houseFlow查询已支付后精算价格*/
	Double getAbmPayOutByHfId(@Param("houseFlowId")String houseFlowId);
	/**根据houseFlow查询未支付实时精算价格*/
	Double getAbmCasualByHfId(@Param("houseFlowId")String houseFlowId);
}
