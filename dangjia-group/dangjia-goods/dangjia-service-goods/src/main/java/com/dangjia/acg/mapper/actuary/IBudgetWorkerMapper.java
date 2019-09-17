package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Technology;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
   * @类 名： BudgetWorkerDao.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-15下午3:15:10
 */
@Repository
public interface IBudgetWorkerMapper extends Mapper<BudgetWorker> {

	List<BudgetWorker> getTypeAllList(@Param("houseId")String houseId, @Param("deleteState")String deleteState, @Param("workerTypeId")String workerTypeId);
	Double getTypeAllPrice(@Param("houseId")String houseId, @Param("deleteState")String deleteState, @Param("workerTypeId")String workerTypeId);
	Double getHouseWorkerPrice(@Param("houseId")String houseId,@Param("deleteState")String deleteState);
	List<String> workerTypeList(@Param("houseId")String houseId);

	BudgetWorker byWorkerGoodsId(@Param("houseId")String houseId, @Param("workerGoodsId")String workerGoodsId);

	/**getByHouseFlowId*/
	List<BudgetWorker> getByHouseFlowId(@Param("houseId")String houseId, @Param("houseFlowId")String houseFlowId);
	/**getByHouseFlowId*/
	List<Technology> getByHouseFlowTechnologyId(@Param("houseId")String houseId, @Param("houseFlowId")String houseFlowId);

	/**支付时工种人工总价*/
	Double getBudgetWorkerPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**未支付人工精算*/
	List<BudgetWorker> getBudgetWorkerList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**查询所有精算*/
	List<Map<String,Object>> getBudgetWorker();
	/**根据风格查询所有精算*/
	List<Map<String,Object>> getAllbudgetTemplates(String template_id);
	/**根据houseId和workerTypeId查询房子人工精算*/
	List<Map<String,Object>> getBudgetWorkerById(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据houseId和workerTypeId查询房子人工精算*/
	List<BudgetWorker> getBudgetWorkerByHouseIdAndWorkerTypeId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据拿到的Id删除精算*/
	void deleteById(String budgetWorkerId);
	/**根据拿到的Id删除精算*/
	void deleteBytemplateId(String template_id);
	/**根据houseId删除材料精算*/
	void deleteByhouseId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据houseId和workerTypeId查询房子人工精算总价*/
	Map<String,Object> getWorkerTotalPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	void insertByBatch(@Param("list")List<BudgetWorker> list);

	/*更新人工商品名称及属性*/
	void updateBudgetMaterialById(@Param("id") String id);

	/*更新人工商品名称及属性(商品3.0改版后的关联)*/
	void updateBudgetMaterialByProductId(@Param("id") String id);
}
