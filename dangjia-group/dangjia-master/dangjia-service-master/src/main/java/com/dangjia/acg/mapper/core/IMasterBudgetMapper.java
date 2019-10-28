package com.dangjia.acg.mapper.core;

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
public interface IMasterBudgetMapper extends Mapper<BudgetMaterial> {


    List<Map<String,Object>> getAllBudgetMaterialWorkerList(@Param("houseId") String houseId, @Param("workerTypeId") String workerTypeId);

	/**
	 * 查询下单按店铺汇总的价钱
	 * @param houseId
	 * @return
	 */
	List<Map<String,Object>> getHouseDetailInfoList(@Param("houseId") String houseId);

	/**
	 * 查询对应店铺下已精算的商品信息
	 * @param houseId
	 * @param storefrontId
	 * @return
	 */
	List<ActuarialProductAppDTO> getBudgetProductList(@Param("houseId") String houseId, @Param("storefrontId") String storefrontId);

	/**支付时工种人工总价*/
	Double getMasterBudgetWorkerPrice(@Param("houseId")String houseId, @Param("workerTypeId")String workerTypeId);
}
