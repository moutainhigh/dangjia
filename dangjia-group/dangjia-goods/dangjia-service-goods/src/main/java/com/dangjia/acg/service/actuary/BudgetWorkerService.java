package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.core.HouseFlowAPI;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
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
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
    @Autowired
    private TechnologyRecordAPI technologyRecordAPI;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;

    private static Logger LOG = LoggerFactory.getLogger(BudgetWorkerService.class);


    //查询所有精算
    public ServerResponse getAllBudgetWorker() {
        try {
            List<Map<String, Object>> mapList = iBudgetWorkerMapper.getBudgetWorker();
            for (Map<String, Object> obj : mapList) {
                ServerResponse serverResponse = workerTypeAPI.getNameByWorkerTypeId(obj.get("workerTypeId").toString());
                String workerTypeName = "";
                if (serverResponse.isSuccess()) {
                    workerTypeName = serverResponse.getResultObj().toString();
                }
                obj.put("workerTypeName", workerTypeName);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据houseId和workerTypeId查询房子人工精算
    public ServerResponse getAllBudgetWorkerById(String houseId, String workerTypeId) {
        try {
            List<Map<String, Object>> mapList = iBudgetWorkerMapper.getBudgetWorkerById(houseId, workerTypeId);
            LOG.info("getAllBudgetWorkerById houseId:" + houseId + " workerTypeId:" + workerTypeId + " size:" + mapList.size());
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据Id查询到精算
    public ServerResponse getBudgetWorkerByMyId(String id) {
        try {
            BudgetWorker budgetWorker = iBudgetWorkerMapper.selectByPrimaryKey(id);
            return ServerResponse.createBySuccess("查询成功", budgetWorker);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //获取所有人工商品
    public ServerResponse getAllWorkerGoods() {
        try {
            List<WorkerGoods> workerList = iWorkerGoodsMapper.selectLists();
            return ServerResponse.createBySuccess("查询成功", workerList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //修改精算模板
    public ServerResponse updateBudgetTemplate(String listOfGoods, String workerTypeId, String templateId) {
        LOG.info("listOfGoods :" + listOfGoods + " workerTypeId:" +workerTypeId +" templateId:" +templateId );
        iBudgetMaterialMapper.deleteBytemplateId(templateId);
        iBudgetWorkerMapper.deleteBytemplateId(templateId);
        return budgetTemplates(listOfGoods, workerTypeId, templateId);
    }

    //生成精算模板
    public ServerResponse budgetTemplates(String listOfGoods, String workerTypeId, String templateId) {
        JSONArray goodsList = JSONArray.parseArray(listOfGoods);
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject job = goodsList.getJSONObject(i);
            BudgetMaterial jobT = job.toJavaObject(BudgetMaterial.class);
            if (jobT.getProductType() != 2) {//材料或者服务
                try {
                    BudgetMaterial budgetMaterial = new BudgetMaterial();
                    Goods goods = iGoodsMapper.queryById(jobT.getGoodsId());
                    if (goods == null) {
                        continue;
                    }
                    if (goods.getBuy() == 2 && !StringUtils.isNoneBlank(jobT.getProductId()))//2自购
                    {
                        budgetMaterial.setProductId("");
                        budgetMaterial.setProductSn("");
                        budgetMaterial.setProductName("");
                        budgetMaterial.setUnitName("");
                        budgetMaterial.setPrice(0.0);
                        budgetMaterial.setCost(0.0);
                        budgetMaterial.setImage("");//货品图片
                        budgetMaterial.setShopCount(0.0);
                        budgetMaterial.setUnitName("");
                        budgetMaterial.setTotalPrice(0.0);
                    } else {
                        Product pro = iProductMapper.getById(jobT.getProductId());
                        if (pro == null) {
                            continue;
                        }
                        budgetMaterial.setProductId(pro.getId());
                        budgetMaterial.setProductSn(pro.getProductSn());
                        budgetMaterial.setProductName(pro.getName());
                        budgetMaterial.setUnitName(pro.getUnitName());
                        budgetMaterial.setPrice(pro.getPrice());
                        budgetMaterial.setCost(pro.getCost());
                        budgetMaterial.setImage(pro.getImage());//货品图片
                       /* double a = jobT.getActuarialQuantity() / pro.getConvertQuality();
                        double shopCount = Math.ceil(a);*/
                        budgetMaterial.setShopCount(jobT.getShopCount());
                        budgetMaterial.setUnitName(pro.getUnitName());
                        BigDecimal b1 = new BigDecimal(Double.toString(pro.getPrice()));
                        BigDecimal b2 = new BigDecimal(Double.toString(jobT.getShopCount()));
                        Double totalPrice = b1.multiply(b2).doubleValue();
                        budgetMaterial.setTotalPrice(totalPrice);
                    }

                    budgetMaterial.setWorkerTypeId(workerTypeId);
                    budgetMaterial.setSteta(3);
                    budgetMaterial.setTemplateId(templateId);
                    budgetMaterial.setDeleteState(0);
                    budgetMaterial.setGoodsId(goods.getId());
                    budgetMaterial.setGoodsName(goods.getName());
                    budgetMaterial.setCategoryId(goods.getCategoryId());//商品分类
                   // budgetMaterial.setActuarialQuantity(jobT.getActuarialQuantity());
                    budgetMaterial.setDescription("材料精算模板");

                    budgetMaterial.setProductType(jobT.getProductType());
                    budgetMaterial.setGroupType(jobT.getGroupType());
                    budgetMaterial.setGoodsGroupId(jobT.getGoodsGroupId());
                    iBudgetMaterialMapper.insert(budgetMaterial);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ServerResponse.createByErrorMessage("生成精算失败");
                }
            } else if (jobT.getProductType() == 2) {//人工商品
                try {
                    Double shopCount = Double.parseDouble(job.getString("shopCount"));
                    BudgetWorker budgetWorker = new BudgetWorker();
                    WorkerGoods workerGoods = iWorkerGoodsMapper.selectByPrimaryKey(jobT.getProductId());
                    budgetWorker.setWorkerTypeId(workerTypeId);
                    budgetWorker.setSteta(3);
                    budgetWorker.setRepairCount(0.0);
                    budgetWorker.setBackCount(0.0);
                    budgetWorker.setTemplateId(templateId);
                    budgetWorker.setDeleteState(0);
                    budgetWorker.setWorkerGoodsId(workerGoods.getId());
                    budgetWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
                    budgetWorker.setName(workerGoods.getName());
                    budgetWorker.setPrice(workerGoods.getPrice());
                    budgetWorker.setShopCount(shopCount);
                    budgetWorker.setUnitName(workerGoods.getUnitName());
                    budgetWorker.setImage(workerGoods.getImage());
                    BigDecimal b1 = new BigDecimal(Double.toString(workerGoods.getPrice()));
                    BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
                    Double totalPrice = b1.multiply(b2).doubleValue();
                    budgetWorker.setTotalPrice(totalPrice);
                    budgetWorker.setDescription("人工精算模板");
                    iBudgetWorkerMapper.insert(budgetWorker);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException(ServerCode.WRONG_PARAM, "修改精算失败");
                }
            }
        }
        return ServerResponse.createBySuccessMessage("生成精算成功");
    }

    //查询该风格下的精算模板
    public ServerResponse getAllbudgetTemplates(String templateId) {
        try {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> wokerList = iBudgetWorkerMapper.getAllbudgetTemplates(templateId);
            for (Map<String, Object> obj : wokerList) {
                ServerResponse serverResponse = workerTypeAPI.getNameByWorkerTypeId(obj.get("workerTypeId").toString());
                String workerTypeName = "";
                if (serverResponse.isSuccess()) {
                    workerTypeName = serverResponse.getResultObj().toString();
                }
                obj.put("workerTypeName", workerTypeName);
            }
            map.put("wokerList", wokerList);//人工精算
            List<Map<String, Object>> materialList = iBudgetMaterialMapper.getAllbudgetTemplates(templateId);
            map.put("materialList", materialList);//材料精算
            return ServerResponse.createBySuccess("查询精算成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询精算失败");
        }
    }

    //使用模板
    public ServerResponse useuseTheBudget(String id) {
        if (iBudgetMaterialMapper.selectByPrimaryKey(id) != null) {
            try {
                BudgetMaterial budgetMaterial = iBudgetMaterialMapper.selectByPrimaryKey(id);
                iActuarialTemplateMapper.useById(budgetMaterial.getTemplateId());
                return ServerResponse.createBySuccess("查询成功", budgetMaterial);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
            }
        } else if (iBudgetWorkerMapper.selectByPrimaryKey(id) != null) {
            try {
                BudgetWorker budgetWorker = iBudgetWorkerMapper.selectByPrimaryKey(id);
                iActuarialTemplateMapper.useById(budgetWorker.getTemplateId());
                return ServerResponse.createBySuccess("查询成功", budgetWorker);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
            }
        } else {
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /**
     *   生成精算
     */
    public ServerResponse makeBudgets(String actuarialTemplateId, String houseId, String workerTypeId, String listOfGoods) {
        try {
            LOG.info("makeBudgets ***** :" + actuarialTemplateId);
            ServerResponse serverResponse = getForBudgetAPI.actuarialForBudget(houseId, workerTypeId);
            if (!serverResponse.isSuccess())
                return ServerResponse.createByErrorMessage("新增人工精算失败。原因:查询houseFlow失败！");

            JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
            String houseFlowId = obj.getString("houseFlowId");

            iBudgetMaterialMapper.deleteByhouseId(houseId, workerTypeId);
            iBudgetWorkerMapper.deleteByhouseId(houseId, workerTypeId);

            JSONArray goodsList = JSONArray.parseArray(listOfGoods);
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject job = goodsList.getJSONObject(i);
                String goodsId = job.getString("goodsId");//商品id
                String productId = job.getString("productId");//货品id
                Integer productType = Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
                String groupType = job.getString("groupType");//null：单品；有值：关联组合
                String goodsGroupId = job.getString("goodsGroupId");//所属关联组
                Double shopCount = Double.parseDouble(job.getString("shopCount"));//数量
                if (0 == productType || 1 == productType) {//材料或者服务
                    try {
                        BudgetMaterial budgetMaterial = new BudgetMaterial();
                        Goods goods = iGoodsMapper.queryById(goodsId);
                        if (goods == null) {
                            continue;
                        }
                        budgetMaterial.setWorkerTypeId(workerTypeId);
                        budgetMaterial.setHouseFlowId(houseFlowId);
                        budgetMaterial.setHouseId(houseId);
                        if (goods.getBuy() == 0 || goods.getBuy() == 1) {//0：必买；1可选；2自购
                            budgetMaterial.setSteta(1);//我们购

                            Product pro = iProductMapper.getById(productId);
                            if (pro == null) {
                                List<Product> pList = iProductMapper.queryByGoodsId(goods.getId());
                                if (pList.size() > 0) {
                                    pro = pList.get(0);
                                }
                            }
                            budgetMaterial.setProductId(pro.getId().toString());
                            budgetMaterial.setProductSn(pro.getProductSn());
                            budgetMaterial.setProductName(pro.getName());
                            budgetMaterial.setUnitName(pro.getUnitName());
                            budgetMaterial.setPrice(pro.getPrice());
                            budgetMaterial.setCost(pro.getCost());
                            budgetMaterial.setImage(pro.getImage());//货品图片
                           /* double a = actuarialQuantity / pro.getConvertQuality();
                            double shopCount = Math.ceil(a);*/
                            budgetMaterial.setShopCount(shopCount);
                            BigDecimal b1 = new BigDecimal(Double.toString(budgetMaterial.getPrice()));
                            BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
                            Double totalPrice = b1.multiply(b2).doubleValue();
                            budgetMaterial.setTotalPrice(totalPrice);
                            budgetMaterial.setUnitName(pro.getUnitName());
                        } else {
                            budgetMaterial.setSteta(2);//自购
                            budgetMaterial.setProductId("");
                            budgetMaterial.setProductSn("");
                            budgetMaterial.setProductName("");
                            budgetMaterial.setUnitName("");
                            budgetMaterial.setPrice(0.0);
                            budgetMaterial.setCost(0.0);
                            budgetMaterial.setImage("");//货品图片
                            budgetMaterial.setShopCount(shopCount);
                            budgetMaterial.setTotalPrice(0.0);
                            budgetMaterial.setUnitName("");
                        }

                        budgetMaterial.setDeleteState(0);
                        budgetMaterial.setGoodsId(goodsId);
                        budgetMaterial.setGoodsName(goods.getName());
                        budgetMaterial.setCategoryId(goods.getCategoryId());//商品分类
                       // budgetMaterial.setActuarialQuantity(actuarialQuantity);
                        budgetMaterial.setCreateDate(new Date());
                        budgetMaterial.setModifyDate(new Date());
                        budgetMaterial.setProductType(productType);
                        budgetMaterial.setGroupType(groupType);
                        budgetMaterial.setGoodsGroupId(goodsGroupId);
//                        budgetMaterial.setTemplateId(actuarialTemplateId);

                        iBudgetMaterialMapper.insert(budgetMaterial);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ServerResponse.createByErrorMessage("生成失败");
                    }
                } else if (2 == productType) {//人工商品
                    try {
                        BudgetWorker budgetWorker = new BudgetWorker();
                        WorkerGoods workerGoods = iWorkerGoodsMapper.selectByPrimaryKey(productId);
                        if (workerGoods == null) {
                            continue;
                        }
                        budgetWorker.setHouseFlowId(houseFlowId);
                        budgetWorker.setHouseId(houseId);
                        budgetWorker.setWorkerTypeId(workerTypeId);
                        budgetWorker.setSteta(1);
                        budgetWorker.setDeleteState(0);
                        budgetWorker.setRepairCount(0.0);
                        budgetWorker.setBackCount(0.0);
                        budgetWorker.setWorkerGoodsId(workerGoods.getId().toString());
                        budgetWorker.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
                        budgetWorker.setName(workerGoods.getName());
                        budgetWorker.setPrice(workerGoods.getPrice());
                        budgetWorker.setImage(workerGoods.getImage());
                        budgetWorker.setShopCount(shopCount);
                        budgetWorker.setUnitName(workerGoods.getUnitName());
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

            ActuarialTemplate actuarialTemplate = iActuarialTemplateMapper.selectByPrimaryKey(actuarialTemplateId);
            if (actuarialTemplate != null) {
                actuarialTemplate.setNumberOfUse(actuarialTemplate.getNumberOfUse() + 1);
                iActuarialTemplateMapper.updateByPrimaryKeySelective(actuarialTemplate);
            }

            return ServerResponse.createBySuccessMessage("生成精算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }
    }

    /**
     * 根据houseId和wokerTypeId查询房子人工精算总价
     */
    public ServerResponse getWorkerTotalPrice(String houseId, String workerTypeId) {
        try {
            //JdbcContextHolder.putDataSource(DataSourceType.DJ_CHANGSHA.getName());
            Map<String, Object> map = iBudgetWorkerMapper.getWorkerTotalPrice(houseId, workerTypeId);
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }
    }

    //业主修改材料精算
    public ServerResponse doModifyBudgets(String listOfGoods) {
        try {
            JSONArray goodsList = JSONArray.parseArray(listOfGoods);
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject job = goodsList.getJSONObject(i);
                String id = job.getString("id");//精算id
                String goodsId = job.getString("goodsId");//商品id
                String productId = job.getString("productId");//货品id
                Integer productType = Integer.parseInt(job.getString("productType"));//0:材料；1：服务；2:人工
                String groupType = job.getString("groupType");//null：单品；有值：关联组合
                String goodsGroupId = job.getString("goodsGroupId");//所属关联组
                if (0 == productType || 1 == productType) {//材料或者服务
                    try {
                        BudgetMaterial budgetMaterial = iBudgetMaterialMapper.selectByPrimaryKey(id);
                        Goods goods = iGoodsMapper.queryById(goodsId);
                        if (goods == null) {
                            continue;
                        }
                        Product pro = iProductMapper.getById(productId);
                        if (pro == null) {
                            List<Product> pList = iProductMapper.queryByGoodsId(goods.getId());
                            if (pList.size() > 0) {
                                pro = pList.get(0);
                            }
                        }
                        if (goods.getBuy() == 0 || goods.getBuy() == 1) {//0：必买；1可选；2自购
                            budgetMaterial.setSteta(1);//我们购
                        } else {
                            budgetMaterial.setSteta(2);//自购
                        }
                        budgetMaterial.setProductId(pro.getId().toString());
                        budgetMaterial.setProductSn(pro.getProductSn());
                        budgetMaterial.setGoodsId(goodsId);
                        budgetMaterial.setGoodsName(goods.getName());
                        budgetMaterial.setProductName(pro.getName());
                        budgetMaterial.setUnitName(pro.getUnitName());
                        budgetMaterial.setPrice(pro.getPrice());
                        budgetMaterial.setCost(pro.getCost());
                        budgetMaterial.setUnitName(pro.getUnitName());
                        budgetMaterial.setModifyDate(new Date());
                        budgetMaterial.setProductType(productType);
                        budgetMaterial.setGroupType(groupType);
                        budgetMaterial.setGoodsGroupId(goodsGroupId);
                        budgetMaterial.setImage(pro.getImage());//货品图片
                        budgetMaterial.setCategoryId(goods.getCategoryId());//商品分类

                        /*Double actuarialQuantity = budgetMaterial.getActuarialQuantity();//精算量
                        Double convertQuality = pro.getConvertQuality();//换算单位量
                        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数
                        Double shopCount = Double.parseDouble(df.format(actuarialQuantity / convertQuality));//购买数量
                        budgetMaterial.setShopCount(shopCount);*/
                        Double totalPrice = budgetMaterial.getShopCount() * pro.getPrice();//购买总价
                        budgetMaterial.setTotalPrice(totalPrice);
                        iBudgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ServerResponse.createByErrorMessage("修改精算失败");
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("修改精算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改精算失败");
        }
    }

    /**
     * 估价
     * 已支付工种 hflist.get(i).getWorktype() == 4 查精算表里价格
     * 未支付查商品库价格
     *
     * @param houseId
     */
    public ServerResponse gatEstimateBudgetByHId(String houseId) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            BudgetResult budgetResult = new BudgetResult();
            budgetResult.setWorkerBudget(0.0);
            budgetResult.setMaterialBudget(0.0);
            House house = houseAPI.getHouseById(houseId);
            HouseFlow houseFlow = houseFlowAPI.getHouseFlowByHidAndWty(house.getId(), 2);
            if (houseFlow != null) {
                List<HouseFlow> hflist = houseFlowAPI.getFlowByhouseIdNot12(house.getId());
                RlistResult rlistResult = null;
                budgetResult.setBudgetDec(house.getCityName() + "/" + house.getResidential() + "/" + house.getSquare() + "m²");
                List<BudgetListResult> biglist = new ArrayList<BudgetListResult>();//人工list
                List<BudgetListResult> cailist = new ArrayList<BudgetListResult>();//材料list

                /*******************************人工费********************************/
                for (int i = 0; i < hflist.size(); i++) {
                    BudgetListResult blr = new BudgetListResult();
                    List<RlistResult> rlist = new ArrayList<RlistResult>();//算人力
                    WorkerType workType = new WorkerType();
                    ServerResponse serverResponse = workerTypeAPI.getWorkerType(hflist.get(i).getWorkerTypeId());
                    if (serverResponse.isSuccess()) {
                        workType = JSON.parseObject(serverResponse.getResultObj().toString(), WorkerType.class);
                    }
                    //二级人工费
                    blr.setListName(workType.getName() + "人工费用");
                    Double rgf = 0.00;//二级费用统计
                    Example example = new Example(BudgetWorker.class);
                    example.createCriteria().andEqualTo("houseFlowId", hflist.get(i).getId()).andCondition("delete_state!=1");
                    List<BudgetWorker> bwList = iBudgetWorkerMapper.selectByExample(example);
                    for (BudgetWorker abw : bwList) {//增加一层循环遍历存储下级子项目
                        WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
                        rlistResult = new RlistResult();
                        rlistResult.setRId(abw.getId());//id
                        //单价
                        rlistResult.setRCost(wg.getPrice() == null ? 0 : wg.getPrice());//单价
                        rlistResult.setRName(wg.getName());//名称
                        Double gjjg = abw.getShopCount() * wg.getPrice();
                        rlistResult.setSumRcost(gjjg);//合计价格
                        Double number = abw.getShopCount();
                        rlistResult.setNumber(number);//数量
                        rlist.add(rlistResult);
                        rgf += rlistResult.getSumRcost();
                        //总人工费
                        budgetResult.setWorkerBudget(budgetResult.getWorkerBudget() + rlistResult.getSumRcost());
                    }
                    //二级人工费
                    blr.setListCost(rgf.toString());
                    biglist.add(blr);
                }
                budgetResult.setBigList(biglist);

                /*************************材料费*************************************/
                hflist = houseFlowAPI.getFlowByhouseIdNot12(house.getId());
                for (int i = 0; i < hflist.size(); i++) {
                    List<RlistResult> rlist = new ArrayList<RlistResult>();//算材料
                    BudgetListResult blr = new BudgetListResult();
                    Double clf = 0.0;//二级费用统计
                    WorkerType workType = new WorkerType();
                    ServerResponse serverResponse = workerTypeAPI.getWorkerType(hflist.get(i).getWorkerTypeId());
                    if (serverResponse.isSuccess()) {
                        workType = JSON.parseObject(serverResponse.getResultObj().toString(), WorkerType.class);
                    }
                    blr.setListName(workType.getName() + "材料费");
                    Example example = new Example(BudgetMaterial.class);
                    example.createCriteria().andEqualTo("houseFlowId", hflist.get(i).getId()).andCondition("delete_state!=1");
                    List<BudgetMaterial> abmList = iBudgetMaterialMapper.selectByExample(example);//获取每个工序对应的材料表
                    for (BudgetMaterial abm : abmList) {//每个商品
                        Product product = iProductMapper.selectByPrimaryKey(abm.getProductId());
                        Goods goods = iGoodsMapper.selectByPrimaryKey(product.getGoodsId());
                        rlistResult = new RlistResult();
                        rlistResult.setRId(abm.getId());//id
                        if (hflist.get(i).getWorkType() == 4) {
                            Double cailiao = hflist.get(i).getMaterialPrice().doubleValue();//支付后
                            if (cailiao != null) {
                                rlistResult.setRCost(cailiao);
                            } else {
                                rlistResult.setRCost(iBudgetMaterialMapper.getAbmPayOutByHfId(hflist.get(i).getId()));
                            }
                        } else {
                            //没支付查实时价格
                            rlistResult.setRCost(iBudgetMaterialMapper.getAbmCasualByHfId(hflist.get(i).getId()));
                        }
                        //单价
                        if (product != null) {
                            rlistResult.setRName(product.getName());
                        } else {
                            rlistResult.setRName(goods.getName());
                        }
                        Double gjjg = (abm.getShopCount() * (abm.getPrice() == null ? product.getPrice() : abm.getPrice()));
                        rlistResult.setSumRcost(gjjg);//合计价格
                        rlistResult.setNumber(abm.getShopCount());//数量
                        rlist.add(rlistResult);
                        clf += rlistResult.getSumRcost();
                        //总材料费
                        budgetResult.setMaterialBudget(budgetResult.getMaterialBudget() + rlistResult.getSumRcost());
                    }

                    blr.setListCost(clf.toString());
                    //blr.setRlist(rlist);
                    cailist.add(blr);
                }
                List<BudgetListResult> expectList = new ArrayList<BudgetListResult>();//固定预计费list
                BudgetListResult blr1 = new BudgetListResult();//固定预计费
                BudgetListResult blr2 = new BudgetListResult();
                BudgetListResult blr3 = new BudgetListResult();
                blr1.setListCost("200");
                blr1.setListName("设计费");
                blr2.setListCost("300");
                blr2.setListName("精算费");
                blr3.setListCost("400");
                blr3.setListName("大管家费");
                expectList.add(blr1);
                expectList.add(blr2);
                expectList.add(blr3);
                budgetResult.setExpectList(expectList);//固定预计费
                budgetResult.setBigList(biglist);//人工list
                budgetResult.setCaiList(cailist);//材料list
                /****************************总项***************************/
                budgetResult.setCost(budgetResult.getMaterialBudget() + budgetResult.getWorkerBudget());//总价
                return ServerResponse.createBySuccess("查询估价成功", budgetResult);
            } else {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，查询估价失败");
        }
    }

    /**
     * 根据houseId查询所有
     * 已进场未完工工艺节点
     * 和所有材料工艺节点
     */
    public JSONArray getAllTechnologyByHouseId(String houseId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<HouseFlow> houseFlowList = technologyRecordAPI.unfinishedFlow(houseId);
            for (HouseFlow houseFlow : houseFlowList){
                List<BudgetWorker> budgetWorkerList = iBudgetWorkerMapper.getByHouseFlowId(houseFlow.getHouseId(),houseFlow.getId());
                for (BudgetWorker abw : budgetWorkerList) {
                    if(abw.getShopCount() + abw.getRepairCount() - abw.getBackCount() > 0){
                        WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
                        List<Technology> tList = iTechnologyMapper.patrolList(wg.getId());
                        for (Technology t : tList) {
                            JSONObject map = new JSONObject();
                            map.put("technologyId", t.getId());
                            map.put("technologyName", t.getName());
                            jsonArray.add(map);
                        }
                    }
                }
            }

            List<Warehouse> warehouseList = technologyRecordAPI.warehouseList(houseId);
            for (Warehouse warehouse : warehouseList) {//每个商品
                if(warehouse.getShopCount() - warehouse.getBackCount() > 0){
                    Product product = iProductMapper.selectByPrimaryKey(warehouse.getProductId());
                    List<Technology> tList = iTechnologyMapper.patrolList(product.getId());
                    for (Technology t : tList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("technologyId", t.getId());
                        map.put("technologyName", t.getName());
                        jsonArray.add(map);
                    }
                }
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
