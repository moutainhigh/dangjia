package com.dangjia.acg.mapper.actuary;

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
	/**
	 * 未删除材料精算
	 */
	List<BudgetMaterial> getBudgetCaiList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
	/**
	 * 未删除服务精算
	 */
	List<BudgetMaterial> getBudgetSerList(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);

	/**查询所有商品精算*/
	List<Map<String,Object>> getBudgetMaterial();
	/**根据风格查询所有精算*/
	List<Map<String,Object>> getAllbudgetTemplates(String template_id);
	/**根据house_id查询所有商品精算*/
	List<Map<String,Object>> getBudgetMaterialById(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
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
