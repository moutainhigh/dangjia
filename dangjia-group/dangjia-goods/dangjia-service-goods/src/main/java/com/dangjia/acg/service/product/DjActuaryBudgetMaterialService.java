package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.DjBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMaterialMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.product.DjBasicsProductMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/17
 * Time: 14:21
 */
@Service
public class DjActuaryBudgetMaterialService {
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    private static Logger LOG = LoggerFactory.getLogger(DjActuaryBudgetMaterialService.class);
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsProductMaterialMapper djBasicsProductMaterialMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IActuarialTemplateMapper iActuarialTemplateMapper;
    @Autowired
    private HouseAPI houseAPI;


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
            redisClient.deleteCache("HOUSEID-ACTUARY-" + houseId + "1");
            redisClient.deleteCache("HOUSEID-ACTUARY-" + houseId + "2");
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
                        DjBasicsGoods djBasicsGoods = djBasicsGoodsMapper.queryById(goodsId);
                        if (djBasicsGoods == null) {
                            continue;
                        }
                        budgetMaterial.setWorkerTypeId(workerTypeId);
                        budgetMaterial.setHouseFlowId(houseFlowId);
                        budgetMaterial.setHouseId(houseId);
                        budgetMaterial.setConvertCount(1d);
                        if (djBasicsGoods.getBuy() == 0 || djBasicsGoods.getBuy() == 1) {//0：必买；1可选；2自购
                            budgetMaterial.setSteta(1);//我们购

                            DjBasicsProduct djBasicsProduct = djBasicsProductMapper.getById(productId);
                            if (djBasicsProduct == null) {
                                List<DjBasicsProduct> pList = djBasicsProductMapper.queryByGoodsId(djBasicsGoods.getId());
                                if (pList.size() > 0) {
                                    djBasicsProduct = pList.get(0);
                                }
                            }
                            budgetMaterial.setProductId(djBasicsProduct.getId());
                            budgetMaterial.setProductSn(djBasicsProduct.getProductSn());
                            budgetMaterial.setProductName(djBasicsProduct.getName());
                            budgetMaterial.setPrice(djBasicsProduct.getPrice());
                            Example example = new Example(DjBasicsProductMaterial.class);
                            example.createCriteria().andEqualTo(DjBasicsProductMaterial.DATA_STATUS, 0)
                                    .andEqualTo(DjBasicsProductMaterial.PRODUCT_ID, djBasicsProduct.getId());
                            List<DjBasicsProductMaterial> djBasicsProductMaterials = djBasicsProductMaterialMapper.selectByExample(example);
                            budgetMaterial.setCost(djBasicsProductMaterials.size() > 0 ? djBasicsProductMaterials.get(0).getCost() : null);
                            budgetMaterial.setImage(djBasicsProduct.getImage());//货品图片
                           /* double a = actuarialQuantity / pro.getConvertQuality();
                            double shopCount = Math.ceil(a);*/
                            budgetMaterial.setShopCount(shopCount);
                            Double converCount = (shopCount / djBasicsProductMaterials.get(0).getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(djBasicsProductMaterials.get(0).getConvertUnit());
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
                            Unit unit = iUnitMapper.selectByPrimaryKey(djBasicsGoods.getUnitId());
                            if (unit != null)
                                budgetMaterial.setUnitName(unit.getName());
                        }
                        budgetMaterial.setDeleteState(0);
                        budgetMaterial.setGoodsId(goodsId);
                        budgetMaterial.setGoodsName(djBasicsGoods.getName());
                        budgetMaterial.setCategoryId(djBasicsGoods.getCategoryId());//商品分类
                        // budgetMaterial.setActuarialQuantity(actuarialQuantity);
                        budgetMaterial.setCreateDate(new Date());
                        budgetMaterial.setModifyDate(new Date());
                        budgetMaterial.setProductType(djBasicsGoods.getType());
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
                        example1.createCriteria().andEqualTo(BudgetWorker.HOUSE_ID, houseId)
                                .andEqualTo(BudgetWorker.WORKER_GOODS_ID, productId)
                                .andEqualTo(BudgetWorker.WORKER_TYPE_ID, workerTypeId);
                        int num = iBudgetWorkerMapper.selectCountByExample(example1);
                        if (num > 0) {
                            continue;
                        }
                        BudgetWorker budgetWorker = new BudgetWorker();
                        DjBasicsProduct workerGoods = djBasicsProductMapper.selectByPrimaryKey(productId);
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
                        budgetWorker.setWorkerGoodsSn(workerGoods.getProductSn());
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

            houseAPI.updateCustomEdit(houseId);
            return ServerResponse.createBySuccessMessage("生成精算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }
    }


}
