package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.core.HouseFlowAPI;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.basics.BudgetListResult;
import com.dangjia.acg.dto.basics.BudgetResult;
import com.dangjia.acg.dto.basics.RlistResult;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.util.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class BudgetWorkerService {
	@Autowired
	private IBudgetWorkerMapper iBudgetWorkerMapper;
	@Autowired
	private IBudgetMaterialMapper iBudgetMaterialMapper;
	@Autowired
	private IWorkerGoodsMapper iWorkerGoodsMapper;
	@Autowired
	private IGoodsMapper iGoodsMapper;
	@Autowired
	private IProductMapper iProductMapper;
	@Autowired
	private IActuarialTemplateMapper iActuarialTemplateMapper;
	@Autowired
	private WorkerTypeAPI workerTypeAPI;
	@Autowired
	private HouseFlowAPI houseFlowAPI;
	@Autowired
	private HouseAPI houseAPI;
	@Autowired
	private ITechnologyMapper iTechnologyMapper;

	//查询所有精算
	public ServerResponse getAllBudgetWorker(){
		try {
			List<Map<String, Object>> mapList=iBudgetWorkerMapper.getBudgetWorker();
			for(Map<String, Object> obj:mapList){
				ServerResponse serverResponse =workerTypeAPI.getNameByWorkerTypeId(obj.get("workerTypeId").toString());
				String  workerTypeName="";
				if(serverResponse.isSuccess()) {
					workerTypeName = serverResponse.getResultObj().toString();
				}
				obj.put("workerTypeName",workerTypeName);
			}
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据houseId和wokerTypeId查询房子人工精算
	public ServerResponse getAllBudgetWorkerById(String houseId,String workerTypeId){
		try {
			List<Map<String, Object>> mapList=iBudgetWorkerMapper.getBudgetWorkerById(houseId,workerTypeId);
			return ServerResponse.createBySuccess("查询成功",mapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//根据Id查询到精算
	public ServerResponse getBudgetWorkerByMyId(String id){
		try {
			BudgetWorker budgetWorker = iBudgetWorkerMapper.selectByPrimaryKey(id);
			return ServerResponse.createBySuccess("查询成功",budgetWorker);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}

	//获取所有人工商品
	public ServerResponse getAllWorkerGoods(){
		try {
			List<WorkerGoods> workerList= iWorkerGoodsMapper.selectLists();
			return ServerResponse.createBySuccess("查询成功",workerList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//修改精算模板
		public ServerResponse updateBudgetTemplate(String listOfGoods,String workerTypeId,String templateId){
			iBudgetMaterialMapper.deleteBytemplateId(templateId);
			iBudgetWorkerMapper.deleteBytemplateId(templateId);
			JSONArray goodsList = JSONArray.parseArray(listOfGoods);
			for (int i = 0; i < goodsList.size(); i++) {
				JSONObject job = goodsList.getJSONObject(i);
				String productId = job.getString("productId");//得到值
				Double shopCount =Double.parseDouble(job.getString("shopcount"));
				Integer productType =Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
				String groupType =job.getString("groupType");//null：单品；有值：关联组合
				String goodsGroupId =job.getString("goodsGroupId");//所属关联组
				if (0==productType||1==productType){//材料或者服务
					try {
						BudgetMaterial budgetMaterial = new BudgetMaterial();
						Product pro =iProductMapper.getById(productId);
						if(pro==null){
							continue;
						}
						Goods goods = iGoodsMapper.queryById(pro.getGoodsId());
						budgetMaterial.setWorkerTypeId(workerTypeId);
						budgetMaterial.setSteta(3);
						budgetMaterial.setTemplateId(templateId);
						budgetMaterial.setDeleteState(0);
						budgetMaterial.setProductId(pro.getId().toString());
						budgetMaterial.setProductSn(pro.getProductSn());
						budgetMaterial.setGoodsId(pro.getGoodsId());
						budgetMaterial.setGoodsName(goods.getName());
						budgetMaterial.setProductName(pro.getName());
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setPrice(pro.getPrice());
						budgetMaterial.setCost(pro.getCost());
						budgetMaterial.setShopCount(shopCount);
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setDescription("材料精算模板");
						BigDecimal b1 = new BigDecimal(Double.toString(pro.getPrice()));
						BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
						Double totalprice = b1.multiply(b2).doubleValue();
						budgetMaterial.setTotalPrice(totalprice);
						budgetMaterial.setCreateDate(new Date());
						budgetMaterial.setModifyDate(new Date());
						budgetMaterial.setProductType(productType);
						budgetMaterial.setGroupType(groupType);
						budgetMaterial.setGoodsGroupId(goodsGroupId);
						iBudgetMaterialMapper.insert(budgetMaterial);
					} catch (Exception e) {
						e.printStackTrace();
						throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
					}
				}else if (2==productType) {//人工商品
					try {
						BudgetWorker budgetWorker = new BudgetWorker();
						WorkerGoods workerGoods = iWorkerGoodsMapper.selectByPrimaryKey(productId);
						if(workerGoods==null){
							continue;
						}
						budgetWorker.setWorkerTypeId(workerTypeId);
						budgetWorker.setSteta(3);
						budgetWorker.setTemplateId(templateId);
						budgetWorker.setDeleteState(0);
						budgetWorker.setWorkerGoodsId(workerGoods.getId());
						budgetWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
						budgetWorker.setName(workerGoods.getName());
						budgetWorker.setPrice(workerGoods.getPrice());
						budgetWorker.setShopCount(shopCount);
						budgetWorker.setUnit(workerGoods.getUnitId());
						BigDecimal b1 = new BigDecimal(Double.toString(workerGoods.getPrice()));
						BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
						Double totalprice = b1.multiply(b2).doubleValue();
						budgetWorker.setTotalPrice(totalprice);
						budgetWorker.setDescription("人工精算模板");
						budgetWorker.setCreateDate(new Date());
						budgetWorker.setModifyDate(new Date());
						iBudgetWorkerMapper.insert(budgetWorker);
					} catch (Exception e) {
						e.printStackTrace();
						throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
					}
				}else{
					throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
				}
		}
			return ServerResponse.createBySuccessMessage("修改成功");
		}
	//生成精算模板
	public ServerResponse makeBudgetTemplate(String listOfGoods,String workerTypeId,String templateId){
		JSONArray goodsList = JSONArray.parseArray(listOfGoods);
		for (int i = 0; i < goodsList.size(); i++) {
			JSONObject job = goodsList.getJSONObject(i);
				String productId = job.getString("productId");//得到值
				Double shopCount =Double.parseDouble(job.getString("shopcount"));
				Integer productType =Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
				String groupType =job.getString("groupType");//null：单品；有值：关联组合
				String goodsGroupId =job.getString("goodsGroupId");//所属关联组
				if (0==productType||1==productType){//材料或者服务
					try {
						BudgetMaterial budgetMaterial = new BudgetMaterial();
						Product pro =iProductMapper.getById(productId);
						if(pro==null){
							continue;
							}
							Goods goods = iGoodsMapper.queryById(pro.getGoodsId());
							budgetMaterial.setWorkerTypeId(workerTypeId);
							budgetMaterial.setSteta(3);
							budgetMaterial.setTemplateId(templateId);
							budgetMaterial.setDeleteState(0);
							budgetMaterial.setProductId(pro.getId().toString());
							budgetMaterial.setProductSn(pro.getProductSn());
							budgetMaterial.setGoodsId(pro.getGoodsId());
							budgetMaterial.setGoodsName(goods.getName());
							budgetMaterial.setProductName(pro.getName());
							budgetMaterial.setUnit(pro.getUnit());
							budgetMaterial.setPrice(pro.getPrice());
							budgetMaterial.setCost(pro.getCost());
							budgetMaterial.setShopCount(shopCount);
							budgetMaterial.setUnit(pro.getUnit());
							budgetMaterial.setDescription("材料精算模板");
							BigDecimal b1 = new BigDecimal(Double.toString(pro.getPrice()));
							BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
							Double totalprice = b1.multiply(b2).doubleValue();
							budgetMaterial.setTotalPrice(totalprice);
							budgetMaterial.setCreateDate(new Date());
							budgetMaterial.setModifyDate(new Date());
							budgetMaterial.setProductType(productType);
							budgetMaterial.setGroupType(groupType);
							iBudgetMaterialMapper.insert(budgetMaterial);
						} catch (Exception e) {
							e.printStackTrace();
							return ServerResponse.createByErrorMessage("生成失败");
						}
					}else if (2==productType) {//人工商品
						try {
							BudgetWorker budgetWorker = new BudgetWorker();
							WorkerGoods workerGoods = iWorkerGoodsMapper.selectByPrimaryKey(productId);
							budgetWorker.setWorkerTypeId(workerTypeId);
							budgetWorker.setSteta(3);
							budgetWorker.setTemplateId(templateId);
							budgetWorker.setDeleteState(0);
							budgetWorker.setWorkerGoodsId(workerGoods.getId().toString());
							budgetWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
							budgetWorker.setName(workerGoods.getName());
							budgetWorker.setPrice(workerGoods.getPrice());
							budgetWorker.setShopCount(shopCount);
							budgetWorker.setUnit(workerGoods.getUnitId());
							BigDecimal b1 = new BigDecimal(Double.toString(workerGoods.getPrice()));
							BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
							Double totalprice = b1.multiply(b2).doubleValue();
							budgetWorker.setTotalPrice(totalprice);
							budgetWorker.setDescription("人工精算模板");
							budgetWorker.setCreateDate(new Date());
							budgetWorker.setModifyDate(new Date());
							iBudgetWorkerMapper.insert(budgetWorker);
						} catch (Exception e) {
							e.printStackTrace();
							return ServerResponse.createByErrorMessage("生成失败");
						}
				}else{
					continue;//跳过此货品生产精算
				}
			}
		return ServerResponse.createBySuccessMessage("生成精算成功");
	}
	//查询该风格下的精算模板
	 public  ServerResponse getAllbudgetTemplates(String templateId){
		 try {
			 Map<String,Object> map = new HashMap<>();
			 List<Map<String, Object>> wokerList = iBudgetWorkerMapper.getAllbudgetTemplates(templateId);
			 for(Map<String, Object> obj:wokerList){
				 ServerResponse serverResponse =workerTypeAPI.getNameByWorkerTypeId(obj.get("workerTypeId").toString());
				 String  workerTypeName="";
				 if(serverResponse.isSuccess()) {
					 workerTypeName = serverResponse.getResultObj().toString();
				 }
			 	obj.put("workerTypeName",workerTypeName);
			 }
			 map.put("wokerList",wokerList);//人工精算
			 List<Map<String, Object>> materialList=iBudgetMaterialMapper.getAllbudgetTemplates(templateId);
			 map.put("materialList",materialList);//材料精算
			 return ServerResponse.createBySuccess("查询精算成功",map);
		} catch (Exception e) {
			e.printStackTrace();
			 throw new BaseException(ServerCode.WRONG_PARAM, "查询精算失败");
		}
	 }
	 //使用模板
	 public ServerResponse useuseTheBudget(String id){
		 if (iBudgetMaterialMapper.selectByPrimaryKey(id)!= null) {
			try {
				BudgetMaterial budgetMaterial = iBudgetMaterialMapper.selectByPrimaryKey(id);
				iActuarialTemplateMapper.useById(budgetMaterial.getTemplateId());
				return ServerResponse.createBySuccess("查询成功",budgetMaterial);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
			}
		}else if(iBudgetWorkerMapper.selectByPrimaryKey(id)!= null){
			try {
				BudgetWorker budgetWorker = iBudgetWorkerMapper.selectByPrimaryKey(id);
				iActuarialTemplateMapper.useById(budgetWorker.getTemplateId());
				return ServerResponse.createBySuccess("查询成功",budgetWorker);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
			}
		}else{
			 throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	 }
	 //生成精算
	 public ServerResponse makeBudgets(String houseFlowId,String houseId,String workerTypeId,String listOfGoods){
		try {
			iBudgetMaterialMapper.deleteByhouseId(houseId,workerTypeId);
			iBudgetWorkerMapper.deleteByhouseId(houseId,workerTypeId);
			JSONArray goodsList = JSONArray.parseArray(listOfGoods);
			for (int i = 0; i < goodsList.size(); i++) {
				JSONObject job = goodsList.getJSONObject(i);
				String goodsId = job.getString("goodsId");//商品id
				String productId = job.getString("productId");//货品id
				Double shopCount = Double.parseDouble(job.getString("shopcount"));//数量
				Double actuarialQuantity = Double.parseDouble(job.getString("actuarialQuantity"));//精算量
				Integer productType = Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
				String groupType = job.getString("groupType");//null：单品；有值：关联组合
				String goodsGroupId =job.getString("goodsGroupId");//所属关联组
				if (0 == productType || 1 == productType) {//材料或者服务
					try {
						BudgetMaterial budgetMaterial = new BudgetMaterial();
						Goods goods = iGoodsMapper.queryById(goodsId);
						if(goods==null){
							continue;
						}
						Product pro = iProductMapper.getById(productId);
						if(pro==null){
							List<Product> pList=iProductMapper.queryByGoodsId(goods.getId());
							if(pList.size()>0){
								pro=pList.get(0);
							}
						}
						budgetMaterial.setWorkerTypeId(workerTypeId);
						budgetMaterial.setHouseFlowId(houseFlowId);
						budgetMaterial.setHouseId(houseId);
						budgetMaterial.setSteta(goods.getBuy());
						budgetMaterial.setDeleteState(0);
						budgetMaterial.setProductId(pro.getId().toString());
						budgetMaterial.setProductSn(pro.getProductSn());
						budgetMaterial.setGoodsId(goodsId);
						budgetMaterial.setGoodsName(goods.getName());
						budgetMaterial.setProductName(pro.getName());
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setPrice(pro.getPrice());
						budgetMaterial.setCost(pro.getCost());
						budgetMaterial.setActuarialQuantity(actuarialQuantity);
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setCreateDate(new Date());
						budgetMaterial.setModifyDate(new Date());
						budgetMaterial.setProductType(productType);
						budgetMaterial.setGroupType(groupType);
						budgetMaterial.setGoodsGroupId(goodsGroupId);
						iBudgetMaterialMapper.insert(budgetMaterial);
					} catch (Exception e) {
						e.printStackTrace();
						return ServerResponse.createByErrorMessage("生成失败");
					}
				} else if (2 == productType) {//人工商品
					try {
						BudgetWorker budgetWorker = new BudgetWorker();
						WorkerGoods workerGoods = iWorkerGoodsMapper.selectByPrimaryKey(productId);
						if(workerGoods==null){
							continue;
						}
						budgetWorker.setHouseFlowId(houseFlowId);
						budgetWorker.setHouseId(houseId);
						budgetWorker.setWorkerTypeId(workerTypeId);
						budgetWorker.setSteta(1);
						budgetWorker.setDeleteState(0);
						budgetWorker.setWorkerGoodsId(workerGoods.getId().toString());
						budgetWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
						budgetWorker.setName(workerGoods.getName());
						budgetWorker.setPrice(workerGoods.getPrice());
						budgetWorker.setShopCount(shopCount);
						budgetWorker.setUnit(workerGoods.getUnitId());
						BigDecimal b1 = new BigDecimal(Double.toString(workerGoods.getPrice()));
						BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
						Double totalPrice = b1.multiply(b2).doubleValue();
						budgetWorker.setTotalPrice(totalPrice);
						budgetWorker.setCreateDate(new Date());
						budgetWorker.setModifyDate(new Date());
						iBudgetWorkerMapper.insert(budgetWorker);
					} catch (Exception e) {
						e.printStackTrace();
						return ServerResponse.createByErrorMessage("生成失败");
					}
				} else {
					continue;
				}
			}
			return ServerResponse.createBySuccessMessage("生成精算成功");
		}catch(Exception e){
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("生成失败");
		}
	 }

	/**根据houseId和wokerTypeId查询房子人工精算总价*/
	public ServerResponse getWorkerTotalPrice(String houseId,String workerTypeId){
		try {
			//JdbcContextHolder.putDataSource(DataSourceType.DJ_CHANGSHA.getName());
			Map<String, Object> map=iBudgetWorkerMapper.getWorkerTotalPrice(houseId,workerTypeId);
			return ServerResponse.createBySuccess("查询成功",map);
		} catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("生成失败");
		}
	}

	//业主修改材料精算
	public ServerResponse doModifyBudgets(String listOfGoods){
		try {
			JSONArray goodsList = JSONArray.parseArray(listOfGoods);
			for (int i = 0; i < goodsList.size(); i++) {
				JSONObject job = goodsList.getJSONObject(i);
				String id = job.getString("id");//精算id
				String goodsId = job.getString("goodsId");//商品id
				String productId = job.getString("productId");//货品id
				Integer productType = Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
				String groupType = job.getString("groupType");//null：单品；有值：关联组合
				String goodsGroupId =job.getString("goodsGroupId");//所属关联组
				if (0 == productType || 1 == productType) {//材料或者服务
					try {
						BudgetMaterial budgetMaterial =iBudgetMaterialMapper.selectByPrimaryKey(id);
						Goods goods = iGoodsMapper.queryById(goodsId);
						if(goods==null){
							continue;
						}
						Product pro = iProductMapper.getById(productId);
						if(pro==null){
							List<Product> pList=iProductMapper.queryByGoodsId(goods.getId());
							if(pList.size()>0){
								pro=pList.get(0);
							}
						}
						budgetMaterial.setProductId(pro.getId().toString());
						budgetMaterial.setProductSn(pro.getProductSn());
						budgetMaterial.setGoodsId(goodsId);
						budgetMaterial.setGoodsName(goods.getName());
						budgetMaterial.setProductName(pro.getName());
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setPrice(pro.getPrice());
						budgetMaterial.setCost(pro.getCost());
						budgetMaterial.setUnit(pro.getUnit());
						budgetMaterial.setModifyDate(new Date());
						budgetMaterial.setProductType(productType);
						budgetMaterial.setGroupType(groupType);
						budgetMaterial.setGoodsGroupId(goodsGroupId);
						Double actuarialQuantity=budgetMaterial.getActuarialQuantity();//精算量
						Double convertQuality=pro.getConvertQuality();//换算单位量
						DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
						Double shopCount=Double.parseDouble(df.format(actuarialQuantity/convertQuality));//购买数量
						budgetMaterial.setShopCount(shopCount);
						Double totalPrice=Double.parseDouble(df.format(shopCount*pro.getPrice()));//购买总价
						budgetMaterial.setTotalPrice(totalPrice);
						iBudgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
					} catch (Exception e) {
						e.printStackTrace();
						return ServerResponse.createByErrorMessage("修改精算失败");
					}
				}
			}
			return ServerResponse.createBySuccessMessage("修改精算成功");
		}catch(Exception e){
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("修改精算失败");
		}
	}

	/**
	 *  估价
	 *  已支付工种 hflist.get(i).getWorktype() == 4 查精算表里价格
	 *  未支付查商品库价格
	 * @param houseId
	 */
	public ServerResponse gatEstimateBudgetByHId(String houseId){
		try{
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			BudgetResult budgetResult = new BudgetResult();
			budgetResult.setWorkerBudget(0.0);
			budgetResult.setMaterialBudget(0.0);
			House house=houseAPI.getHouseById(houseId);
			HouseFlow houseFlow = houseFlowAPI.getHouseFlowByHidAndWty(house.getId(), 2);
			if(houseFlow!=null){
				List<HouseFlow> hflist = houseFlowAPI.getFlowByhouseIdNot12(house.getId());
				RlistResult rlistResult = null;
				budgetResult.setBudgetDec(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber()+"号，总计：");
				List<BudgetListResult> biglist = new ArrayList<BudgetListResult>();//人工list
				List<BudgetListResult> cailist = new ArrayList<BudgetListResult>();//材料list

				/*******************************人工费********************************/
				for(int i=0; i<hflist.size(); i++){
					BudgetListResult blr = new BudgetListResult();
					List<RlistResult> rlist = new ArrayList<RlistResult>();//算人力
					WorkerType workType=new WorkerType();
					ServerResponse serverResponse = workerTypeAPI.getWorkerType(hflist.get(i).getWorkerTypeId());
					if(serverResponse.isSuccess()) {
						workType = JSON.parseObject(serverResponse.getResultObj().toString(),WorkerType.class);
					}
					//二级人工费
					blr.setListName(workType.getName()+"人工费用");
					blr.setUrl(StringTool.getUrl(request)+"/app/app_budget_material!index.action?houseflow.id="+
							hflist.get(i).getId()+"&title"+workType.getName()+"title");

					Double rgf=0.00;//二级费用统计
					Example example=new Example(BudgetWorker.class);
					example.createCriteria().andEqualTo("houseFlow",hflist.get(i).getId()).andCondition("deleteState!=1");
					List<BudgetWorker> bwList=iBudgetWorkerMapper.selectByExample(example);
					for(BudgetWorker abw : bwList){//增加一层循环遍历存储下级子项目
						WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
						rlistResult =new RlistResult();
						rlistResult.setRId(abw.getId());//id
						//单价
						rlistResult.setRCost(wg.getPrice()==null?0:wg.getPrice());//单价
						rlistResult.setRName(wg.getName());//名称
						Double gjjg=abw.getShopCount()*wg.getPrice();
						rlistResult.setSumRcost(gjjg);//合计价格
						Double number=abw.getShopCount();
						rlistResult.setNumber(number);//数量
						rlist.add(rlistResult);
						rgf+=rlistResult.getSumRcost();
						//总人工费
						budgetResult.setWorkerBudget(budgetResult.getWorkerBudget()+rlistResult.getSumRcost());
					}
					//二级人工费
					blr.setListCost(rgf.toString());
					biglist.add(blr);
				}
				budgetResult.setBigList(biglist);

				/*************************材料费*************************************/
				hflist = houseFlowAPI.getFlowByhouseIdNot12(house.getId());
				for(int i=0; i<hflist.size(); i++){
					List<RlistResult> rlist = new ArrayList<RlistResult>();//算材料
					BudgetListResult blr = new BudgetListResult();
					Double clf=0.0;//二级费用统计
					WorkerType workType=new WorkerType();
					ServerResponse serverResponse = workerTypeAPI.getWorkerType(hflist.get(i).getWorkerTypeId());
					if(serverResponse.isSuccess()) {
						workType = JSON.parseObject(serverResponse.getResultObj().toString(),WorkerType.class);
					}
					blr.setListName(workType.getName()+"材料费");
					blr.setUrl( StringTool.getUrl(request)+"/app/app_budget_material!list.action?houseflow.id="+
							hflist.get(i).getId()+"&title"+workType.getName()+"材料费title");
					Example example=new Example(BudgetMaterial.class);
					example.createCriteria().andEqualTo("houseFlowId",hflist.get(i).getId()).andCondition("deleteState!=1");
					List<BudgetMaterial> abmList = iBudgetMaterialMapper.selectByExample(example);//获取每个工序对应的材料表
					for(BudgetMaterial abm : abmList){//每个商品
						Product product = iProductMapper.selectByPrimaryKey(abm.getProductId());
						Goods goods = iGoodsMapper.selectByPrimaryKey(product.getGoodsId());
						rlistResult =new RlistResult();
						rlistResult.setRId(abm.getId());//id
						if(hflist.get(i).getWorkType() == 4){
							Double cailiao = hflist.get(i).getMaterialPrice().doubleValue();//支付后
							if(cailiao != null){
								rlistResult.setRCost(cailiao);
							}else{
								rlistResult.setRCost(iBudgetMaterialMapper.getAbmPayOutByHfId(hflist.get(i).getId()));
							}
						}else{
							//没支付查实时价格
							rlistResult.setRCost(iBudgetMaterialMapper.getAbmCasualByHfId(hflist.get(i).getId()));
						}
						//单价
						if(product != null){
							rlistResult.setRName(product.getName());
						}else{
							rlistResult.setRName(goods.getName());
						}
						Double gjjg=(abm.getShopCount()*(abm.getPrice()==null?product.getPrice():abm.getPrice()));
						rlistResult.setSumRcost(gjjg);//合计价格
						rlistResult.setNumber(abm.getShopCount());//数量
						rlist.add(rlistResult);
						clf+=rlistResult.getSumRcost();
						//总材料费
						budgetResult.setMaterialBudget(budgetResult.getMaterialBudget()+rlistResult.getSumRcost());
					}

					blr.setListCost(clf.toString());
					//blr.setRlist(rlist);
					cailist.add(blr);
				}
				budgetResult.setBigList(biglist);//人工list
				budgetResult.setCaiList(cailist);//材料list
				/****************************总项***************************/
				budgetResult.setCost(budgetResult.getMaterialBudget()+budgetResult.getWorkerBudget());//总价
				budgetResult.setMianji("面积："+house.getSquare()+"m²");//面积
				BigDecimal d=BigDecimal.valueOf(budgetResult.getCost()).divide(house.getSquare(),2).setScale(2, BigDecimal.ROUND_HALF_UP);//总价除以总面积（单价）
				budgetResult.setDanjia("单价：￥"+d+"/m²");//单价
				return ServerResponse.createBySuccess("查询估价成功",budgetResult);
			}else{
				return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"暂无数据");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("系统出错，查询估价失败");
		}
	}

	/**
	 *  1.3.0 房子精算
	 *  已支付工种 hflist.get(i).getWorktype() == 4 查精算表里价格
	 *  未支付查商品库价格
	 * @param houseId
	 */
	public ServerResponse gatBudgetResultByHouse(String houseId){
		Map<String, Object> returnMap=new HashMap<String, Object>();
		try{
			House house=houseAPI.getHouseById(houseId);
			HttpServletRequest request = ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest();
			HouseFlow houseFlow = houseFlowAPI.getHouseFlowByHidAndWty(house.getId(), 2);
			if(houseFlow!=null){
				List<HouseFlow> hflist = houseFlowAPI.getFlowByhouseIdNot12(house.getId());
				List<Map<String, Object>> workList=new ArrayList<Map<String,Object>>();//工序list
				List<Map<String, Object>> sList=new ArrayList<Map<String,Object>>();//准备阶段list
				Map<String, Object> wMap=new HashMap<String, Object>();
				wMap.put("workTypeName", "准备阶段");
				Map<String, Object> aMap=new HashMap<String, Object>();
				aMap.put("name", "设计费");
				aMap.put("pirce", "0");
				aMap.put("aUrl","");
				sList.add(aMap);
				aMap=new HashMap<String, Object>();
				aMap.put("name", "精算费");
				aMap.put("pirce", "0");
				aMap.put("aUrl","");
				sList.add(aMap);
				wMap.put("wList", sList);
				workList.add(wMap);
				for(int i=0; i<hflist.size(); i++){
					WorkerType workType=new WorkerType();
					ServerResponse serverResponse = workerTypeAPI.getWorkerType(hflist.get(i).getWorkerTypeId());
					if(serverResponse.isSuccess()) {
						workType = JSON.parseObject(serverResponse.getResultObj().toString(),WorkerType.class);
					}
					wMap=new HashMap<String, Object>();
					wMap.put("workTypeName", workType.getName());
					Example example=new Example(BudgetWorker.class);
					example.createCriteria().andEqualTo("houseFlow",hflist.get(i).getId()).andCondition("deleteState!=1");
					List<BudgetWorker> abwlist=iBudgetWorkerMapper.selectByExample(example);
					Example example2=new Example(BudgetMaterial.class);
					example2.createCriteria().andEqualTo("houseFlow",hflist.get(i).getId()).andCondition("deleteState!=1");
					List<BudgetMaterial> abmList = iBudgetMaterialMapper.selectByExample(example);//获取每个工序对应的材料表
					BigDecimal rgzj=new BigDecimal(0);
					BigDecimal clzj=new BigDecimal(0);
					BigDecimal fwzj=new BigDecimal(0);
					for(BudgetWorker abw : abwlist){//增加一层循环遍历存储下级子项目
						WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
						BigDecimal r=(new BigDecimal(abw.getShopCount()).multiply(new BigDecimal(wg.getPrice()))).setScale(2, BigDecimal.ROUND_HALF_UP);
						rgzj=rgzj.add(r);
					}
					for(BudgetMaterial abm : abmList){//每个商品
						Product product = iProductMapper.selectByPrimaryKey(abm.getProductId());
						Goods goods = iGoodsMapper.selectByPrimaryKey(product.getGoodsId());
						BigDecimal c=(new BigDecimal(abm.getShopCount()).multiply(abm.getPrice()==null?new BigDecimal(product.getPrice()):new BigDecimal(abm.getPrice()))).setScale(2, BigDecimal.ROUND_HALF_UP);
						if(goods.getType()==0) {//材料
							clzj = clzj.add(c);
						}else{
							fwzj= fwzj.add(c);
						}
					}
					List<Map<String, Object>> aList=new ArrayList<Map<String,Object>>();//子工序list
					aMap=new HashMap<String, Object>();
					aMap.put("aName", workType.getName()+"人工费");//人工费名称
					aMap.put("aPirce",rgzj);//人工费总计
					aMap.put("aUrl", StringTool.getUrl(request)+"/app/app_budget_material!index.action?houseflow.id="+
							hflist.get(i).getId()+"&title"+workType.getName());//人工费详情链接
					aList.add(aMap);
					aMap=new HashMap<String, Object>();
					aMap.put("aName",workType.getName()+"材料费");//材料费
					aMap.put("aPirce", clzj);//材料费总计
					aMap.put("aUrl", StringTool.getUrl(request)+"/app/app_budget_material!list.action?houseflow.id="+
							hflist.get(i).getId()+"&title"+workType.getName());//材料费详情链接
					aList.add(aMap);
					aMap=new HashMap<String, Object>();
					aMap.put("aName", "服务费");//服务费
					aMap.put("aPirce", fwzj);//服务费总计
					aMap.put("aUrl","");
					aList.add(aMap);
					wMap.put("wList", aList);
					workList.add(wMap);
				}
				returnMap.put("workList", workList);
			}else{
				return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"暂无数据");
			}
			return ServerResponse.createBySuccess("查询精算成功",returnMap);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("系统出错,查询精算失败");
		}
	}

	/**
	 * 根据houseId查询所有验收节点
	 * @param houseId
	 * @return
	 */
	public List<Map<String,Object>> getAllTechnologyByHouseId(String houseId){
		try{
			Example example=new Example(BudgetWorker.class);
			example.createCriteria().andEqualTo("houseId",houseId).andCondition("deleteState!=1");
			List<BudgetWorker> abwlist=iBudgetWorkerMapper.selectByExample(example);//根据houseId查询所有人工精算
			Example example2=new Example(BudgetMaterial.class);
			example2.createCriteria().andEqualTo("houseId",houseId).andCondition("deleteState!=1");
			List<BudgetMaterial> abmList = iBudgetMaterialMapper.selectByExample(example);//根据houseId查询所有材料精算
			for(BudgetWorker abw : abwlist){
				WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
				List<Technology> tList = iTechnologyMapper.queryTechnologyByWgId(wg.getId());
			}
			for(BudgetMaterial abm : abmList){//每个商品
				Product product = iProductMapper.selectByPrimaryKey(abm.getProductId());
				List<Technology> tList = iTechnologyMapper.queryTechnologyByWgId(product.getId());
			}
			return null;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
