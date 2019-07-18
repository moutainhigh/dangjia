package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.core.HouseFlowAPI;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ImportExcel;
import com.dangjia.acg.dto.basics.*;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.service.basics.ProductService;
import com.dangjia.acg.service.basics.WorkerGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

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
    private IUnitMapper iUnitMapper;
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
    @Autowired
    private WorkerGoodsService workerGoodsService;
    @Autowired
    private ProductService productService;

    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;

    private static Logger LOG = LoggerFactory.getLogger(BudgetWorkerService.class);

    //根据HouseFlowId查询房子材料精算
    public ServerResponse queryBudgetWorkerByHouseFlowId(String houseFlowId) {
        try {
            Example example = new Example(BudgetWorker.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlowId).andEqualTo("deleteState", 0);
            List<BudgetWorker> budgetMaterialist = iBudgetWorkerMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", budgetMaterialist);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询所有精算
    public ServerResponse getAllBudgetWorker() {
        try {
            List<Map<String, Object>> mapList = iBudgetWorkerMapper.getBudgetWorker();
            setWorkerTypeName(mapList);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    private void setWorkerTypeName(List<Map<String, Object>> mapList) {
        for (Map<String, Object> obj : mapList) {
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(obj.get("workerTypeId").toString());
            if (response.isSuccess()) {
                //二级人工费
                workerTypeName = ((JSONObject) response.getResultObj()).getString("name");
            }
            obj.put("workerTypeName", workerTypeName);
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
        LOG.info("listOfGoods :" + listOfGoods + " workerTypeId:" + workerTypeId + " templateId:" + templateId);
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
            if (jobT.getProductType() != 2) {//材料或者包工包料
                try {
                    BudgetMaterial budgetMaterial = new BudgetMaterial();
                    budgetMaterial.setConvertCount(1d);
                    Goods goods = iGoodsMapper.queryById(jobT.getGoodsId());
                    if (goods == null) {
                        continue;
                    }
                    if (goods.getBuy() == 2 && !StringUtils.isNoneBlank(jobT.getProductId()))//2自购
                    {
                        budgetMaterial.setProductId("");
                        budgetMaterial.setProductSn("");
                        budgetMaterial.setProductName("");
                        budgetMaterial.setPrice(0.0);
                        budgetMaterial.setCost(0.0);
                        budgetMaterial.setImage("");//货品图片
//                        budgetMaterial.setShopCount(0.0);
                        budgetMaterial.setShopCount(jobT.getShopCount());
                        Unit unit = iUnitMapper.selectByPrimaryKey(goods.getUnitId());
                        if (unit != null)
                            budgetMaterial.setUnitName(unit.getName());
                        budgetMaterial.setTotalPrice(0.0);
                    } else {
                        Product pro = iProductMapper.getById(jobT.getProductId());
                        if (pro == null) {
                            continue;
                        }
                        budgetMaterial.setProductId(pro.getId());
                        budgetMaterial.setProductSn(pro.getProductSn());
                        budgetMaterial.setProductName(pro.getName());
                        budgetMaterial.setPrice(pro.getPrice());
                        budgetMaterial.setCost(pro.getCost());
                        budgetMaterial.setImage(pro.getImage());//货品图片
                       /* double a = jobT.getActuarialQuantity() / pro.getConvertQuality();
                        double shopCount = Math.ceil(a);*/
                        budgetMaterial.setShopCount(jobT.getShopCount());
                        Double converCount = (jobT.getShopCount() / pro.getConvertQuality());
                        Unit convertUnit = iUnitMapper.selectByPrimaryKey(pro.getConvertUnit());
                        if (convertUnit.getType() == 1) {
                            converCount = Math.ceil(converCount);
                        }
                        budgetMaterial.setConvertCount(converCount);
//                        budgetMaterial.setUnitName(pro.getUnitName());
                        budgetMaterial.setUnitName(convertUnit.getName());
                        BigDecimal b1 = new BigDecimal(Double.toString(pro.getPrice()));
//                        BigDecimal b2 = new BigDecimal(Double.toString(budgetMaterial.getConvertCount()));
//                        BigDecimal b2 = new BigDecimal(Double.toString(Math.ceil(budgetMaterial.getConvertCount())));
                        BigDecimal b2 = new BigDecimal(Double.toString(budgetMaterial.getConvertCount()));
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
        return ServerResponse.createBySuccessMessage("保存精算成功");
    }

    //查询该风格下的精算模板
    public ServerResponse getAllbudgetTemplates(String templateId) {
        try {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> wokerList = iBudgetWorkerMapper.getAllbudgetTemplates(templateId);
            setWorkerTypeName(wokerList);
            map.put("wokerList", wokerList);//人工精算
            List<Map<String, Object>> materialList = iBudgetMaterialMapper.getAllbudgetTemplates(templateId);
//            setGoods(materialList, iGoodsMapper, iProductMapper, iUnitMapper);
            setGoods(materialList);

            for (Map<String, Object> obj : materialList) {
                String goodsId = obj.get("goodsId").toString();
                Goods goods = iGoodsMapper.queryById(goodsId);
                Unit unit = iUnitMapper.selectByPrimaryKey(goods.getUnitId());
                if (unit != null)
                    obj.put("goodsUnitName", unit.getName());
            }

            map.put("materialList", materialList);//材料精算
            return ServerResponse.createBySuccess("查询精算成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询精算失败");
        }
    }

    public void setGoods(List<Map<String, Object>> materialList) {
//        , IGoodsMapper iGoodsMapper, IProductMapper iProductMapper, IUnitMapper iUnitMapper
        for (Map<String, Object> obj : materialList) {
            String goodsId = obj.get("goodsId").toString();
//            Goods goods = iGoodsMapper.queryById(goodsId);
            Goods goods = iGoodsMapper.selectByPrimaryKey(goodsId);
            obj.put("goodsBuy", goods.getBuy());
            if (goods.getBuy() == 0 || goods.getBuy() == 1) {//0：必买；1可选；2自购
//                    budgetMaterial.setSteta(1);//我们购
                String productId = obj.get("productId").toString();
                Product pro = iProductMapper.selectByPrimaryKey(productId);
                Unit unit = iUnitMapper.selectByPrimaryKey(pro.getConvertUnit());
                obj.put("convertUnitName", unit.getName());
            } else {//自购
                Unit unit = iUnitMapper.selectByPrimaryKey(goods.getUnitId());
                if (unit != null)
                    obj.put("goodsUnitName", unit.getName());
            }
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
     * 生成精算
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
                Integer productType = Integer.parseInt(job.getString("productType"));//0:材料；1：包工包料；2:人工
                String groupType = job.getString("groupType");//null：单品；有值：关联组合
                String goodsGroupId = job.getString("goodsGroupId");//所属关联组
                Double shopCount = Double.parseDouble(job.getString("shopCount"));//数量
                if (0 == productType || 1 == productType) {//材料或者包工包料
                    Example example1 = new Example(BudgetMaterial.class);
                    example1.createCriteria().andEqualTo(BudgetMaterial.HOUSE_ID, houseId).andEqualTo(BudgetMaterial.PRODUCT_ID, productId).andEqualTo(BudgetMaterial.WORKER_TYPE_ID, workerTypeId);
                    int num = iBudgetMaterialMapper.selectCountByExample(example1);
                    if (num > 0) {
                        continue;
                    }
                    try {
                        BudgetMaterial budgetMaterial = new BudgetMaterial();
                        Goods goods = iGoodsMapper.queryById(goodsId);
                        if (goods == null) {
                            continue;
                        }
                        budgetMaterial.setWorkerTypeId(workerTypeId);
                        budgetMaterial.setHouseFlowId(houseFlowId);
                        budgetMaterial.setHouseId(houseId);
                        budgetMaterial.setConvertCount(1d);
                        if (goods.getBuy() == 0 || goods.getBuy() == 1) {//0：必买；1可选；2自购
                            budgetMaterial.setSteta(1);//我们购

                            Product pro = iProductMapper.getById(productId);
                            if (pro == null) {
                                List<Product> pList = iProductMapper.queryByGoodsId(goods.getId());
                                if (pList.size() > 0) {
                                    pro = pList.get(0);
                                }
                            }
                            budgetMaterial.setProductId(pro.getId());
                            budgetMaterial.setProductSn(pro.getProductSn());
                            budgetMaterial.setProductName(pro.getName());
                            budgetMaterial.setPrice(pro.getPrice());
                            budgetMaterial.setCost(pro.getCost());
                            budgetMaterial.setImage(pro.getImage());//货品图片
                           /* double a = actuarialQuantity / pro.getConvertQuality();
                            double shopCount = Math.ceil(a);*/
                            budgetMaterial.setShopCount(shopCount);
                            Double converCount = (shopCount / pro.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(pro.getConvertUnit());
                            if (convertUnit.getType() == 1) {
                                converCount = Math.ceil(converCount);
                            }
                            budgetMaterial.setConvertCount(converCount);
                            BigDecimal b1 = new BigDecimal(budgetMaterial.getPrice());
//                            BigDecimal b2 = new BigDecimal(Double.toString(shopCount));
//                            BigDecimal b2 = new BigDecimal(Double.toString(budgetMaterial.getConvertCount()));
                            BigDecimal b2 = new BigDecimal(budgetMaterial.getConvertCount());
                            Double totalPrice = b1.multiply(b2).doubleValue();
                            budgetMaterial.setTotalPrice(totalPrice);
//                            budgetMaterial.setUnitName(pro.getUnitName());
                            budgetMaterial.setUnitName(convertUnit.getName());
                        } else {
                            budgetMaterial.setSteta(2);//自购
                            budgetMaterial.setProductId("");
                            budgetMaterial.setProductSn("");
                            budgetMaterial.setProductName("");
                            budgetMaterial.setPrice(0.0);
                            budgetMaterial.setCost(0.0);
                            budgetMaterial.setImage("");//货品图片
                            budgetMaterial.setShopCount(shopCount);
                            budgetMaterial.setTotalPrice(0.0);
                            Unit unit = iUnitMapper.selectByPrimaryKey(goods.getUnitId());
                            if (unit != null)
                                budgetMaterial.setUnitName(unit.getName());
                        }

                        budgetMaterial.setDeleteState(0);
                        budgetMaterial.setGoodsId(goodsId);
                        budgetMaterial.setGoodsName(goods.getName());
                        budgetMaterial.setCategoryId(goods.getCategoryId());//商品分类
                        // budgetMaterial.setActuarialQuantity(actuarialQuantity);
                        budgetMaterial.setCreateDate(new Date());
                        budgetMaterial.setModifyDate(new Date());
                        budgetMaterial.setProductType(goods.getType());
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
                        Example example1 = new Example(BudgetWorker.class);
                        example1.createCriteria().andEqualTo(BudgetWorker.HOUSE_ID, houseId).andEqualTo(BudgetWorker.WORKER_GOODS_ID, productId).andEqualTo(BudgetWorker.WORKER_TYPE_ID, workerTypeId);
                        int num = iBudgetWorkerMapper.selectCountByExample(example1);
                        if (num > 0) {
                            continue;
                        }
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
                        budgetWorker.setWorkerGoodsId(workerGoods.getId());
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
     * 生成精算（xls导入）
     */
    public ServerResponse importExcelBudgets(String workerTypeId, MultipartFile file) {
        try {
            Map map = new HashMap();
            ImportExcel caiLiao = new ImportExcel(file, 0, 1);//材料
            List<ProductDTO> caiLiaoList = caiLiao.getDataList(ProductDTO.class, 0);
            List<ProductDTO> caiLiaos = new ArrayList<>();
            for (int i = 0; i < caiLiaoList.size(); i++) {
                ProductDTO productDTO = caiLiaoList.get(i);
                if (CommonUtil.isEmpty(productDTO.getProductSn())) {
                    break;
                }
                productDTO = productService.getProductDTO(productDTO.getProductSn(), productDTO.getShopCount());
                if (CommonUtil.isEmpty(productDTO.getShopCount())) {
                    continue;
                }
                caiLiaos.add(productDTO);
            }
            map.put("caiLiaos", caiLiaos);


            ImportExcel fuWu = new ImportExcel(file, 0, 2);//包工包料
            List<ProductDTO> fuWuList = fuWu.getDataList(ProductDTO.class, 0);
            List<ProductDTO> fuWus = new ArrayList<>();
            for (int i = 0; i < fuWuList.size(); i++) {
                ProductDTO productDTO = fuWuList.get(i);
                if (CommonUtil.isEmpty(productDTO.getProductSn())) {
                    break;
                }
                productDTO = productService.getProductDTO(productDTO.getProductSn(), productDTO.getShopCount());
                if (CommonUtil.isEmpty(productDTO.getShopCount())) {
                    continue;
                }
                fuWus.add(productDTO);
            }
            map.put("fuWus", fuWus);


            ImportExcel renGong = new ImportExcel(file, 0, 0);//人工
            List<WorkerGoodsDTO> workerGoodsDTOList = renGong.getDataList(WorkerGoodsDTO.class, 0);
            List<WorkerGoodsDTO> workerGoodsDTOS = new ArrayList<>();
            for (int i = 0; i < workerGoodsDTOList.size(); i++) {
                WorkerGoodsDTO workerGoodsDTO = workerGoodsDTOList.get(i);
                if (CommonUtil.isEmpty(workerGoodsDTO.getWorkerGoodsSn())) {
                    break;
                }
                workerGoodsDTO = workerGoodsService.getWorkerGoodsDTO(workerGoodsDTO.getWorkerGoodsSn(), workerTypeId, workerGoodsDTO.getShopCount());
                if (CommonUtil.isEmpty(workerGoodsDTO.getShopCount())) {
                    continue;
                }
                workerGoodsDTOS.add(workerGoodsDTO);
            }
            map.put("workerGoods", workerGoodsDTOS);
            return ServerResponse.createBySuccess("生成精算成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }
    }

    /**
     * 根据houseId和workerTypeId查询房子人工精算总价
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

    /**
     * 估价
     * 已支付工种 hflist.get(i).getWorktype() == 4 查精算表里价格
     * 未支付查商品库价格
     */
    public ServerResponse gatEstimateBudgetByHId(String houseId) {
        try {
            BudgetResult budgetResult = new BudgetResult();
            budgetResult.setWorkerBudget(0.0);
            budgetResult.setMaterialBudget(0.0);
            House house = houseAPI.getHouseById(houseId);
            List<HouseFlow> hflist = houseFlowAPI.getWorkerFlow(house.getId());
            RlistResult rlistResult;
            budgetResult.setBudgetDec(house.getCityName() + "/" + house.getResidential() + "/" + house.getBuildSquare() + "m²");
            List<BudgetListResult> biglist = new ArrayList<>();//人工list
            List<BudgetListResult> cailist = new ArrayList<>();//材料list

            /*******************************人工费********************************/
            for (HouseFlow aHflist1 : hflist) {
                BudgetListResult blr = new BudgetListResult();
                ServerResponse response = workerTypeAPI.getWorkerType(aHflist1.getWorkerTypeId());
                if (response.isSuccess()) {
                    //二级人工费
                    blr.setListName((((JSONObject) response.getResultObj()).getString("name")) + "人工费用");
                }
                Double rgf = 0.00;//二级费用统计
                Example example = new Example(BudgetWorker.class);
                example.createCriteria().andEqualTo(BudgetWorker.HOUSE_FLOW_ID, aHflist1.getId()).andEqualTo(BudgetWorker.STETA, 1)
                        .andCondition("delete_state!=1");
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
                    rgf += rlistResult.getSumRcost();
                    //总人工费
                    budgetResult.setWorkerBudget(budgetResult.getWorkerBudget() + rlistResult.getSumRcost());
                }
                //二级人工费
                blr.setListCost(new BigDecimal(rgf));
                biglist.add(blr);
            }
            budgetResult.setBigList(biglist);
            /*************************材料费*************************************/
            for (HouseFlow aHflist : hflist) {
                BudgetListResult blr = new BudgetListResult();
                Double clf = 0.0;//二级费用统计
                ServerResponse response = workerTypeAPI.getWorkerType(aHflist.getWorkerTypeId());
                if (response.isSuccess()) {
                    //二级人工费
                    blr.setListName((((JSONObject) response.getResultObj()).getString("name")) + "材料费");
                }
                Example example = new Example(BudgetMaterial.class);
                example.createCriteria().andEqualTo(BudgetMaterial.HOUSE_FLOW_ID, aHflist.getId()).andEqualTo(BudgetMaterial.STETA, 1)
                        .andCondition("delete_state!=1");
                List<BudgetMaterial> abmList = iBudgetMaterialMapper.selectByExample(example);//获取每个工序对应的材料表
                for (BudgetMaterial abm : abmList) {//每个商品
                    Product product = iProductMapper.selectByPrimaryKey(abm.getProductId());
                    Goods goods = iGoodsMapper.selectByPrimaryKey(abm.getGoodsId());
                    rlistResult = new RlistResult();
                    rlistResult.setRId(abm.getId());//id
                    if (aHflist.getWorkType() == 4) {
                        Double cailiao = aHflist.getMaterialPrice().doubleValue();//支付后
                        rlistResult.setRCost(cailiao);
                    } else {
                        //没支付查实时价格
                        rlistResult.setRCost(iBudgetMaterialMapper.getAbmCasualByHfId(aHflist.getId()));
                    }
                    double price = 0;
                    //单价
                    if (product != null) {
                        rlistResult.setRName(product.getName());
                        price = product.getPrice() == null ? 0 : product.getPrice();
                    } else {
                        rlistResult.setRName(goods.getName());
                    }
                    double gjjg = (abm.getConvertCount() * (abm.getPrice() == null ? price : abm.getPrice()));
                    rlistResult.setSumRcost(gjjg);//合计价格
                    rlistResult.setNumber(abm.getShopCount());//数量
                    clf += rlistResult.getSumRcost();
                    //总材料费
                    budgetResult.setMaterialBudget(budgetResult.getMaterialBudget() + rlistResult.getSumRcost());
                }

                blr.setListCost(new BigDecimal(clf));
                cailist.add(blr);
            }

            List<BudgetListResult> expectList = new ArrayList<>();//固定预计费list
            BudgetListResult blr1 = new BudgetListResult();//固定预计费
            BudgetListResult blr2 = new BudgetListResult();
            BudgetListResult blr3 = new BudgetListResult();

            HouseStyleType houseStyleType = getForBudgetAPI.getStyleByName(house.getStyle());
            if (houseStyleType != null && houseStyleType.getPrice() != null) {
                blr1.setListCost(house.getSquare().multiply(houseStyleType.getPrice()));
            } else {
                blr1.setListCost(new BigDecimal(0));
            }
            blr1.setListName("设计费");

            HouseFlow houseFlow = houseFlowAPI.getHouseFlowByHidAndWty(houseId, 2);
            if (houseFlow != null && houseFlow.getWorkPrice() != null) {
                blr2.setListCost(houseFlow.getWorkPrice());
            } else {
                blr2.setListCost(new BigDecimal(0));
            }
            blr2.setListName("精算费");

            houseFlow = houseFlowAPI.getHouseFlowByHidAndWty(houseId, 3);
            if (houseFlow != null && houseFlow.getTotalPrice() != null) {
                blr3.setListCost(houseFlow.getTotalPrice());
            } else {
                blr3.setListCost(new BigDecimal(0));
            }
            blr3.setListName("管家费");
            expectList.add(blr1);
            expectList.add(blr2);
            expectList.add(blr3);
            budgetResult.setExpectList(expectList);//固定预计费
            budgetResult.setBigList(biglist);//人工list
            budgetResult.setCaiList(cailist);//材料list
            /****************************总项***************************/
            budgetResult.setCost(budgetResult.getMaterialBudget() + budgetResult.getWorkerBudget());//总价
            return ServerResponse.createBySuccess("查询估价成功", budgetResult);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("系统出错，查询估价失败", e);
            return ServerResponse.createByErrorMessage("系统出错，查询估价失败");
        }
    }

    /**
     * 工种施工节点
     */
    public JSONArray getTecByHouseFlowId(String houseId, String houseFlowId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<BudgetWorker> budgetWorkerList = iBudgetWorkerMapper.getByHouseFlowId(houseId, houseFlowId);
            for (BudgetWorker abw : budgetWorkerList) {
                if (abw.getShopCount() + abw.getRepairCount() - abw.getBackCount() > 0) {
                    WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
                    List<Technology> tList = iTechnologyMapper.patrolList(wg.getTechnologyIds());
                    for (Technology t : tList) {
                        JSONObject map = new JSONObject();
                        map.put("technologyName", t.getName());
                        map.put("content", t.getContent());
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

    /**
     * 根据houseId查询所有
     * 已进场未完工工艺节点
     * 和所有材料工艺节点
     */
    public JSONArray getAllTechnologyByHouseId(String houseId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<HouseFlow> houseFlowList = technologyRecordAPI.unfinishedFlow(houseId);
            for (HouseFlow houseFlow : houseFlowList) {
                List<BudgetWorker> budgetWorkerList = iBudgetWorkerMapper.getByHouseFlowId(houseFlow.getHouseId(), houseFlow.getId());
                for (BudgetWorker abw : budgetWorkerList) {
                    if (abw.getShopCount() + abw.getRepairCount() - abw.getBackCount() > 0) {
                        WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());
                        List<Technology> tList = iTechnologyMapper.patrolList(wg.getTechnologyIds());
                        if (tList.size() > 0) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("workerGoodsId", wg.getId());
                            jsonObject.put("workerGoodsName", wg.getName());
                            jsonArray.add(jsonObject);
                        }
                    }
                }
            }

            List<Warehouse> warehouseList = technologyRecordAPI.warehouseList(houseId);
            for (Warehouse warehouse : warehouseList) {//每个商品
                if (warehouse.getShopCount() - warehouse.getBackCount() > 0) {
                    Product product = iProductMapper.selectByPrimaryKey(warehouse.getProductId());
                    if (product == null) continue;
                    List<Technology> tList = iTechnologyMapper.patrolList(product.getId());
                    if (tList.size() > 0) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("workerGoodsId", product.getId());
                        jsonObject.put("workerGoodsName", product.getName());
                        jsonArray.add(jsonObject);
                    }
                }
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 精算查询包含工艺的人工商品
     */
    public JSONArray getWorkerGoodsList(String houseId, String houseFlowId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<BudgetWorker> budgetWorkerList = iBudgetWorkerMapper.getByHouseFlowId(houseId, houseFlowId);
            for (BudgetWorker abw : budgetWorkerList) {
                if (abw.getShopCount() + abw.getRepairCount() - abw.getBackCount() > 0) {
                    WorkerGoods  workerGoods = workerGoodsMapper.selectByPrimaryKey(abw.getWorkerGoodsId());//人工商品
                    if (!CommonUtil.isEmpty(workerGoods)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("workerGoodsId", abw.getWorkerGoodsId());
                        jsonObject.put("workerGoodsName", abw.getName());
                        jsonArray.add(jsonObject);
                    }
                }
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据人工商品查询工艺
     */
    public JSONArray getTecList(int workerType, String workerGoodsId) {
        try {
            JSONArray jsonArray = new JSONArray();
            WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(workerGoodsId);
            List<Technology> tList;
            if (workerType == 3) {
                tList = iTechnologyMapper.patrolList(wg.getTechnologyIds());
            } else {
                tList = iTechnologyMapper.workerPatrolList(wg.getTechnologyIds());
            }
            for (Technology t : tList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("technologyId", t.getId());
                jsonObject.put("technologyName", t.getName());
                jsonArray.add(jsonObject);
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean workerPatrolList(String workerGoodsId) {
        WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(workerGoodsId);
        List<Technology> tList = iTechnologyMapper.workerPatrolList(wg.getTechnologyIds());
        if (tList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean patrolList(String workerGoodsId) {
        WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(workerGoodsId);
        List<Technology> tList = iTechnologyMapper.patrolList(wg.getTechnologyIds());
        if (tList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
