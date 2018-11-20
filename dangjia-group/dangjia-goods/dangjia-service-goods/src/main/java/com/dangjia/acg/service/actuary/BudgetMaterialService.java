package com.dangjia.acg.service.actuary;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.pay.BusinessOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Service
public class BudgetMaterialService {

	@Autowired
	private IBudgetMaterialMapper iBudgetMaterialMapper;

	@Autowired
	private IGoodsMapper iGoodsMapper;

	@Autowired
	private IProductMapper iProductMapper;

	//查询所有精算
	public ServerResponse getAllBudgetMaterial(){
		try {
			List<Map<String, Object>> mapList=iBudgetMaterialMapper.getBudgetMaterial();
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据HouseFlowId查询房子材料精算
	public ServerResponse queryBudgetMaterialByHouseFlowId(String houseFlowId){
		try {
			Example example = new Example(BudgetMaterial.class);
			example.createCriteria().andEqualTo("houseFlowId", houseFlowId).andEqualTo("deleteState", 0);
			List<BudgetMaterial> budgetMaterialist = iBudgetMaterialMapper.selectByExample(example);
			return ServerResponse.createBySuccess("查询成功",budgetMaterialist);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据houseId和wokerTypeId查询房子材料精算
	public ServerResponse getAllBudgetMaterialById(String houseId,String workerTypeId){
		try {
			List<Map<String, Object>> mapList=iBudgetMaterialMapper.getBudgetMaterialById(houseId,workerTypeId);
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据Id查询到精算
	public ServerResponse getBudgetMaterialByMyId(String id){
		try {
			BudgetMaterial budgetMaterial = iBudgetMaterialMapper.selectByPrimaryKey(id);
			return ServerResponse.createBySuccess("查询成功",budgetMaterial);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据ID删除精算
	public ServerResponse deleteById(String id){
		try {
			iBudgetMaterialMapper.deleteById(id);
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}

	//根据类别Id查到所有所属商品goods
	public ServerResponse getAllGoodsByCategoryId(String categoryId){
		try {
			List<Goods> mapList=iGoodsMapper.queryByCategoryId(categoryId);
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据商品Id查货品
	public ServerResponse getAllProductByGoodsId(String goodsId){
		try {
			List<Product> mapList = iProductMapper.queryByGoodsId(goodsId);
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
}
