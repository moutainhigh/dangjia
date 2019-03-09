package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.repair.MendOrderAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.dto.repair.MendOrderInfoDTO;
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
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
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
    private IUnitMapper iUnitMapper;
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

    @Autowired
    private MendOrderAPI mendOrderAPI;
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
            int count = 0;
            String ret = productId + " " + budgetMaterialId + " " + srcGroupId + " " + targetGroupId + " " + houseId + " " + workerTypeId;
            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            if (StringUtils.isNotBlank(targetGroupId) && StringUtils.isNoneBlank(srcGroupId))//不为空  可以切换
            {
                //找到 原关联组的goods成员， 把 goods 下的product 更换 成 目标关联组的 goods下的product
                List<GroupLink> srcGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(srcGroupId);
                List<GroupLink> targetGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(targetGroupId);
                for (GroupLink groupLink : srcGroupLinkLists) {
                    LOG.info(" srcGroupLinkLists :" + groupLink.getProductName() + " id" + groupLink.getId() + groupLink);
                }
                for (GroupLink groupLink : targetGroupLinkLists) {
                    LOG.info(" targetGroupLinkLists :" + groupLink.getProductName() + " id" + groupLink.getId() + groupLink);
                }

                Set<String> allNoPayProductIds = new HashSet<>();//所有未支付的product 是单品的
                Set<String> danProductIds = new HashSet<>();//所有未支付的product 是单品的 或者是 其他关联组的商品，都不参与切换
                //未支付的材料1
                List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                for (BudgetMaterial budgetMaterial1 : budgetMaterials) {
                    LOG.info("未支付 budgetMaterial1 :" + budgetMaterial1 + " budgetMaterial1Id:" + budgetMaterial1.getId());
                    if (budgetMaterial.getGoodsGroupId().equals(srcGroupId)
//                            || budgetMaterial.getGoodsGroupId().equals(targetGroupId)
                    ) {
                        allNoPayProductIds.add(budgetMaterial1.getProductId());
                    }

                    if (!StringUtils.isNoneBlank(budgetMaterial1.getGoodsGroupId())) {
                        LOG.info("加入单品 GoodsGroupId null：" + budgetMaterial1);
                        danProductIds.add(budgetMaterial1.getProductId());
                    }


                    //精算时，原关联组里的商品，如果被其他关联组的替换了，也不能更换
                    for (GroupLink groupLink : srcGroupLinkLists) {
                        if (budgetMaterial1.getProductId().equals(groupLink.getProductId())
                                && !budgetMaterial1.getGoodsGroupId().equals(groupLink.getGroupId())) {
                            LOG.info("被其他关联组的替换的" + budgetMaterial1);
                            danProductIds.add(budgetMaterial1.getProductId());
                        }
                    }

                }
                for (String pId : allNoPayProductIds) {
                    Product product = productMapper.selectByPrimaryKey(pId);
                    LOG.info("allNoPayProductIds :" + " size:" + allNoPayProductIds.size() + "  " + allNoPayProductIds + product);
                }
                for (String pId : danProductIds) {
                    Product product = productMapper.selectByPrimaryKey(pId);
                    LOG.info("单品productIds :" + " size:" + danProductIds.size() + "  " + danProductIds + product);
                }

                for (GroupLink srcGroupLink : srcGroupLinkLists) {
                    LOG.info(" srcGroupLinkLists id :" + srcGroupLink.getId() + srcGroupLink);
                    for (GroupLink targetGroupLink : targetGroupLinkLists) {
                        LOG.info(" targetGroupLink id:" + targetGroupLink.getId() + targetGroupLink);
                        //原关联组的对应的goodsId 和 目标 关联组 goodsId 一样时，进行更换 product
                        if (srcGroupLink.getGoodsId().equals(targetGroupLink.getGoodsId())
                                && allNoPayProductIds.contains(srcGroupLink.getProductId())) {// 必须属于 未购买里的是 原关联组 和 目标关联组里包含的。
                            if (danProductIds.contains(srcGroupLink.getProductId())) {//如果是单品，就不换
                                LOG.info(" 单品不执行更换 :" + srcGroupLink.getProductName() + " id" + srcGroupLink.getId() + srcGroupLink);
                                continue;
                            }

                            //查到 老的关联组 的精算
                            BudgetMaterial srcBudgetMaterial = budgetMaterialMapper.getBudgetCaiListByGoodsId(houseId, workerTypeId, srcGroupLink.getGoodsId());
                            Product targetProduct = productMapper.selectByPrimaryKey(targetGroupLink.getProductId());//目标product 对象
                            LOG.info("product :" + targetProduct + " pid:" + targetProduct.getId());
                            LOG.info("srcBudgetMaterial 换前:" + srcBudgetMaterial);
                            srcBudgetMaterial.setProductId(targetProduct.getId());
                            srcBudgetMaterial.setProductSn(targetProduct.getProductSn());
                            srcBudgetMaterial.setProductName(targetProduct.getName());
                            srcBudgetMaterial.setPrice(targetProduct.getPrice());
                            srcBudgetMaterial.setGoodsGroupId(targetGroupId);
                            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(targetGroupId);
                            srcBudgetMaterial.setGroupType(goodsGroup.getName());
                            srcBudgetMaterial.setCost(targetProduct.getCost());
                            //这里会更新 为 新product的 换算后的购买数量
//                            srcBudgetMaterial.setConvertCount(Math.ceil(srcBudgetMaterial.getShopCount() / targetProduct.getConvertQuality()));
                            Double converCount = Math.ceil(srcBudgetMaterial.getShopCount() / targetProduct.getConvertQuality());
                            srcBudgetMaterial.setConvertCount(converCount.intValue());
                            srcBudgetMaterial.setTotalPrice(targetProduct.getPrice() * srcBudgetMaterial.getConvertCount());
                            LOG.info("srcBudgetMaterial 换后:" + srcBudgetMaterial);
                            budgetMaterialMapper.updateByPrimaryKey(srcBudgetMaterial);
                            count++;
                        }

                    }
                }

                LOG.info("count ::" + count);
            } else {
                Product product = productMapper.selectByPrimaryKey(productId);
                budgetMaterial.setProductId(productId);
                budgetMaterial.setProductSn(product.getProductSn());
                budgetMaterial.setProductName(product.getName());
                budgetMaterial.setPrice(product.getPrice());
                budgetMaterial.setCost(product.getCost());
                //这里会更新 为 新product的 换算后的购买数量
                Double converCount = Math.ceil(budgetMaterial.getShopCount() / product.getConvertQuality());
                budgetMaterial.setConvertCount(converCount.intValue());
                budgetMaterial.setTotalPrice(product.getPrice() * budgetMaterial.getConvertCount());
                budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
                return ServerResponse.createBySuccessMessage("操作成功" + ret);
            }

            if (count == 0)
                return ServerResponse.createByErrorMessage(count + "操作失败" + ret);
            else
                return ServerResponse.createBySuccessMessage(count + "操作成功" + ret);
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
    public ServerResponse selectProduct(String goodsId, String brandId, String brandSeriesId, String attributeIdArr, String budgetMaterialId) {
        try {
            if (!StringUtils.isNoneBlank(goodsId))
                return ServerResponse.createByErrorMessage("goodsId 不能为null");
//            if (!StringUtils.isNoneBlank(brandId))
//                return ServerResponse.createByErrorMessage("brandId 不能为null");
//            if (!StringUtils.isNoneBlank(brandSeriesId))
//                return ServerResponse.createByErrorMessage("brandSeriesId 不能为null");
//            if (!StringUtils.isNoneBlank(attributeIdArr))
//                return ServerResponse.createByErrorMessage("attributeIdArr 不能为null");
            if (!StringUtils.isNoneBlank(budgetMaterialId))
                return ServerResponse.createByErrorMessage("budgetMaterialId 不能为null");

            String[] valueIdArr = attributeIdArr.split(",");
            LOG.info("selectProduct goodsId :" + goodsId);
            LOG.info("selectProduct brandId :" + brandId);
            LOG.info("selectProduct brandSeriesId :" + brandSeriesId);
            LOG.info("selectProduct attributeIdArr :" + attributeIdArr);
            LOG.info("selectProduct budgetMaterialId :" + budgetMaterialId);
            for (String str : valueIdArr) {
                LOG.info("valueIdArr str:" + str);
            }
            Example example = new Example(Product.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Product.TYPE, 1);
            criteria.andEqualTo(Product.MAKET, 1);
            if (!CommonUtil.isEmpty(goodsId)) {
                criteria.andEqualTo(Product.GOODS_ID, goodsId);
            }
            if (!CommonUtil.isEmpty(brandId)) {
                criteria.andEqualTo(Product.BRAND_ID, brandId);
            } else {
                criteria.andCondition("  (isnull(brand_id) or brand_id = '')");
            }
            if (!CommonUtil.isEmpty(brandSeriesId)) {
                criteria.andEqualTo(Product.BRAND_SERIES_ID, brandSeriesId);
            } else {
                criteria.andCondition("  (isnull(brand_series_id) or brand_series_id = '') ");
            }

            if (valueIdArr == null || valueIdArr.length == 0 || CommonUtil.isEmpty(attributeIdArr)) {
                criteria.andCondition(" (isnull(value_id_arr) or value_id_arr = '') ");
            } else {
                for (String val : valueIdArr) {
                    criteria.andCondition("  FIND_IN_SET('" + val + "',value_id_arr) ");
                }
            }
            List<Product> products = productMapper.selectByExample(example);
//            Product product = productMapper.selectProduct(goodsId, brandId, brandSeriesId, valueIdArr);
            if (products == null || products.size() == 0) {
                return ServerResponse.createBySuccess("暂无该货号", "");
            }
            Product product = products.get(0);
            GoodsDTO goodsDTO = goodsDetail(product, budgetMaterialId, 3);
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
                wGoodsDTO.setPrice("¥" + String.format("%.2f", workerGoods.getPrice()) + "/" + workerGoods.getUnitName());
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
                GoodsDTO goodsDTO = goodsDetail(product, budgetMaterial.getId(), 2);
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
                wGoodsDTO.setPrice("¥" + String.format("%.2f", workerGoods.getPrice()) + "/" + workerGoods.getUnitName());
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
                GoodsDTO goodsDTO = goodsDetail(product, null, 1);
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

    /**
     * @param product
     * @param budgetMaterialId 传null ：表示不是精算里的商品。 如果是精算里的商品 ，可能有 关联组，关联组id 在 精算表里存的，所以，需要传精算id  ，
     * @param apiType          哪个接口调用的 1: getGoodsDetail 接口    2：getCommo 接口    3: selectProduct 接口
     * @return GoodsDTO
     */
    public GoodsDTO goodsDetail(Product product, String budgetMaterialId, Integer apiType) {
        try {
            LOG.info("goodsDetail product:" + product);
            LOG.info("goodsDetail productId:" + product.getId());
            LOG.info("goodsDetail budgetMaterialId:" + budgetMaterialId);
            LOG.info("goodsDetail apiType:" + apiType);
            GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());//当前 商品
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setImage(getImage(product.getImage()));//图一张
            String convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();
//            goodsDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + product.getUnitName());
            goodsDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
            goodsDTO.setName(product.getName());
//            goodsDTO.setUnitName(product.getUnitName());//单位
            goodsDTO.setUnitName(convertUnitName);//单位
            goodsDTO.setProductType(goods.getType());//材料类型

            Product srcProduct = product;
            List<String> imageList = new ArrayList<String>();//长图片 多图组合
            GoodsGroup srcGoodsGroup = null;
            //找到一个groupId 的可以切换的目标关联组
            List<GroupLink> groupLinkTargetList = new ArrayList<>();//可以切换的其他关联组的 GroupLink的productId
            Set<String> pIdTargetGroupSet = new HashSet();//目标关联组下的所有productId
            pIdTargetGroupSet.add(product.getId());
//            goodsDTO.setIsSwitch(1);//默认不可切换

            BudgetMaterial budgetMaterial = null;
            if (srcProduct != null) {
                if (budgetMaterialId != null)
                    budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
                if (budgetMaterial == null)
                    LOG.info("暂无该精算 ");
                else {//有精算的时候，才有可能 有关联组的处理
                    if (StringUtils.isNoneBlank(budgetMaterial.getGoodsGroupId())) {
                        LOG.info("valueList budgetMaterial:" + budgetMaterial + " srcProduct:" + srcProduct);
                        LOG.info("valueList srcProduct.getId():" + srcProduct.getId());
                        srcGoodsGroup = iGoodsGroupMapper.selectByPrimaryKey(budgetMaterial.getGoodsGroupId());
                        LOG.info("srcGoodsGroup:" + srcGoodsGroup);
                        goodsDTO.setIsSwitch(1);
                        String[] gGroupIds = srcGoodsGroup.getSwitchArr().split(",");
                        if (StringUtils.isNoneBlank(gGroupIds)) {
                            for (String gGroupId : gGroupIds) {
                                //拼接 所有目标关联组 的所有 productId
                                //找到所有可以切换的组 商品
                                List<GroupLink> groupLinkList = iGroupLinkMapper.queryGroupLinkByGidAndGoodsId(gGroupId, product.getGoodsId());
                                for (GroupLink groupLink : groupLinkList) {
                                    if (groupLink.getGoodsId().equals(product.getGoodsId())
                                            && groupLinkList.size() == groupLinkList.size()) { //可切换性0:可切换；1不可切换
                                        if (groupLink.getProductId().equals(product.getId())) {// 找出当前product 所在关联组id 和 是否能切换
                                            goodsDTO.setSrcGroupId(groupLink.getGroupId());
                                            goodsDTO.setIsSwitch(groupLink.getIsSwitch());
                                            LOG.info("原关联组为:" + groupLink + " gropuId:" + groupLink.getGroupId());
                                        }
                                        if (groupLink.getIsSwitch() == 0) {//保存可以切换的 product
                                            if (!pIdTargetGroupSet.contains(groupLink.getProductId()))
                                                groupLinkTargetList.add(groupLink);//保存 所有可切换的 关联组 下的不同 productId
                                            pIdTargetGroupSet.add(groupLink.getProductId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (goodsDTO.getSrcGroupId() != null)
                        LOG.info("goodsDetail SrcGroupId:" + iGoodsGroupMapper.selectByPrimaryKey(goodsDTO.getSrcGroupId()));
                    LOG.info("pIdTargetGroupSet size:" + pIdTargetGroupSet.size() + "  " + pIdTargetGroupSet + "  " + groupLinkTargetList);
                }
            }


            List<BrandDTO> brandDTOList = new ArrayList<BrandDTO>();//品牌
//            //根据商品分类id关联所有价格属性
//            List<Attribute> goodsAttributeList = attributeMapper.queryPriceAttribute(goods.getCategoryId());
            Set<String> brandSet = new HashSet();
            Set<String> brandSeriesSet = new HashSet();

            if (srcGoodsGroup != null) {//是关联组
                for (String pId : pIdTargetGroupSet) {
                    Product pt = productMapper.selectByPrimaryKey(pId);
                    if (StringUtils.isNoneBlank(pt.getBrandId())
                            && StringUtils.isNoneBlank(pt.getBrandSeriesId())) {
                        brandSet.add(pt.getBrandId());//添加品牌
                        brandSeriesSet.add(pt.getBrandSeriesId());//添加品牌系列
                    }
                }
            } else {
                //该商品关联所有品牌系列
//                if (apiType == 1) {
////                    BrandSeries brandSeries = iBrandSeriesMapper.brandSeriesByPid(product.getId());
//                    brandSet.add(product.getBrandId());//添加品牌
//                    brandSeriesSet.add(product.getBrandSeriesId());//添加品牌系列
//                } else if (apiType == 2 || apiType == 3) {
                List<BrandSeries> brandSeriesList = iBrandSeriesMapper.queryBrandByGid(goods.getId());
                for (BrandSeries brandSeries : brandSeriesList) {//循环品牌系列
                    List<Product> productList = productMapper.queryByGoodsIdAndbrandSeriesIdAndBrandId(goods.getId(), brandSeries.getBrandId(), brandSeries.getId());
                    if (productList.size() > 0) {
                        brandSet.add(brandSeries.getBrandId());//添加品牌
                        brandSeriesSet.add(brandSeries.getId());//添加品牌系列
                    }
                }
//                }
            }

            if (brandSeriesSet.size() == 0 || brandSet.size() == 0) {//如果没有品牌，就只遍历属性
                List<Product> productList = new ArrayList<>();
                if (srcGoodsGroup != null) {//是关联组
                    for (String pId : pIdTargetGroupSet) {
                        //如果没有品牌，就只遍历属性
                        Product pt = productMapper.selectByPrimaryKey(pId);
                        if (StringUtils.isNoneBlank(pt.getAttributeIdArr())
                                && StringUtils.isNoneBlank(pt.getValueIdArr())) {
                            productList.add(pt);
                        }
                    }
                } else {
//                    if (apiType == 1) {
//                        productList.add(product);
//                    } else if (apiType == 2 || apiType == 3) {
                    productList = productMapper.getPListByGoodsIdAndNullBrandId(product.getGoodsId());
//                    }
//                    }
                    List<AttributeDTO> attrList = getAllAttributes(null, null, product, productList, imageList);
//                    LOG.info(" attrList :" + attrList);
                    goodsDTO.setAttrList(attrList);
                }

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
//                LOG.info(" brandDTO:" + brandDTO.getBrandId() + " name:" + brandDTO.getName());
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

                        //查询 一个 goods 的 某个 系列的 所有 product
                        //        List<Product> productList = productMapper.queryByGoodsIdAndbrandSeriesId(product.getGoodsId(), brandSeries.getId());
                        List<Product> productList = new ArrayList<>();
                        if (srcGoodsGroup != null) { //是关联组
                            for (String pId : pIdTargetGroupSet) {
                                Product p = productMapper.selectByPrimaryKey(pId);
                                if (brandSeries.getId().equals(p.getBrandSeriesId()))//某个系列的
                                    productList.add(p);
                            }
                        } else {
                            //            if (apiType == 1)
                            //                productList.add(product);
                            //            else if (apiType == 2 || apiType == 3)
                            productList = productMapper.queryByGoodsIdAndbrandSeriesId(product.getGoodsId(), brandSeries.getId());
                        }

                        List<AttributeDTO> attributeDTOList = getAllAttributes(brandSeries.getBrandId(), brandSeries.getId(), product, productList, imageList);
//                        LOG.info(" brandSeries:" + brandSeries.getName());
//                        LOG.info(" attributeDTOList :" + attributeDTOList);
                        brandSeriesDTO.setAttributeDTOList(attributeDTOList);


//                        if (attributeDTOList.size() > 0)
                        brandSeriesDTOList.add(brandSeriesDTO);
                    }
                }

                brandDTO.setBrandSeriesDTOList(brandSeriesDTOList);
                brandDTOList.add(brandDTO);
            }


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
                for (int i = 0; i < imageArr.length; i++) {
                    imageArr[i] = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + imageArr[i];
                }
                return StringUtils.join(imageArr, ",");
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
    public ServerResponse confirmActuaryDetail(String userToken, String houseId, String workerTypeId,
                                               int type, String cityId) {
        try {
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(workerTypeId);
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            } else {
                return ServerResponse.createByErrorMessage("查询工序精算失败");
            }
            Map<Integer, String> mapgx = new HashMap<>();
            mapgx.put(DjConstants.GXType.RENGGONG, "人工");
            mapgx.put(DjConstants.GXType.CAILIAO, "材料");
            mapgx.put(DjConstants.GXType.FUWU, "服务");
            mapgx.put(DjConstants.GXType.BU_RENGGONG, "补人工");
            mapgx.put(DjConstants.GXType.BU_CAILIAO, "补材料");
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
                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", workerGoods.getPrice()) + "/" + workerGoods.getUnitName());
                    flowActuaryDTO.setTotalPrice(workerGoods.getPrice() * bw.getShopCount());
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
                Double workerPrice = budgetWorkerMapper.getBudgetWorkerPrice(houseId, workerTypeId);//精算工钱
                flowDTO.setSumTotal(new BigDecimal(workerPrice));//合计
            } else if (type == DjConstants.GXType.BU_RENGGONG) {
                MendOrderInfoDTO mendOrderInfoDTO = mendOrderAPI.getMendMendOrderInfo(houseId, workerTypeId, "1", "");
                List<MendWorker> budgetWorkerList = mendOrderInfoDTO.getMendWorkers();
                for (MendWorker bw : budgetWorkerList) {

                    BudgetWorker budgetWorker = budgetWorkerMapper.byWorkerGoodsId(houseId, bw.getWorkerGoodsId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setName(bw.getWorkerGoodsName());
                    flowActuaryDTO.setImage(bw.getImage());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setShopCount(bw.getShopCount());
//                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
//                            String.format(DjConstants.YZPageAddress.COMMO, userToken, cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + budgetWorker.getId() + "&type=1";
//                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", bw.getPrice()) + "/" + bw.getUnitName());
                    flowActuaryDTO.setTotalPrice(bw.getPrice() * bw.getShopCount());
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
                flowDTO.setSumTotal(new BigDecimal(mendOrderInfoDTO.getTotalAmount()));//合计
            } else if (type == DjConstants.GXType.BU_CAILIAO) {
                MendOrderInfoDTO mendOrderInfoDTO = mendOrderAPI.getMendMendOrderInfo(houseId, workerTypeId, "0", "");
                List<MendMateriel> budgetMaterielList = mendOrderInfoDTO.getMendMateriels();
                for (MendMateriel bw : budgetMaterielList) {
                    Product product = productMapper.selectByPrimaryKey(bw.getProductId());
                    BudgetMaterial budgetMaterial = budgetMaterialMapper.getBudgetCaiListByGoodsId(houseId, workerTypeId, product.getGoodsId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + bw.getImage());
//                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken,
//                            cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + budgetMaterial.getId() + "&type=2";
//                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setAttribute(getAttributes(product));//拼接属性品牌
//                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + product.getUnitName());

                    String convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();
                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
                    flowActuaryDTO.setTotalPrice(bw.getTotalPrice());
                    flowActuaryDTO.setShopCount(bw.getShopCount());
//                    flowActuaryDTO.setConvertCount(Math.ceil(bw.getShopCount() / product.getConvertQuality()));
                    Double converCount = Math.ceil(bw.getShopCount() / product.getConvertQuality());
                    flowActuaryDTO.setConvertCount(converCount.intValue());
                    flowActuaryDTO.setBuy(0);
                    flowActuaryDTO.setBudgetMaterialId(bw.getId());
                    flowActuaryDTO.setName(bw.getProductName());
//                    flowActuaryDTO.setUnitName(bw.getUnitName());
                    flowActuaryDTO.setUnitName(convertUnitName);
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
                flowDTO.setSumTotal(new BigDecimal(mendOrderInfoDTO.getTotalAmount()));//合计
            } else {
                List<BudgetMaterial> budgetMaterialList = null;
                if (type == DjConstants.GXType.CAILIAO) {
                    budgetMaterialList = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                    Double caiPrice = budgetMaterialMapper.getBudgetCaiPrice(houseId, workerTypeId);
                    if (caiPrice == null) {
                        caiPrice = 0.0;
                    }
                    flowDTO.setSumTotal(new BigDecimal(caiPrice));//合计
                }
                if (type == DjConstants.GXType.FUWU) {
                    budgetMaterialList = budgetMaterialMapper.getBudgetSerList(houseId, workerTypeId);
                    Double serPrice = budgetMaterialMapper.getBudgetSerPrice(houseId, workerTypeId);
                    if (serPrice == null) {
                        serPrice = 0.0;
                    }
                    flowDTO.setSumTotal(new BigDecimal(serPrice));//合计
                }
                for (BudgetMaterial bm : budgetMaterialList) {
                    Goods goods = goodsMapper.selectByPrimaryKey(bm.getGoodsId());
                    Product product = productMapper.selectByPrimaryKey(bm.getProductId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setTypeName(typsValue);

                    String convertUnitName = bm.getUnitName();
                    if (product != null) {
                        flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + product.getImage());
                        String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken,
                                cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + bm.getId() + "&type=" + type;
                        flowActuaryDTO.setUrl(url);
                        flowActuaryDTO.setAttribute(getAttributes(product));//拼接属性品牌
//                        flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + product.getUnitName());

                        convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();

//                        flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName());
                        flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
                        flowActuaryDTO.setTotalPrice(product.getPrice() * bm.getConvertCount());
                    }
                    flowActuaryDTO.setShopCount(bm.getShopCount());
                    flowActuaryDTO.setConvertCount(bm.getConvertCount());
                    flowActuaryDTO.setBudgetMaterialId(bm.getId());
                    flowActuaryDTO.setName(bm.getGoodsName());
//                    flowActuaryDTO.setUnitName(bm.getUnitName());
                    flowActuaryDTO.setUnitName(convertUnitName);

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
        } catch (Exception e) {
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
    private List<AttributeDTO> getAllAttributes(String selectBrandId, String selectBrandSeriesId, Product product, List<Product> productList, List<String> imageList) {
//        List<String> imageList = new ArrayList<String>();//长图片 多图组合
        List<AttributeDTO> attributeDTOList = new ArrayList<>();

        Set<String> attributeIdSet = new HashSet<String>();
        for (Product pt : productList) { //查所有属性id
            String strAttributeIdArr = pt.getAttributeIdArr();
            if (StringUtils.isNoneBlank(strAttributeIdArr)) {
                String[] strAtIdArr = strAttributeIdArr.split(",");
                if (StringUtils.isNoneBlank(strAtIdArr)) {
                    for (String atId : strAtIdArr) {
                        attributeIdSet.add(atId);
                    }
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
                            if (attributeValueSet.contains(attributeValue.getId()))//去重
                                break;
//                            avDTO.setIsSwitch(1);//默认不可切换
                            attributeValueSet.add(attributeValue.getId());
                            AttributeValueDTO avDTO = new AttributeValueDTO();
                            avDTO.setAttributeValueId(attributeValue.getId());
                            avDTO.setName(attributeValue.getName());

                            if (isContainsValue(attributeValue.getId(), product.getValueIdArr())) {//如果包含该属性
                                avDTO.setState(1);//选中

                                if (StringUtils.isNoneBlank(product.getBrandId())
                                        && StringUtils.isNoneBlank(product.getBrandSeriesId())) {//有品牌和系列
                                    if (product.getBrandId().equals(selectBrandId)
                                            && product.getBrandSeriesId().equals(selectBrandSeriesId)) { //当前选中的品牌和系列对应的属性图
                                        if (StringUtils.isNoneBlank(attributeValue.getImage())) {
                                            imageList.add(getImage(attributeValue.getImage()));//属性图
                                        }
                                    }
                                } else {//没有品牌和系列
                                    if (StringUtils.isNoneBlank(attributeValue.getImage())) {
                                        imageList.add(getImage(attributeValue.getImage()));//属性图
                                    }
                                }
                            } else {
                                avDTO.setState(0);//未选中
                            }

                            attributeValueDTOList.add(avDTO);//添加属性值
                        }
                        j++;
                    }
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
