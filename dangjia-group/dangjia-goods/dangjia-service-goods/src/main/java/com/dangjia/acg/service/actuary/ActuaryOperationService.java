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
import com.dangjia.acg.modle.brand.Unit;
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
                            Double converCount = (srcBudgetMaterial.getShopCount() / targetProduct.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(targetProduct.getConvertUnit());
                            if(convertUnit.getType()==1){
                                converCount=Math.ceil(converCount);
                            }
                            srcBudgetMaterial.setConvertCount(converCount);
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
                Double converCount = (budgetMaterial.getShopCount() / product.getConvertQuality());
                Unit convertUnit = iUnitMapper.selectByPrimaryKey(product.getConvertUnit());
                if(convertUnit.getType()==1){
                    converCount=Math.ceil(converCount);
                }
                budgetMaterial.setConvertCount(converCount);
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
     * @param selectVal
     * @param attributeIdArr 属性值id集合
     */
    public ServerResponse selectProduct(String goodsId, String selectVal, String attributeIdArr, String budgetMaterialId) {
        try {
            if (!StringUtils.isNoneBlank(goodsId))
                return ServerResponse.createByErrorMessage("goodsId 不能为null");
//            if (!StringUtils.isNoneBlank(budgetMaterialId))
//                return ServerResponse.createByErrorMessage("budgetMaterialId 不能为null");

            String[] valueIdArr = attributeIdArr.split(",");
            Example example = new Example(Product.class);
            Example.Criteria criteria = example.createCriteria();
            if (!CommonUtil.isEmpty(goodsId)) {
                criteria.andEqualTo(Product.GOODS_ID, goodsId);
            }

            if (valueIdArr == null || valueIdArr.length == 0 || CommonUtil.isEmpty(attributeIdArr)) {
                criteria.andCondition(" (isnull(value_id_arr) or value_id_arr = '') ");
            } else {
                for (String val : valueIdArr) {
                    if(!CommonUtil.isEmpty(val)) {
                        criteria.andCondition("  CONCAT(IFNULL(brand_id,''),',',IFNULL(brand_series_id,''),',',IFNULL(value_id_arr,'')) LIKE '%" + val + "%' ");
                    }
                }
            }
            List<Product> products = productMapper.selectByExample(example);

            Product product = products.get(0);
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
            if (type == 1||type == 4) {//人工
                WorkerGoods workerGoods;
                if(type == 1) {
                    BudgetWorker budgetWorker = budgetWorkerMapper.selectByPrimaryKey(gId);
                    workerGoods = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());//人工商品
                }else{
                    workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
                }
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
            } else if (type == 2 || type == 3 || type == 5 ) {//材料商品  服务商品
                Product product;
                String budgetMaterialId=null;
                if(type != 5) {
                    BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(gId);
                    product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());//当前 货品
                    budgetMaterialId=budgetMaterial.getId();
                }else{
                    product = productMapper.selectByPrimaryKey(gId);//当前 货品
                }
                GoodsDTO goodsDTO = goodsDetail(product, budgetMaterialId);
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
            if (type == 1||type == 4) {//人工
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
            } else if (type == 2 || type == 3|| type == 5) {//材料商品  服务商品
                Product product = productMapper.selectByPrimaryKey(gId);//当前 货品
                GoodsDTO goodsDTO = goodsDetail(product, null);
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
     * @return GoodsDTO
     */
    public GoodsDTO goodsDetail(Product product, String budgetMaterialId) {
        try {
            LOG.info("goodsDetail product:" + product);
            LOG.info("goodsDetail productId:" + product.getId());
            LOG.info("goodsDetail budgetMaterialId:" + budgetMaterialId);
            GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());//当前 商品
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setMaket(product.getMaket());
            goodsDTO.setImage(getImage(product.getImage()));//图一张
            String convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();
            goodsDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
            goodsDTO.setName(product.getName());
            goodsDTO.setUnitName(convertUnitName);//单位
            goodsDTO.setProductType(goods.getType());//材料类型

            List<String> imageList = new ArrayList<String>();//长图片 多图组合
            GoodsGroup srcGoodsGroup = null;
            //找到一个groupId 的可以切换的目标关联组
            List<GroupLink> groupLinkTargetList = new ArrayList<>();//可以切换的其他关联组的 GroupLink的productId
            Set<String> pIdTargetGroupSet = new HashSet();//目标关联组下的所有productId
            pIdTargetGroupSet.add(product.getId());
            BudgetMaterial budgetMaterial = null;
            if (budgetMaterialId != null) {
                budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            }
            if (budgetMaterial != null) {
               //有精算的时候，才有可能 有关联组的处理
                if (StringUtils.isNoneBlank(budgetMaterial.getGoodsGroupId())) {
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
                LOG.info("pIdTargetGroupSet size:" + pIdTargetGroupSet.size() + "  " + pIdTargetGroupSet + "  " + groupLinkTargetList);
            }


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
                Example example =new Example(Product.class);
                example.createCriteria().andEqualTo(Product.GOODS_ID,goods.getId());
                example.orderBy(Product.VALUE_ID_ARR);
                productList = productMapper.selectByExample(example);
            }
            List<AttributeDTO> attrList = getAllAttributes( product, productList, imageList);
            goodsDTO.setAttrList(attrList);
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
        if(CommonUtil.isEmpty(value)){
            return true;
        }
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
            if(type != 5 && type !=4){
                ServerResponse response = workerTypeAPI.getWorkerType(workerTypeId);
                if (response.isSuccess()) {
                    workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
                } else {
                    return ServerResponse.createByErrorMessage("查询工序精算失败");
                }
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
                MendOrderInfoDTO mendOrderInfoDTO = mendOrderAPI.getMendDetail(workerTypeId, "1");
                List<MendWorker> budgetWorkerList = mendOrderInfoDTO.getMendWorkers();
                for (MendWorker bw : budgetWorkerList) {
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setName(bw.getWorkerGoodsName());
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + bw.getImage());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setShopCount(bw.getShopCount());
//                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
//                            String.format(DjConstants.YZPageAddress.COMMO, userToken, cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + budgetWorker.getId() + "&type=1";
//                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", bw.getPrice()) + "/" + bw.getUnitName());
                    flowActuaryDTO.setTotalPrice(bw.getPrice() * bw.getShopCount());
                    flowActuaryDTO.setBudgetMaterialId(bw.getWorkerGoodsId());
                    flowActuaryDTOList.add(flowActuaryDTO);
                }
                flowDTO.setSumTotal(new BigDecimal(mendOrderInfoDTO.getTotalAmount()));//合计
            } else if (type == DjConstants.GXType.BU_CAILIAO) {
                MendOrderInfoDTO mendOrderInfoDTO = mendOrderAPI.getMendDetail(workerTypeId, "0");
                List<MendMateriel> budgetMaterielList = mendOrderInfoDTO.getMendMateriels();
                for (MendMateriel mendMateriel : budgetMaterielList) {
                    Product product = productMapper.selectByPrimaryKey(mendMateriel.getProductId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + mendMateriel.getImage());
//                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken,
////                            cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + budgetMaterial.getId() + "&type=2";
////                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setAttribute(getAttributes(product));//拼接属性品牌

                    Unit convertUnit = iUnitMapper.selectByPrimaryKey(product.getConvertUnit());
                    flowActuaryDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnit.getName());
                    flowActuaryDTO.setTotalPrice(mendMateriel.getTotalPrice());
                    flowActuaryDTO.setShopCount(mendMateriel.getShopCount());
                    flowActuaryDTO.setConvertCount(mendMateriel.getShopCount());
                    flowActuaryDTO.setBuy(0);
                    flowActuaryDTO.setBudgetMaterialId(mendMateriel.getProductId());
                    flowActuaryDTO.setName(mendMateriel.getProductName());
                    flowActuaryDTO.setUnitName(convertUnit.getName());
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
                        String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMODITY, userToken,
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
                    if(CommonUtil.isEmpty(flowActuaryDTO.getName())){
                        flowActuaryDTO.setName(bm.getProductName());
                    }
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
        String attributes = product.getValueNameArr();
        if(attributes==null){
            attributes="";
        }
        BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(product.getBrandSeriesId());
        if(brandSeries!=null) {
            attributes = attributes + " " + brandSeries.getName();
        }
        if(CommonUtil.isEmpty(attributes)){
            return "无";
        }
        return attributes.replaceAll(","," ");
    }

    private String listToStr(String brandId,String brandSeriesId,String valueIdArr){
        List list=new ArrayList();
        if(!CommonUtil.isEmpty(brandId)){list.add(brandId);}
        if(!CommonUtil.isEmpty(brandSeriesId)){list.add(brandSeriesId);}
        if(!CommonUtil.isEmpty(valueIdArr)){list.add(valueIdArr);}
        return StringUtils.join(list,",");
    }
    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes1(Product product, List<Product> productList, List<String> imageList) {

        Map<String,List> productsMaps=new HashMap<>();//商品组合临时集合
        //装置货品下所有商品
        for (Product product1 : productList) {
            //品牌集合
            if(!CommonUtil.isEmpty(product1.getBrandId())) {

                List brandList = productsMaps.get(product1.getBrandId());
                if (brandList == null || brandList.size() == 0) {
                    brandList = new ArrayList();
                }
                brandList.add(listToStr(product1.getBrandId(), product1.getBrandSeriesId(), product1.getValueIdArr()));
                productsMaps.put(product1.getBrandId(), brandList);
            }
            //系列集合
            if(!CommonUtil.isEmpty(product1.getBrandSeriesId())) {
                List brandSeriesList = productsMaps.get(product1.getBrandSeriesId());
                if (brandSeriesList == null || brandSeriesList.size() == 0) {
                    brandSeriesList = new ArrayList();
                }
                brandSeriesList.add(listToStr(product1.getBrandId(), product1.getBrandSeriesId(), product1.getValueIdArr()));
                productsMaps.put(product1.getBrandSeriesId(), brandSeriesList);

            }
            if(!CommonUtil.isEmpty(product1.getValueIdArr())) {
                String[] strVIs = product1.getValueIdArr().split(",");
                for (String strVI : strVIs) {
                    //属性集合
                    List valueIdArr = productsMaps.get(strVI);
                    if (valueIdArr == null || valueIdArr.size() == 0) {
                        valueIdArr = new ArrayList();
                    }

                    valueIdArr.add(listToStr(product1.getBrandId(), product1.getBrandSeriesId(), product1.getValueIdArr()));
                    productsMaps.put(strVI, valueIdArr);
                }
            }
        }

        List<AttributeDTO> attributeDTOList = new ArrayList<>();

        Set<String> attributeIdSet = new HashSet<String>();
        Set<String> brandIdIdSet = new HashSet<String>();
        for (Product pt : productList) { //查所有属性id
            String brandIdArr = pt.getBrandId();
            if (!CommonUtil.isEmpty(pt.getBrandId())) {
                brandIdIdSet.add(brandIdArr);
            }
        }
        String strAttributeIdArr = product.getAttributeIdArr();
        if (StringUtils.isNoneBlank(strAttributeIdArr)) {
            String[] strAtIdArr = strAttributeIdArr.split(",");
            if (StringUtils.isNoneBlank(strAtIdArr)) {
                for (String atId : strAtIdArr) {
                    attributeIdSet.add(atId);
                }
            }
        }
        //品牌
        if(brandIdIdSet.size()>0) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("0");
            attributeDTO.setName("品牌");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            for (String atId : brandIdIdSet) {
                //属性 id
                Brand brand = iBrandMapper.selectByPrimaryKey(atId);
                if(brand!=null){
                    AttributeValueDTO avDTO = new AttributeValueDTO();
                    avDTO.setAttributeValueId(brand.getId());
                    avDTO.setName(brand.getName());

                    if (isContainsValue(brand.getId(), product.getBrandId())) {//如果包含该属性
                        avDTO.setState(1);//选中
                    } else {
                        avDTO.setState(0);//未选中

                        List<String> valueIdArr=  productsMaps.get(brand.getId());
                        if(valueIdArr==null||valueIdArr.size()==0){
                            avDTO.setState(2);//不能选中
                        }else {
                            boolean isExist = false;
                            for (Product product1 : productList) {
                                String attributeVal = listToStr(brand.getId(),product1.getBrandSeriesId() , product1.getValueIdArr());
                                for (String s : valueIdArr) {
                                    if (s.equals(attributeVal)) {
                                        isExist = true;
                                        break;
                                    }
                                }
                            }
                            if (!isExist) {
                                avDTO.setState(2);//不能选中
                            }
                        }
                    }
                    attributeValueDTOList.add(avDTO);//添加属性值
                }
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }
        //系列
        if(!CommonUtil.isEmpty(product.getBrandId())) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("1");
            attributeDTO.setName("系列");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            List<BrandSeries>  brandSeries=iBrandSeriesMapper.queryBrandSeries(product.getBrandId());
            for (BrandSeries brandSerie : brandSeries) {
                //属性 id
                if(brandSerie!=null){
                    boolean isShow=false;
                    for (Product product1 : productList) {
                        if(brandSerie.getId().equals(product1.getBrandSeriesId())){
                            isShow=true;
                        }
                    }
                    if(isShow) {
                        AttributeValueDTO avDTO = new AttributeValueDTO();
                        avDTO.setAttributeValueId(brandSerie.getId());
                        avDTO.setName(brandSerie.getName());
                        if (isContainsValue(brandSerie.getId(), product.getBrandSeriesId())) {//如果包含该属性
                            avDTO.setState(1);//选中
                            if (StringUtils.isNoneBlank(brandSerie.getImage())) {
                                imageList.add(getImage(brandSerie.getImage()));//属性图
                            }
                        } else {
                            avDTO.setState(0);//未选中

                            List<String> valueIdArr = productsMaps.get(brandSerie.getId());
                            if (valueIdArr == null || valueIdArr.size() == 0) {
                                avDTO.setState(2);//不能选中
                            } else {
                                boolean isExist = false;
                                String attributeVal = listToStr(product.getBrandId(),brandSerie.getId() , product.getValueIdArr());
                                for (String s : valueIdArr) {
                                    if (s.equals(attributeVal)) {
                                        isExist = true;
                                        break;
                                    }
                                }
                                if (!isExist) {
                                    avDTO.setState(2);//不能选中
                                }
                            }
                        }
                        attributeValueDTOList.add(avDTO);//添加属性值
                    }
                }
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }
        //价格属性
        for (String atId : attributeIdSet) {
            //属性 id
            Attribute attribute = iAttributeMapper.selectByPrimaryKey(atId);
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId(attribute.getId());
            attributeDTO.setName(attribute.getName());
            LOG.info("attributeDTO name:" + attributeDTO.getName());
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            List<AttributeValue> strVIs=iAttributeValueMapper.queryByAttributeId(atId);
            if (strVIs.size()>0) {
                for (AttributeValue attributeValue : strVIs) {
                    AttributeValueDTO avDTO = new AttributeValueDTO();
                    avDTO.setAttributeValueId(attributeValue.getId());
                    avDTO.setName(attributeValue.getName());

                    if (isContainsValue(attributeValue.getId(), product.getValueIdArr())) {//如果包含该属性
                        avDTO.setState(1);//选中

                        if (StringUtils.isNoneBlank(attributeValue.getImage())) {
                            imageList.add(getImage(attributeValue.getImage()));//属性图
                        }
                    } else {
                        avDTO.setState(0);//未选中

                        List<String> valueIdArr=  productsMaps.get(attributeValue.getId());
                        if(valueIdArr==null||valueIdArr.size()==0){
                            avDTO.setState(2);//不能选中
                        }else {
                            boolean isExist = true;
                            for (String s : valueIdArr) {
                                if (!isContainsValue(product.getBrandId(), s) ||
                                       !isContainsValue(product.getBrandSeriesId(), s) ||
                                        !isContainsValue(attributeValue.getId(), s)) {
                                    isExist = false;
                                    break;
                                }
                            }
                            if (!isExist) {
                                avDTO.setState(2);//不能选中
                            }
                        }
                    }
                    attributeValueDTOList.add(avDTO);//添加属性值
                }
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }

        return attributeDTOList;
    }
    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(Product product, List<Product> productList, List<String> imageList) {

        List<AttributeDTO> attributeDTOList = new ArrayList<>();

        //品牌
        if(productList.size()>0) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("0");
            attributeDTO.setName("规格");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            for (Product atId : productList) {
                StringBuffer strbuf=new StringBuffer();
                if(!CommonUtil.isEmpty(atId.getBrandId())) {
                    Brand brand = iBrandMapper.selectByPrimaryKey(atId.getBrandId());
                    strbuf.append(brand.getName()+" ");

                }
                if(!CommonUtil.isEmpty(atId.getBrandSeriesId())) {
                    BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(atId.getBrandSeriesId());
                    strbuf.append(brandSeries.getName()+" ");

                    if (atId.getId().equals(product.getId())) {//如果包含该属性
                        if (StringUtils.isNoneBlank(brandSeries.getImage())) {
                            imageList.add(getImage(brandSeries.getImage()));//属性图
                        }
                    }
                }
                if(!CommonUtil.isEmpty(atId.getValueIdArr())) {
                    strbuf.append(atId.getValueNameArr().replaceAll(","," "));

                    if (atId.getId().equals(product.getId())) {//如果包含该属性
                        String[] strAtIdArr = atId.getValueIdArr().split(",");
                        if (StringUtils.isNoneBlank(strAtIdArr)) {
                            for (String atValId : strAtIdArr) {
                                AttributeValue strVIs=iAttributeValueMapper.selectByPrimaryKey(atValId);
                                if (strVIs!=null&&StringUtils.isNoneBlank(strVIs.getImage())) {
                                    imageList.add(getImage(strVIs.getImage()));//属性图
                                }
                            }
                        }
                    }
                }
                AttributeValueDTO avDTO = new AttributeValueDTO();
                avDTO.setAttributeValueId(atId.getBrandId()+","+atId.getBrandSeriesId()+","+atId.getValueIdArr());
                avDTO.setName(strbuf.toString().trim());
                if (atId.getId().equals(product.getId())) {//如果包含该属性
                    avDTO.setState(1);//选中
                } else {
                    avDTO.setState(0);//未选中
                }
                attributeValueDTOList.add(avDTO);//添加属性值
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
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
