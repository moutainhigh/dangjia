package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.product.BasicsgDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
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
public interface IBudgetWorkerMapper extends Mapper<BudgetMaterial> {

	List<BudgetMaterial> getTypeAllList(@Param("houseId")String houseId, @Param("deleteState")String deleteState, @Param("workerTypeId")String workerTypeId);
	Double getTypeAllPrice(@Param("houseId")String houseId, @Param("deleteState")String deleteState, @Param("workerTypeId")String workerTypeId);
	Double getHouseWorkerPrice(@Param("houseId")String houseId,@Param("deleteState")String deleteState);
	List<String> workerTypeList(@Param("houseId")String houseId);

	BudgetMaterial byWorkerGoodsId(@Param("houseId")String houseId, @Param("workerGoodsId")String workerGoodsId);

	/**getByHouseFlowId*/
	List<BudgetMaterial> getByHouseFlowId(@Param("houseId")String houseId, @Param("houseFlowId")String houseFlowId);
	/**getByHouseFlowId*/
	List<Technology> getByHouseFlowTechnologyId(@Param("houseId")String houseId, @Param("houseFlowId")String houseFlowId);

	/**支付时工种人工总价*/
	Double getBudgetWorkerPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**未支付人工精算*/
	List<BudgetMaterial> getBudgetWorkerList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**查询所有精算*/
	List<Map<String,Object>> getBudgetWorker();
	/**根据风格查询所有精算*/
	List<Map<String,Object>> getAllbudgetTemplates(String template_id);
	/**根据houseId和workerTypeId查询房子人工精算*/
	List<Map<String,Object>> getBudgetWorkerById(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据houseId和workerTypeId查询房子人工精算*/
	List<BudgetMaterial> getBudgetWorkerByHouseIdAndWorkerTypeId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据拿到的Id删除精算*/
	void deleteById(String budgetWorkerId);
	/**根据拿到的Id删除精算*/
	void deleteBytemplateId(String template_id);
	/**根据houseId删除材料精算*/
	void deleteByhouseId(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**根据houseId和workerTypeId查询房子人工精算总价*/
	Map<String,Object> getWorkerTotalPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	//void insertByBatch(@Param("list")List<BudgetWorker> list);

	/*更新人工商品名称及属性*/
	void updateBudgetMaterialById(@Param("id") String id);

	/*更新人工商品名称及属性(商品3.0改版后的关联)*/
	void updateBudgetMaterialByProductId(@Param("id") String id);


    /**
     * 查询商品
     * @param houseId
     * @param id
     * @return
     */
    List<BasicsgDTO> queryMakeBudgetsList(@Param("houseId")String houseId, @Param("id")String id);


    List<BasicsgDTO> queryMakeBudgetsBmList(@Param("houseId")String houseId, @Param("id")String id);

    List<Map<String,Object>> getAllBudgetMaterialWorkerList(@Param("houseId") String  houseId,@Param("workerTypeId") String workerTypeId);

	/**
	 * 查询下单按店铺汇总的价钱
	 * @param houseId
	 * @return
	 */
	List<Map<String,Object>> getHouseDetailInfoList(@Param("houseId") String  houseId);

	/**
	 * 查询对应店铺下已精算的商品信息
	 * @param houseId
	 * @param storefrontId
	 * @return
	 */
	List<ActuarialProductAppDTO> getBudgetProductList(@Param("houseId") String houseId, @Param("storefrontId")String storefrontId);
}
