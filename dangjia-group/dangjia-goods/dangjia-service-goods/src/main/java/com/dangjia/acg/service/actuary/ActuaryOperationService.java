package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.*;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.house.House;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:27
 */
@Service
public class ActuaryOperationService {

    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private IGroupLinkMapper iGroupLinkMapper;
    @Autowired
    private IGoodsGroupMapper iGoodsGroupMapper;
    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;
    @Autowired
    private IUnitMapper unitMapper;
    @Autowired
    private ITechnologyMapper technologyMapper;
    @Autowired
    private IAttributeMapper attributeMapper;
    @Autowired
    private IBrandSeriesMapper iBrandSeriesMapper;
    @Autowired
    private IBrandMapper iBrandMapper;
    //    @Autowired
//    private IAttributeValueMapper valueMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private IAttributeMapper iAttributeMapper;
    @Autowired
    private HouseAPI houseAPI;
    protected static final Logger LOG = LoggerFactory.getLogger(ActuaryOperationService.class);

    /**
     * 选择取消精算
     * buy": 0必买；1可选选中；2自购; 3可选没选中(业主已取消)
     * <p>
     * 这里往精算表插入最新价格
     */
    public ServerResponse choiceGoods(String budgetIdList) {
        try {
            JSONArray arr = JSONArray.parseArray(budgetIdList);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int buy = Integer.parseInt(obj.getString("buy"));
                String budgetMaterialId = obj.getString("budgetMaterialId");

                BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
                if (buy == 3) {
                    budgetMaterial.setDeleteState(2);//取消
                } else if (buy == 1) {
                    budgetMaterial.setDeleteState(0);//选回来
                } else {
                    return ServerResponse.createByErrorMessage("操作失败,参数错误");
                }
                budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 更换货品
     */
    public ServerResponse changeProduct(String productId, String budgetMaterialId, String srcGroupId, String targetGroupId, String houseId, String workerTypeId) {
        try {
            LOG.info("changeProduct productId:" + productId);
            LOG.info("changeProduct budgetMaterialId:" + budgetMaterialId);
            LOG.info("changeProduct srcGroupId:" + srcGroupId);
            LOG.info("changeProduct targetGroupId:" + targetGroupId);
            LOG.info("changeProduct houseId:" + houseId);
            LOG.info("changeProduct workerTypeId:" + workerTypeId);

            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            if (StringUtils.isNotBlank(targetGroupId) && StringUtils.isNoneBlank(srcGroupId))//不为空  可以切换
            {
                List<String> allNoPayProductIds = new ArrayList<>();//所有未支付的product 是单品的
                List<String> productIds = new ArrayList<>();//所有未支付的product 是单品的

                //未支付的材料
                List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                for (BudgetMaterial budgetMaterial1 : budgetMaterials) {
                    LOG.info("未支付 budgetMaterial1 :" + budgetMaterial1.getProductName() + " id" + budgetMaterial1.getId());
                    allNoPayProductIds.add(budgetMaterial1.getProductId());
                    if (!productIds.contains(budgetMaterial1.getProductId()) && !StringUtils.isNoneBlank(budgetMaterial1.getGoodsGroupId()))
                        productIds.add(budgetMaterial1.getProductId());
                }

                LOG.info("单品 productIds:" + productIds);
                for (String pId : productIds) {
                    Product product = productMapper.selectByPrimaryKey(pId);
                    LOG.info("单品 pId:" + product);
                }

                //找到 原关联组的goods成员， 把 goods 下的product 更换 成 目标关联组的 goods下的product
                List<GroupLink> srcGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(srcGroupId);
                List<GroupLink> targetGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(targetGroupId);

                for (GroupLink groupLink : srcGroupLinkLists) {
                    LOG.info(" srcGroupLinkLists :" + groupLink.getProductName() + " id" + groupLink.getId() + groupLink);
                }

                for (GroupLink groupLink : targetGroupLinkLists) {
                    LOG.info(" targetGroupLinkLists :" + groupLink.getProductName() + " id" + groupLink.getId() + groupLink);
                }

                //找到切换的product
                for (GroupLink srcGroupLink : srcGroupLinkLists) {
                    //进行 更换
                    for (GroupLink targetGroupLink : targetGroupLinkLists) {
                        for (BudgetMaterial budgetMaterial1 : budgetMaterials) {
                            if (allNoPayProductIds.contains(srcGroupLink.getProductId())
                                    && !productIds.contains(srcGroupLink.getProductId()))//在未购买的材料里 并且 不在单品里
                            {
                                if (targetGroupLink.getProductId().equals(srcGroupLink.getProductId())) {

                                    if (budgetMaterial1.getProductId().equals(srcGroupLink.getProductId())) {
                                        BudgetMaterial srcBudgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterial1.getId());
                                        Product product = productMapper.selectByPrimaryKey(srcGroupLink.getProductId());
                                        LOG.info("product :" + product + " pid:" + product.getId());
                                        LOG.info("srcBudgetMaterial 换前:" + srcBudgetMaterial);
                                        srcBudgetMaterial.setProductId(product.getId());
                                        srcBudgetMaterial.setProductSn(product.getProductSn());
                                        srcBudgetMaterial.setProductName(product.getName());
                                        srcBudgetMaterial.setPrice(product.getPrice());
                                        srcBudgetMaterial.setCost(product.getCost());
                                        srcBudgetMaterial.setTotalPrice(product.getPrice() * srcBudgetMaterial.getShopCount());
                                        LOG.info("srcBudgetMaterial 换后:" + srcBudgetMaterial);
                                        budgetMaterialMapper.updateByPrimaryKeySelective(srcBudgetMaterial);
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                Product product = productMapper.selectByPrimaryKey(productId);
                budgetMaterial.setProductId(productId);
                budgetMaterial.setProductSn(product.getProductSn());
                budgetMaterial.setProductName(product.getName());
                budgetMaterial.setPrice(product.getPrice());
                budgetMaterial.setCost(product.getCost());
                budgetMaterial.setTotalPrice(product.getPrice() * budgetMaterial.getShopCount());
                budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
            }

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 选择货品刷新页面
     *
     * @param goodsId
     * @param brandSeriesId
     * @param attributeIdArr 属性值id集合
     */
    public ServerResponse selectProduct(String goodsId, String brandSeriesId, String attributeIdArr, String budgetMaterialId) {
        try {
            if (!StringUtils.isNoneBlank(goodsId))
                return ServerResponse.createByErrorMessage("goodsId 不能为null");
            if (!StringUtils.isNoneBlank(brandSeriesId))
                return ServerResponse.createByErrorMessage("brandSeriesId 不能为null");
            if (!StringUtils.isNoneBlank(attributeIdArr))
                return ServerResponse.createByErrorMessage("attributeIdArr 不能为null");
            if (!StringUtils.isNoneBlank(budgetMaterialId))
                return ServerResponse.createByErrorMessage("budgetMaterialId 不能为null");

            String[] valueIdArr = attributeIdArr.split(",");
            LOG.info("selectProduct goodsId :" + goodsId);
            LOG.info("selectProduct brandSeriesId :" + brandSeriesId);
            LOG.info("selectProduct attributeIdArr :" + attributeIdArr);
            LOG.info("selectProduct budgetMaterialId :" + budgetMaterialId);
            for (String str : valueIdArr) {
                LOG.info("valueIdArr str:" + str);
            }
            Product product = productMapper.selectProduct(goodsId, brandSeriesId, valueIdArr);
            if (product == null) {
                return ServerResponse.createBySuccess("暂无该货号", "");
            }
            GoodsDTO goodsDTO = goodsDetail(product, budgetMaterialId);
            if (goodsDTO != null) {
                return ServerResponse.createBySuccess("查询成功", goodsDTO);
            } else {
                return ServerResponse.createByErrorMessage("查询失败,数据异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
    }

    /**
     * 商品详情
     * gId:  budgetWorkerId   budgetMaterialId
     */
    public ServerResponse getCommo(String gId, int type) {
        try {
            if (type == 1) {//人工
                BudgetWorker budgetWorker = budgetWorkerMapper.selectByPrimaryKey(gId);
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());//人工商品
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                wGoodsDTO.setPrice("￥" + workerGoods.getPrice() + "/" + unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(getImage(workerGoods.getWorkerDec()));
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getId());
                for (Technology technology : technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功", wGoodsDTO);
            } else if (type == 2 || type == 3) {//材料商品  服务商品
                BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(gId);
                Product product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());//当前 货品
                GoodsDTO goodsDTO = goodsDetail(product, gId);
                if (goodsDTO != null) {
                    return ServerResponse.createBySuccess("查询成功", goodsDTO);
                } else {
                    return ServerResponse.createByErrorMessage("查询失败,数据异常");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
        return ServerResponse.createByErrorMessage("查询失败,type错误");
    }

    /**
     * 商品详情
     * gId:  WorkerGoodsId   ProductId
     */
    public ServerResponse getGoodsDetail(String gId, String cityId, int type) {
        try {
            if (type == 1) {//人工
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                if (!CommonUtil.isEmpty(workerGoods.getImage())) {
                    wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                }
                wGoodsDTO.setPrice("￥" + workerGoods.getPrice() + "/" + unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(getImage(workerGoods.getWorkerDec()));
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getId());
                for (Technology technology : technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功", wGoodsDTO);
            } else if (type == 2 || type == 3) {//材料商品  服务商品
                Product product = productMapper.selectByPrimaryKey(gId);//当前 货品
                GoodsDTO goodsDTO = goodsDetail(product, gId);
                if (goodsDTO != null) {
                    return ServerResponse.createBySuccess("查询成功", goodsDTO);
                } else {
                    return ServerResponse.createByErrorMessage("查询失败,数据异常");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败,数据异常");
        }
        return ServerResponse.createByErrorMessage("查询失败,type错误");
    }

    //商品详情
    public GoodsDTO goodsDetail(Product product, String budgetMaterialId) {
        try {
            LOG.info("goodsDetail product:" + product);
            LOG.info("goodsDetail budgetMaterialId:" + budgetMaterialId);
            GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
            List<String> imageList = new ArrayList<String>();//长图片 多图组合
//            List<AttributeDTO> attributeDTOList = new ArrayList<AttributeDTO>();//属性
            List<BrandDTO> brandDTOList = new ArrayList<BrandDTO>();//品牌

            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());//当前 商品
            //该商品关联所有品牌系列
            List<BrandSeries> brandSeriesList = iBrandSeriesMapper.queryBrandByGid(goods.getId());
//            //根据商品分类id关联所有价格属性
//            List<Attribute> goodsAttributeList = attributeMapper.queryPriceAttribute(goods.getCategoryId());

            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            if (budgetMaterial == null) {
                LOG.info("暂无该精算 ");
            }
            goodsDTO.setBudgetMaterialId(budgetMaterialId);
            goodsDTO.setSrcGroupId(budgetMaterial.getGoodsGroupId());
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setImage(getImage(product.getImage()));//图一张
            goodsDTO.setPrice("￥" + product.getPrice() + "/" + product.getUnitName());
            goodsDTO.setName(product.getName());
            goodsDTO.setUnitName(product.getUnitName());//单位
            goodsDTO.setProductType(goods.getType());//材料类型

//            String curAttributeValueId = "";

            Set<String> brandSet = new HashSet();
            Set<String> brandSeriesSet = new HashSet();
            for (BrandSeries brandSeries : brandSeriesList) {//循环品牌系列
                brandSet.add(brandSeries.getBrandId());
                brandSeriesSet.add(brandSeries.getId());
            }

            for (String brandId : brandSet) {//循环品牌系列
                Brand brand = iBrandMapper.selectByPrimaryKey(brandId);
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setBrandId(brand.getId());
                brandDTO.setName(brand.getName());
                if (brand.getId().equals(product.getBrandId())) {
                    brandDTO.setState(1);//选中
                } else {
                    brandDTO.setState(0);//未选中
                }
                List<BrandSeriesDTO> brandSeriesDTOList = new ArrayList<BrandSeriesDTO>();//品牌系列
                for (String brandSeriesId : brandSeriesSet) {//循环品牌系列
                    BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(brandSeriesId);
                    if (brandSeries.getBrandId().equals(brandId)) { //相同的品牌
                        BrandSeriesDTO brandSeriesDTO = new BrandSeriesDTO();
                        brandSeriesDTO.setBrandSeriesId(brandSeries.getId());
                        brandSeriesDTO.setName(brandSeries.getName());

                        if (brandSeries.getId().equals(product.getBrandSeriesId())) {
                            brandSeriesDTO.setState(1);//选中
                            imageList.add(getImage(brandSeries.getImage()));//加入品牌系列图
                        } else {
                            brandSeriesDTO.setState(0);//未选中
                        }

                        LOG.info(" brandSeries id:" + brandSeries.getId() + " getBrandId:" + brandSeries.getBrandId() + " name:" + brandSeries.getName());
                        List<AttributeDTO> attributeDTOList = getAllAttributes(budgetMaterial, product, brandSeries);

                        LOG.info(" brandSeries:" + brandSeries.getName());
                        LOG.info(" attributeDTOList :" + attributeDTOList);

                        brandSeriesDTO.setAttributeDTOList(attributeDTOList);
                        brandSeriesDTOList.add(brandSeriesDTO);
                    }
                }

                brandDTO.setBrandSeriesDTOList(brandSeriesDTOList);
                brandDTOList.add(brandDTO);
            }


            Product srcProduct = product;
            goodsDTO.setIsSwitch(1);//默认不可切换
            if (srcProduct != null && budgetMaterial != null) {
                if (budgetMaterial.getGoodsGroupId() != null) {
                    LOG.info("valueList budgetMaterial:" + budgetMaterial + " srcProduct:" + srcProduct);
                    LOG.info("valueList srcProduct.getId():" + srcProduct.getId());
                    GroupLink groupLink = iGroupLinkMapper.queryGroupLinkByGroupIdAndPid(budgetMaterial.getGoodsGroupId(), srcProduct.getId());
                    if (groupLink != null) {
                        LOG.info("valueList groupLink:" + groupLink);
                        LOG.info("valueList getProductName:" + groupLink.getProductName() + " getGroupId:" + groupLink.getGroupId() + "getGoodsName:" + groupLink.getGoodsName() + " groupLink.getIsSwitch():" + groupLink.getIsSwitch());
                        goodsDTO.setIsSwitch(groupLink.getIsSwitch());
                        if (goodsDTO.getIsSwitch() == 0) {
                            goodsDTO.setTargetGroupId(groupLink.getGroupId()); //可以切换的关联组
//                            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(goodsDTO.getSrcGroupId());
//                            if (goodsGroup.getSwitchArr() != null) {
//                                String[] strGroupIdArr = goodsGroup.getSwitchArr().split(",");
//                                for (String groupId : strGroupIdArr) {
//                                    if (groupId.equals(groupLink.getGroupId())) {
//                                        goodsDTO.setTargetGroupId(groupId);
//                                        break;
//                                    }
//                                }
//                            }
//                            avDTO.setName(attributeValue.getName() + " 0");
                        }
                    }
                }
            }

            if (goodsDTO.getSrcGroupId() != null)
                LOG.info("goodsDetail SrcGroupId:" + iGoodsGroupMapper.selectByPrimaryKey(goodsDTO.getSrcGroupId()));
            if (goodsDTO.getTargetGroupId() != null)
                LOG.info("goodsDetail TargetGroupId:" + iGoodsGroupMapper.selectByPrimaryKey(goodsDTO.getTargetGroupId()));

            goodsDTO.setBrandDTOList(brandDTOList);
            goodsDTO.setImageList(imageList);
            LOG.info("goodsDetail goodsDTO:" + goodsDTO);
            return goodsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //判断该货品是不是该属性
    private boolean isValue(String valueId, String valueIdArr) {
        try {
            String[] valueIdList = valueIdArr.split(",");
            for (int i = 0; i < valueIdList.length; i++) {
                if (valueId.equals(valueIdList[i])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // contains

    /**
     * 检查 strArr 字符数组中，是否包含 value值
     *
     * @param value
     * @param strArr
     * @return
     */
    private boolean isContainsValue(String value, String strArr) {
        String[] arr = strArr.split(",");
        if (arr != null) {
            for (String str : arr) {
                if (value.equals(str))
                    return true;
            }
        }
        return false;
    }

    //取第一张图
    private String getImage(String images) {
        try {
            if (StringUtil.isNotEmpty(images)) {
                String[] imageArr = images.split(",");
                return configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + imageArr[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";//图片上传错误
        }
        return "";//暂无图片
    }

    /**
     * 查看工序 type 人工1 材料2 服务3
     * 支付时精算goods详情 查最新价格 共用此方法
     */
    public ServerResponse confirmActuaryDetail(String userToken, String houseId, String workerTypeId, int type, String cityId) {
        try{
            ServerResponse serverResponse = workerTypeAPI.getNameByWorkerTypeId(workerTypeId);
            String workerTypeName = "";
            if (serverResponse.isSuccess()) {
                workerTypeName = serverResponse.getResultObj().toString();
            } else {
                return ServerResponse.createByErrorMessage("查询工序精算失败");
            }
            Map<Integer, String> mapgx = new HashMap<>();
            mapgx.put(DjConstants.GXType.RENGGONG, "人工");
            mapgx.put(DjConstants.GXType.CAILIAO, "材料");
            mapgx.put(DjConstants.GXType.FUWU, "服务");
            FlowDTO flowDTO = new FlowDTO();
            flowDTO.setName(workerTypeName);
            flowDTO.setType(type);
            List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<FlowActuaryDTO>();
            String typsValue = mapgx.get(type);
            if (CommonUtil.isEmpty(typsValue)) {
                return ServerResponse.createByErrorMessage("type参数错误");
            }
            if (type == DjConstants.GXType.RENGGONG) {
                List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId, workerTypeId);
                for (BudgetWorker bw : budgetWorkerList) {
                    WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(bw.getWorkerGoodsId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setName(bw.getName());
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + workerGoods.getImage());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setShopCount(bw.getShopCount());
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken, cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + bw.getId() + "&type=" + type;
                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setPrice("￥" + workerGoods.getPrice() + "/" + unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
                    flowActuaryDTO.setTotalPrice(workerGoods.getPrice() * bw.getShopCount());
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
                Double workerPrice = budgetWorkerMapper.getBudgetWorkerPrice(houseId, workerTypeId);//精算工钱
                flowDTO.setSumTotal(workerPrice);//合计
            } else {
                List<BudgetMaterial> budgetMaterialList = null;
                if (type == DjConstants.GXType.CAILIAO) {
                    budgetMaterialList = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                    Double caiPrice = budgetMaterialMapper.getBudgetCaiPrice(houseId, workerTypeId);
                    flowDTO.setSumTotal(caiPrice);//合计
                }
                if (type == DjConstants.GXType.FUWU) {
                    budgetMaterialList = budgetMaterialMapper.getBudgetSerList(houseId, workerTypeId);
                    Double serPrice = budgetMaterialMapper.getBudgetSerPrice(houseId, workerTypeId);
                    flowDTO.setSumTotal(serPrice);//合计
                }
                for (BudgetMaterial bm : budgetMaterialList) {
                    Goods goods = goodsMapper.selectByPrimaryKey(bm.getGoodsId());
                    Product product = productMapper.selectByPrimaryKey(bm.getProductId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    if (product != null){
                        flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + product.getImage());
                        String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken,
                                cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + bm.getId() + "&type=" + type;
                        flowActuaryDTO.setUrl(url);
                        flowActuaryDTO.setAttribute(getAttributes(product));//拼接属性品牌
                        flowActuaryDTO.setPrice("￥" + product.getPrice() + "/" + product.getUnitName());
                        flowActuaryDTO.setTotalPrice(product.getPrice() * bm.getShopCount());
                    }
                    flowActuaryDTO.setBudgetMaterialId(bm.getId());
                    flowActuaryDTO.setName(bm.getGoodsName());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setShopCount(bm.getShopCount());
                    if (bm.getDeleteState() == 2) {
                        flowActuaryDTO.setBuy(3);//可选没选中(业主已取消)
                    } else {
                        flowActuaryDTO.setBuy(goods.getBuy());
                    }
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
            }
            flowDTO.setFlowActuaryDTOList(flowActuaryDTOList);
            return ServerResponse.createBySuccess("查询成功", flowDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //拼接属性品牌
    private String getAttributes(Product product) {
        String attributes = "";
        try {
            String[] valueIdArr = product.getValueIdArr().split(",");
            for (int i = 0; i < valueIdArr.length; i++) {
                AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueIdArr[i]);
                attributes = attributes + " " + attributeValue.getName();
            }
            BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(product.getBrandSeriesId());
            attributes = attributes + " " + brandSeries.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "查询属性失败";
        }
        return attributes;
    }

    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(BudgetMaterial budgetMaterial, Product product, BrandSeries brandSeries) {

        List<String> imageList = new ArrayList<String>();//长图片 多图组合
        List<AttributeDTO> attributeDTOList = new ArrayList<>();
        //查询 一个 goods 的 某个 系列的 所有 product
        List<Product> productList = productMapper.queryByGoodsIdAndbrandSeriesId(product.getGoodsId(), brandSeries.getId());
        Set<String> attributeIdSet = new HashSet<String>();
        for (Product pt : productList) { //查所有属性id
            String strAttributeIdArr = pt.getAttributeIdArr();
            String[] strAtIdArr = strAttributeIdArr.split(",");
            if (StringUtils.isNoneBlank(strAtIdArr)) {
                for (String atId : strAtIdArr) {
                    attributeIdSet.add(atId);
                }
            }
        }

        Set<String> attributeValueSet = new HashSet();
        int index = 0;
        for (String atId : attributeIdSet) {
            //属性 id
            Attribute attribute = iAttributeMapper.selectByPrimaryKey(atId);
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId(attribute.getId());
            attributeDTO.setName(attribute.getName());

            LOG.info("attributeDTO name:" + attributeDTO.getName());
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            for (Product pt : productList) { //根据 属性id ，找 属性id 对应的属性值
//                if (isContainsValue(atId, pt.getAttributeIdArr()))//如果包含该属性
                String[] strVIs = pt.getValueIdArr().split(",");
                if (StringUtils.isNoneBlank(strVIs)) {
                    int j = 0;
                    for (String strVId : strVIs) {
                        if (j == index) {
                            AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(strVId);
                            if (attributeValueSet.contains(attributeValue.getId()))
                                break;
//                            if (this.isValue(attributeValue.getId(), product.getValueIdArr())) {//当前货品属性
//                                avDTO.setState(1);//选中
//                                imageList.add(getImage(attributeValue.getImage()));//属性图
//                            } else {
//                                avDTO.setState(0);//未选中
//                            }
                            int isSwitch = 1;
                            Product srcProduct = product;
//                            avDTO.setIsSwitch(1);//默认不可切换
                            if (srcProduct != null && budgetMaterial != null) {
                                if (budgetMaterial.getGoodsGroupId() != null) {
                                    LOG.info("valueList budgetMaterial:" + budgetMaterial + " srcProduct:" + srcProduct);
                                    LOG.info("valueList srcProduct.getId():" + srcProduct.getId());
                                    GroupLink groupLink = iGroupLinkMapper.queryGroupLinkByGroupIdAndPid(budgetMaterial.getGoodsGroupId(), srcProduct.getId());
                                    if (groupLink != null) {
                                        LOG.info("valueList groupLink:" + groupLink);
                                        LOG.info("valueList getProductName:" + groupLink.getProductName() + " getGroupId:" + groupLink.getGroupId() + "getGoodsName:" + groupLink.getGoodsName() + " groupLink.getIsSwitch():" + groupLink.getIsSwitch());
                                        if (groupLink.getIsSwitch() == 0) {
                                            isSwitch = 0;
                                        }
//                                        avDTO.setIsSwitch(groupLink.getIsSwitch());
//                                        if (avDTO.getIsSwitch() == 0) {
//                                            avDTO.setTargetGroupId(groupLink.getGroupId());
//                                        avDTO.setName(attributeValue.getName() + " 0");
//                                        }
                                    }
                                }
                            }

                            attributeValueSet.add(attributeValue.getId());
//                        if (attributeValue.getAttributeId().equals(attributeDTO.getId())) {
                            AttributeValueDTO avDTO = new AttributeValueDTO();
                            avDTO.setAttributeValueId(attributeValue.getId());
                            avDTO.setName(attributeValue.getName());

                            if (isContainsValue(attributeValue.getId(), product.getValueIdArr()))//如果包含该属性
                            {
                                avDTO.setState(1);//选中
                                imageList.add(getImage(attributeValue.getImage()));//属性图
                            } else {
                                avDTO.setState(0);//未选中

                                //如果 不问null 说明 是关联组
                                if (StringUtils.isNoneBlank(budgetMaterial.getGoodsGroupId())
                                        && isSwitch != 0) {//如果是关联组，就不显示 单品
                                    break;
                                }

                                if (!StringUtils.isNoneBlank(budgetMaterial.getGoodsGroupId())
                                        && isSwitch == 0) {//如果不是关联组，就不显示 关联组的
                                    break;
                                }
                            }
                            attributeValueDTOList.add(avDTO);//添加属性值
                        }
                        j++;
                    }
//                    }
                }
            }

            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
            index++;
        }

        return attributeDTOList;
    }

    /**
     * 精算详情 productType  0：材料；1：服务
     */
    public ServerResponse confirmActuary(String userToken, String houseId, String cityId) {
        //从master获取工序详情
        List<Map<String, String>> mapList = getForBudgetAPI.getFlowList(houseId);
        ActuaryDetailsDTO actuaryDetailsDTO = new ActuaryDetailsDTO();//最外层
        List<FlowDetailsDTO> flowDetailsDTOList = new ArrayList<FlowDetailsDTO>();
        for (Map<String, String> map : mapList) {
            String name = map.get("name");
            String workerTypeId = map.get("workerTypeId");
            FlowDetailsDTO flowDetailsDTO = new FlowDetailsDTO();
            flowDetailsDTO.setName(name);
            List<DetailsDTO> detailsDTOList = new ArrayList<DetailsDTO>();//人工材料服务
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId, workerTypeId);//人工明细
            List<BudgetMaterial> materialCaiList = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);//材料明细
            List<BudgetMaterial> materialSerList = budgetMaterialMapper.getBudgetSerList(houseId, workerTypeId);//服务明细
            List<Map> mapworker = new ArrayList<>();
            Map<Integer, String> mapgx = new HashMap<>();
            mapgx.put(DjConstants.GXType.RENGGONG, "人工");
            mapgx.put(DjConstants.GXType.CAILIAO, "材料");
            mapgx.put(DjConstants.GXType.FUWU, "服务");
            for (Map.Entry<Integer, String> entry : mapgx.entrySet()) {
                Map m = new HashMap();
                m.put("key", String.valueOf(entry.getKey()));
                m.put("name", entry.getValue());
                Integer size = 0;
                if (DjConstants.GXType.RENGGONG == entry.getKey()) {
                    size = budgetWorkerList.size();
                }
                if (DjConstants.GXType.CAILIAO == entry.getKey()) {
                    size = materialCaiList.size();
                }
                if (DjConstants.GXType.FUWU == entry.getKey()) {
                    size = materialSerList.size();
                }
                m.put("size", size);
                mapworker.add(m);
            }
            for (Map mp : mapworker) {
                Integer size = (Integer) mp.get("size");
                String names = (String) mp.get("name");
                String key = (String) mp.get("key");
                if (size > 0) {
                    DetailsDTO detailsDTO = new DetailsDTO();
                    detailsDTO.setImage("");
                    detailsDTO.setNameA(names);
                    detailsDTO.setNameB(name + "阶段" + names);
                    detailsDTO.setNameC(names + "明细");
                    detailsDTO.setType(key);
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.CONFIRMACTUARYDETAIL, userToken, cityId, names + "明细") + "&houseId=" + houseId + "&workerTypeId=" + workerTypeId + "&type=" + key;
                    detailsDTO.setUrl(url);
                    detailsDTOList.add(detailsDTO);
                }
            }
            flowDetailsDTO.setDetailsDTOList(detailsDTOList);
            flowDetailsDTOList.add(flowDetailsDTO);
        }
        House house = houseAPI.getHouseById(houseId);
        actuaryDetailsDTO.setHouseId(houseId);
        actuaryDetailsDTO.setFlowDetailsDTOList(flowDetailsDTOList);
        actuaryDetailsDTO.setBudgetOk(house.getBudgetOk());

        return ServerResponse.createBySuccess("查询成功", actuaryDetailsDTO);
    }
}
