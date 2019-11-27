package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.product.BasicsGoodArrDTO;
import com.dangjia.acg.dto.product.BasicsGoodDTO;
import com.dangjia.acg.dto.product.BasicsgDTO;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.DjBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsCategoryMapper;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.actuary.app.SearchActuarialConfigServices;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private static Logger LOG = LoggerFactory.getLogger(DjActuaryBudgetMaterialService.class);
    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private IBasicsGoodsCategoryMapper djBasicsGoodsCategoryMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
    @Autowired
    private IActuarialTemplateMapper iActuarialTemplateMapper;
    @Autowired
    private HouseAPI houseAPI;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;

    @Autowired
    private SearchActuarialConfigServices searchActuarialConfigServices;
    /**
     * 生成精算
     */
    public ServerResponse makeBudgets(String actuarialTemplateId, String houseId,
                                      String workerTypeId, String listOfGoods,String cityId) {
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

                            DjBasicsProductTemplate djBasicsProduct = iBasicsProductTemplateMapper.getById(productId);
                            if (djBasicsProduct == null) {
                                List<DjBasicsProductTemplate> pList = iBasicsProductTemplateMapper.queryByGoodsId(djBasicsGoods.getId());
                                if (pList.size() > 0) {
                                    djBasicsProduct = pList.get(0);
                                }
                            }
                            budgetMaterial.setProductId(djBasicsProduct.getId());
                            budgetMaterial.setProductSn(djBasicsProduct.getProductSn());
                            budgetMaterial.setProductName(djBasicsProduct.getName());
                            budgetMaterial.setPrice(djBasicsProduct.getPrice());
                            budgetMaterial.setCost( djBasicsProduct.getCost() );
                            budgetMaterial.setImage(djBasicsProduct.getImage());//货品图片
                           /* double a = actuarialQuantity / pro.getConvertQuality();
                            double shopCount = Math.ceil(a);*/
                            budgetMaterial.setShopCount(shopCount);
                            Double converCount = (shopCount / djBasicsProduct.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(djBasicsProduct.getConvertUnit());
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
                        budgetMaterial.setCityId(cityId);
//                        budgetMaterial.setTemplateId(actuarialTemplateId);
                        iBudgetMaterialMapper.insert(budgetMaterial);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ServerResponse.createByErrorMessage("生成失败");
                    }
                } else if (2 == productType) {//人工商品
                    try {
                        Example example1 = new Example(BudgetMaterial.class);
                        example1.createCriteria().andEqualTo(BudgetMaterial.HOUSE_ID, houseId)
                                .andEqualTo(BudgetMaterial.PRODUCT_ID, productId)
                                .andEqualTo(BudgetMaterial.WORKER_TYPE_ID, workerTypeId);
                        int num = iBudgetWorkerMapper.selectCountByExample(example1);
                        if (num > 0) {
                            continue;
                        }
                        BudgetMaterial budgetWorker = new BudgetMaterial();
                        DjBasicsProductTemplate workerGoods = iBasicsProductTemplateMapper.selectByPrimaryKey(productId);
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
                        budgetWorker.setProductId(workerGoods.getId());
                        budgetWorker.setProductSn(workerGoods.getProductSn());
                        budgetWorker.setProductName(workerGoods.getName());
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
                        budgetWorker.setCityId(cityId);
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
                actuarialTemplate.setCityId(cityId);
                iActuarialTemplateMapper.updateByPrimaryKeySelective(actuarialTemplate);
            }

            houseAPI.updateCustomEdit(houseId);
            return ServerResponse.createBySuccessMessage("生成精算成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }
    }


    /**
     * 查询精算列表
     * @param bclId
     * @param categoryId
     * @param houseId
     * @return
     */
    public ServerResponse queryMakeBudgetsList(String bclId, String categoryId, String houseId,String cityId) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        BasicsGoodArrDTO basicsGoodArrDTO = new BasicsGoodArrDTO();
        Example example = new Example(DjBasicsGoods.class);
        example.createCriteria().andEqualTo(DjBasicsGoods.CATEGORY_ID, categoryId).andEqualTo(DjBasicsGoods.CITY_ID,cityId);
        List<DjBasicsGoods> list = djBasicsGoodsMapper.selectByExample(example);
        BasicsGoodsCategory djBasicsGoodsCategory = djBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (list.size() > 0) {
                //0：材料；1：服务   //2 人工
                List<BasicsGoodDTO> bgdList = new ArrayList<>();
                if(!CommonUtil.isEmpty(djBasicsGoodsCategory)){
                    example = new Example(BasicsGoodsCategory.class);
                    example.createCriteria().andEqualTo(BasicsGoodsCategory.PARENT_ID, djBasicsGoodsCategory.getParentId()).andEqualTo(BasicsGoodsCategory.CITY_ID,cityId);
                    List<BasicsGoodsCategory> li = djBasicsGoodsCategoryMapper.selectByExample(example);
                    if (!li.isEmpty()) {
                        for (BasicsGoodsCategory bgc : li) {
                            BasicsGoodDTO basicsGoodDTO = new BasicsGoodDTO();
                            List<BasicsgDTO> bList = iBudgetWorkerMapper.queryMakeBudgetsBmList(houseId, bgc.getId());
                            for (BasicsgDTO basicsgDTO : bList) {
                                basicsgDTO.setImage(imageAddress + basicsgDTO.getImage());
                                if (basicsgDTO.getBuy() == 2) {
                                    basicsgDTO.setBuyStr("自购商品需自行购买");
                                } else {
                                    basicsgDTO.setBuyStr("");
                                }
                            }
                            Double priceArr = bList.stream().filter(a -> a.getPrice()!=null).mapToDouble(BasicsgDTO::getPrice).sum();
                            basicsGoodDTO.setPriceArr(priceArr);
                            basicsGoodDTO.setList(bList);
                            basicsGoodDTO.setName(bgc.getName());
                            bgdList.add(basicsGoodDTO);
                        }
                    }
                    Double priceArr = bgdList.stream().filter(a -> a.getPriceArr()!=null).mapToDouble(BasicsGoodDTO::getPriceArr).sum();
                    basicsGoodArrDTO.setPriceArr(priceArr);
                    basicsGoodArrDTO.setList(bgdList);
                }
                return ServerResponse.createBySuccess("查询成功", basicsGoodArrDTO);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }


    /**
     * 查询精算列表
     * @param bclId
     * @param categoryId
     * @param houseId
     * @return
     */
    public ServerResponse queryMakeBudgetsList2(String bclId, String categoryId, String houseId,String cityId) {

        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
//        String imageAddress ="";
        BasicsGoodArrDTO basicsGoodArrDTO = new BasicsGoodArrDTO();
        Example example = new Example(DjBasicsGoods.class);
                example.createCriteria()
                .andEqualTo(DjBasicsGoods.CATEGORY_ID, categoryId)
                .andEqualTo(DjBasicsGoods.CITY_ID,cityId);
        List<DjBasicsGoods> list = djBasicsGoodsMapper.selectByExample(example);

        BasicsGoodsCategory djBasicsGoodsCategory = djBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (list.size() > 0) {
            int i = list.get(0).getType();
            if (i == 2) {
                //2 人工
                List<BasicsGoodDTO> bgdList = new ArrayList<>();
                if(!CommonUtil.isEmpty(djBasicsGoodsCategory)){
                    example = new Example(BasicsGoodsCategory.class);
                    example.createCriteria().andEqualTo(BasicsGoodsCategory.PARENT_ID,
                            djBasicsGoodsCategory.getParentId());
                    List<BasicsGoodsCategory> li = djBasicsGoodsCategoryMapper.selectByExample(example);
                    if (!li.isEmpty()) {
                        for (BasicsGoodsCategory bgc : li) {
                            BasicsGoodDTO basicsGoodDTO = new BasicsGoodDTO();
                            List<BasicsgDTO> bList = iBudgetWorkerMapper.queryMakeBudgetsList(houseId, bgc.getId());
                            for (BasicsgDTO basicsgDTO : bList) {
                                basicsgDTO.setImage(imageAddress + basicsgDTO.getImage());
                                if (basicsgDTO.getBuy() == 2) {
                                    basicsgDTO.setBuyStr("自购商品需自行购买 ");
                                } else {
                                    basicsgDTO.setBuyStr("");
                                }
                            }
                            Double priceArr = bList.stream().filter(a -> a.getPrice()!=null).mapToDouble(BasicsgDTO::getPrice).sum();
                            basicsGoodDTO.setPriceArr(priceArr);
                            basicsGoodDTO.setList(bList);
                            basicsGoodDTO.setName(bgc.getName());
                            bgdList.add(basicsGoodDTO);
                        }
                    }
                    Double priceArr = bgdList.stream().filter(a -> a.getPriceArr()!=null).mapToDouble(BasicsGoodDTO::getPriceArr).sum();
                    basicsGoodArrDTO.setPriceArr(priceArr);
                    basicsGoodArrDTO.setList(bgdList);
                }
                return ServerResponse.createBySuccess("查询成功", basicsGoodArrDTO);
            } else if (i == 0 || i == 1) {
                //0：材料；1：服务
                List<BasicsGoodDTO> bgdList = new ArrayList<>();
                if(!CommonUtil.isEmpty(djBasicsGoodsCategory)){
                    example = new Example(BasicsGoodsCategory.class);
                    example.createCriteria().andEqualTo(BasicsGoodsCategory.PARENT_ID,
                            djBasicsGoodsCategory.getParentId()).andEqualTo(BasicsGoodsCategory.CITY_ID,cityId);
                    List<BasicsGoodsCategory> li = djBasicsGoodsCategoryMapper.selectByExample(example);
                    if (!li.isEmpty()) {
                        for (BasicsGoodsCategory bgc : li) {
                            BasicsGoodDTO basicsGoodDTO = new BasicsGoodDTO();
                            List<BasicsgDTO> bList = iBudgetWorkerMapper.queryMakeBudgetsBmList(houseId, bgc.getId());
                            for (BasicsgDTO basicsgDTO : bList) {
                                basicsgDTO.setImage(imageAddress + basicsgDTO.getImage());
                                if (basicsgDTO.getBuy() == 2) {
                                    basicsgDTO.setBuyStr("自购商品需自行购买");
                                } else {
                                    basicsgDTO.setBuyStr("");
                                }
                            }
                            Double priceArr = bList.stream().filter(a -> a.getPrice()!=null).mapToDouble(BasicsgDTO::getPrice).sum();
                            basicsGoodDTO.setPriceArr(priceArr);
                            basicsGoodDTO.setList(bList);
                            basicsGoodDTO.setName(bgc.getName());
                            bgdList.add(basicsGoodDTO);
                        }
                    }
                    Double priceArr = bgdList.stream().filter(a -> a.getPriceArr()!=null).mapToDouble(BasicsGoodDTO::getPriceArr).sum();
                    basicsGoodArrDTO.setPriceArr(priceArr);
                    basicsGoodArrDTO.setList(bgdList);
                }
                return ServerResponse.createBySuccess("查询成功", basicsGoodArrDTO);
            }else {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }



    /**
     * 查询精算详情列表
     *
     * @return
     */
    public ServerResponse queryBasicsProduct(String productId,
                                             PageDTO pageDTO,String cityId,String categoryId,String goodsId,
                                             String name, String attributeVal,
                                             String brandVal,String orderKey) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<ActuarialProductAppDTO> arr = new ArrayList<>();
        PageInfo pageResult = null;
        try {
            //根据内容模糊搜索商品
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String[] attributeVals = null;
            if (!CommonUtil.isEmpty(attributeVal)) {
                attributeVals = attributeVal.split(",");
            }
            String[] brandVals = null;
            if (!CommonUtil.isEmpty(brandVal)) {
                brandVals = brandVal.split(",");
            }
            String[] names=null;
            if(!CommonUtil.isEmpty(name)){
                names=name.split(",");
            }
            List<ActuarialProductAppDTO> pList = iBasicsProductTemplateMapper.serchCategoryProduct(categoryId, goodsId,names, brandVals, attributeVals, orderKey);
            pageResult = new PageInfo<>(pList);
            if (!pList.isEmpty()) {
                searchActuarialConfigServices.getProductList(pList, imageAddress);
                for (ActuarialProductAppDTO product : pList) {
                    if (productId.equals(product.getProductId())) {
                        //勾选商品标识
                        product.setFlag(true);
                    } else {
                        //未勾选商品标识
                        product.setFlag(false);
                    }
                    arr.add(product);
                }
            }
            pageResult.setList(arr);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }
}
