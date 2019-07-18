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
    private ITechnologyMapper technologyMapper;
    @Autowired
    private IBrandSeriesMapper iBrandSeriesMapper;
    @Autowired
    private IBrandMapper iBrandMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
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
    public ServerResponse changeProduct(String productId, String budgetMaterialId,
                                        String srcGroupId, String targetGroupId,
                                        String houseId, String workerTypeId) {
        try {
            int count = 0;
            String ret = productId + " " + budgetMaterialId + " " + srcGroupId + " " + targetGroupId + " " + houseId + " " + workerTypeId;
            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            if (StringUtils.isNotBlank(targetGroupId) && StringUtils.isNoneBlank(srcGroupId))//不为空  可以切换
            {
                //找到 原关联组的goods成员， 把 goods 下的product 更换 成 目标关联组的 goods下的product
                List<GroupLink> srcGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(srcGroupId);
                List<GroupLink> targetGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(targetGroupId);
                Set<String> allNoPayProductIds = new HashSet<>();//所有未支付的product 是单品的
                Set<String> danProductIds = new HashSet<>();//所有未支付的product 是单品的 或者是 其他关联组的商品，都不参与切换
                //未支付的材料1
                List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                for (BudgetMaterial budgetMaterial1 : budgetMaterials) {
                    if (budgetMaterial.getGoodsGroupId().equals(srcGroupId)) {
                        allNoPayProductIds.add(budgetMaterial1.getProductId());
                    }
                    if (!StringUtils.isNoneBlank(budgetMaterial1.getGoodsGroupId())) {
                        danProductIds.add(budgetMaterial1.getProductId());
                    }
                    //精算时，原关联组里的商品，如果被其他关联组的替换了，也不能更换
                    for (GroupLink groupLink : srcGroupLinkLists) {
                        if (budgetMaterial1.getProductId().equals(groupLink.getProductId())
                                && !budgetMaterial1.getGoodsGroupId().equals(groupLink.getGroupId())) {
                            danProductIds.add(budgetMaterial1.getProductId());
                        }
                    }

                }
                for (GroupLink srcGroupLink : srcGroupLinkLists) {
                    for (GroupLink targetGroupLink : targetGroupLinkLists) {
                        //原关联组的对应的goodsId 和 目标 关联组 goodsId 一样时，进行更换 product
                        if (srcGroupLink.getGoodsId().equals(targetGroupLink.getGoodsId())
                                && allNoPayProductIds.contains(srcGroupLink.getProductId())) {// 必须属于 未购买里的是 原关联组 和 目标关联组里包含的。
                            if (danProductIds.contains(srcGroupLink.getProductId())) {//如果是单品，就不换
                                continue;
                            }
                            //查到 老的关联组 的精算
                            BudgetMaterial srcBudgetMaterial = budgetMaterialMapper.getBudgetCaiListByGoodsId(houseId, workerTypeId, srcGroupLink.getGoodsId());
                            Product targetProduct = productMapper.selectByPrimaryKey(targetGroupLink.getProductId());//目标product 对象
                            srcBudgetMaterial.setProductId(targetProduct.getId());
                            srcBudgetMaterial.setProductSn(targetProduct.getProductSn());
                            srcBudgetMaterial.setProductName(targetProduct.getName());
                            srcBudgetMaterial.setImage(targetProduct.getImage());
                            srcBudgetMaterial.setPrice(targetProduct.getPrice());
                            srcBudgetMaterial.setGoodsGroupId(targetGroupId);
                            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(targetGroupId);
                            srcBudgetMaterial.setGroupType(goodsGroup.getName());
                            srcBudgetMaterial.setCost(targetProduct.getCost());
                            //这里会更新 为 新product的 换算后的购买数量
//                            srcBudgetMaterial.setConvertCount(Math.ceil(srcBudgetMaterial.getShopCount() / targetProduct.getConvertQuality()));
                            double converCount = (srcBudgetMaterial.getShopCount() / targetProduct.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(targetProduct.getConvertUnit());
                            if (convertUnit.getType() == 1) {
                                converCount = Math.ceil(converCount);
                            }
                            srcBudgetMaterial.setConvertCount(converCount);
                            srcBudgetMaterial.setTotalPrice(targetProduct.getPrice() * srcBudgetMaterial.getConvertCount());

                            srcBudgetMaterial.setCategoryId(targetProduct.getCategoryId());
                            srcBudgetMaterial.setImage(targetProduct.getImage());
                            srcBudgetMaterial.setUnitName(convertUnit.getName());
                            Goods goods = goodsMapper.queryById( targetProduct.getGoodsId());
                            srcBudgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
                            budgetMaterialMapper.updateByPrimaryKey(srcBudgetMaterial);
                            count++;
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
                //这里会更新 为 新product的 换算后的购买数量
                double converCount = (budgetMaterial.getShopCount() / product.getConvertQuality());
                Unit convertUnit = iUnitMapper.selectByPrimaryKey(product.getConvertUnit());
                if (convertUnit.getType() == 1) {
                    converCount = Math.ceil(converCount);
                }
                budgetMaterial.setConvertCount(converCount);
                budgetMaterial.setTotalPrice(product.getPrice() * budgetMaterial.getConvertCount());

                budgetMaterial.setCategoryId(product.getCategoryId());
                budgetMaterial.setImage(product.getImage());
                budgetMaterial.setUnitName(convertUnit.getName());
                Goods goods = goodsMapper.queryById( product.getGoodsId());
                budgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
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
     * @param attributeIdArr 属性值id集合
     */
    public ServerResponse selectProduct(String goodsId, String selectVal, String attributeIdArr, String budgetMaterialId) {
        try {
            if (!StringUtils.isNoneBlank(goodsId))
                return ServerResponse.createByErrorMessage("goodsId 不能为null");
            Product product = productMapper.selectByPrimaryKey(selectVal);
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
            if (type == 1 || type == 4) {//人工
                WorkerGoods workerGoods;
                if (type == 1) {
                    BudgetWorker budgetWorker = budgetWorkerMapper.selectByPrimaryKey(gId);
                    workerGoods = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());//人工商品
                } else {
                    workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
                }
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                wGoodsDTO.setPrice("¥" + String.format("%.2f", workerGoods.getPrice()) + "/" + workerGoods.getUnitName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(getImage(workerGoods.getWorkerDec()));
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getTechnologyIds());
                for (Technology technology : technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功", wGoodsDTO);
            } else if (type == 2 || type == 3 || type == 5) {//材料商品  包工包料商品
                Product product;
                String budgetMaterialId = null;
                if (type != 5) {
                    BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(gId);
                    if (budgetMaterial != null) {
                        product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());//当前 货品
                        budgetMaterialId = budgetMaterial.getId();
                    } else {
                        product = productMapper.selectByPrimaryKey(gId);//当前 货品
                    }
                } else {
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
    public ServerResponse getGoodsDetail(String gId, int type) {
        try {
            if (type == 1 || type == 4) {//人工
                WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
                WGoodsDTO wGoodsDTO = new WGoodsDTO();
                if (!CommonUtil.isEmpty(workerGoods.getImage())) {
                    wGoodsDTO.setImage(getImage(workerGoods.getImage()));
                }
                wGoodsDTO.setPrice("¥" + String.format("%.2f", workerGoods.getPrice()) + "/" + workerGoods.getUnitName());
                wGoodsDTO.setName(workerGoods.getName());
                wGoodsDTO.setWorkerDec(getImage(workerGoods.getWorkerDec()));
                List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(workerGoods.getTechnologyIds());
                for (Technology technology : technologyList) {
                    technology.setImage(getImage(technology.getImage()));//图一张
                }
                wGoodsDTO.setTechnologyList(technologyList);
                return ServerResponse.createBySuccess("查询成功", wGoodsDTO);
            } else if (type == 2 || type == 3 || type == 5) {//材料商品  包工包料商品
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

    /**
     * @param budgetMaterialId 传null ：表示不是精算里的商品。 如果是精算里的商品 ，可能有 关联组，关联组id 在 精算表里存的，所以，需要传精算id  ，
     * @return GoodsDTO
     */
    public GoodsDTO goodsDetail(Product product, String budgetMaterialId) {
        try {
            GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
            Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());//当前 商品
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setMaket(1);
            if (product.getMaket() == 0 || product.getType() == 0) {
                goodsDTO.setMaket(0);
            }
            goodsDTO.setImage(getImage(product.getImage()));//图一张
            String convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();
            goodsDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
            goodsDTO.setName(product.getName());
            goodsDTO.setUnitName(convertUnitName);//单位
            goodsDTO.setProductType(goods.getType());//材料类型

            List<String> imageList = new ArrayList<>();//长图片 多图组合
            GoodsGroup srcGoodsGroup = null;
            //找到一个groupId 的可以切换的目标关联组
            Set<String> pIdTargetGroupSet = new HashSet<>();//目标关联组下的所有productId
            pIdTargetGroupSet.add(product.getId());
            BudgetMaterial budgetMaterial = null;
            if (budgetMaterialId != null) {
                budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            }
            if (budgetMaterial != null) {
                //有精算的时候，才有可能 有关联组的处理
                if (StringUtils.isNoneBlank(budgetMaterial.getGoodsGroupId())) {
                    srcGoodsGroup = iGoodsGroupMapper.selectByPrimaryKey(budgetMaterial.getGoodsGroupId());
                    goodsDTO.setIsSwitch(1);
                    String[] gGroupIds = srcGoodsGroup.getSwitchArr().split(",");
                    if (StringUtils.isNoneBlank(gGroupIds)) {
                        for (String gGroupId : gGroupIds) {
                            //拼接 所有目标关联组 的所有 productId
                            //找到所有可以切换的组 商品
                            List<GroupLink> groupLinkList = iGroupLinkMapper.queryGroupLinkByGidAndGoodsId(gGroupId, product.getGoodsId());
                            for (GroupLink groupLink : groupLinkList) {
                                if (groupLink.getGoodsId().equals(product.getGoodsId())) { //可切换性0:可切换；1不可切换
                                    if (groupLink.getProductId().equals(product.getId())) {// 找出当前product 所在关联组id 和 是否能切换
                                        goodsDTO.setSrcGroupId(groupLink.getGroupId());
                                        goodsDTO.setIsSwitch(groupLink.getIsSwitch());
                                    }
                                    if (groupLink.getIsSwitch() == 0) {//保存可以切换的 product
                                        pIdTargetGroupSet.add(groupLink.getProductId());
                                    }
                                }
                            }
                        }
                    }
                }
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
                Example example = new Example(Product.class);
                example.createCriteria().andEqualTo(Product.GOODS_ID, goods.getId());
                example.orderBy(Product.VALUE_ID_ARR);
                productList = productMapper.selectByExample(example);
            }
            List<AttributeDTO> attrList = getAllAttributes(product, productList, imageList);
            goodsDTO.setAttrList(attrList);
            if (imageList.size() > 0) {
                String img = StringUtils.join(imageList, ",");
                imageList.remove(0);
                imageList.add(0, img);
            }
            goodsDTO.setImageList(imageList);

            return goodsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //取第一张图
    private String getImage(String images) {
        if (!CommonUtil.isEmpty(images)) {
            String[] imageArr = images.split(",");
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (int i = 0; i < imageArr.length; i++) {
                imageArr[i] = imageAddress + imageArr[i];
            }
            return StringUtils.join(imageArr, ",");
        }
        return "";//暂无图片
    }

    /**
     * 查看工序 type 人工1 材料2 包工包料3
     * 支付时精算goods详情 查最新价格 共用此方法
     */
    public ServerResponse confirmActuaryDetail(String userToken, String houseId, String workerTypeId,
                                               int type, String cityId) {
        try {
            String workerTypeName = "";
            if (type != 5 && type != 4) {
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
            mapgx.put(DjConstants.GXType.FUWU, "包工包料");
            mapgx.put(DjConstants.GXType.BU_RENGGONG, "补人工");
            mapgx.put(DjConstants.GXType.BU_CAILIAO, "补材料");
            FlowDTO flowDTO = new FlowDTO();
            flowDTO.setName(workerTypeName);
            flowDTO.setType(type);
            List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<>();
            String typsValue = mapgx.get(type);
            if (CommonUtil.isEmpty(typsValue)) {
                return ServerResponse.createByErrorMessage("type参数错误");
            }
            if (type == DjConstants.GXType.RENGGONG) {
                List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId, workerTypeId);
                for (BudgetWorker bw : budgetWorkerList) {
                    WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(bw.getWorkerGoodsId());
                    FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                    flowActuaryDTO.setBudgetMaterialId(bw.getId());
                    flowActuaryDTO.setId(workerGoods.getId());
                    flowActuaryDTO.setName(bw.getName());
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + workerGoods.getImage());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setType(type);
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
                    flowActuaryDTO.setId(bw.getWorkerGoodsId());
                    flowActuaryDTO.setName(bw.getWorkerGoodsName());
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + bw.getImage());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setType(type);
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
                    flowActuaryDTO.setId(product.getId());
                    flowActuaryDTO.setTypeName(typsValue);
                    flowActuaryDTO.setType(type);
                    flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + mendMateriel.getImage());
//                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.COMMO, userToken,
////                            cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + budgetMaterial.getId() + "&type=2";
////                    flowActuaryDTO.setUrl(url);
                    flowActuaryDTO.setAttribute(getAttributes(mendMateriel.getProductId()));//拼接属性品牌

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
                if (budgetMaterialList != null)
                    for (BudgetMaterial bm : budgetMaterialList) {
                        Goods goods = goodsMapper.selectByPrimaryKey(bm.getGoodsId());
                        Product product = productMapper.selectByPrimaryKey(bm.getProductId());
                        FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                        flowActuaryDTO.setTypeName(typsValue);
                        flowActuaryDTO.setType(type);
                        String convertUnitName = bm.getUnitName();
                        if (product != null) {
                            flowActuaryDTO.setId(product.getId());
                            flowActuaryDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + product.getImage());
                            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                                    String.format(DjConstants.YZPageAddress.COMMODITY, userToken,
                                            cityId, flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + bm.getId() + "&type=" + type;
                            flowActuaryDTO.setUrl(url);
                            flowActuaryDTO.setAttribute(getAttributes(product.getId()));//拼接属性品牌
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
                        if (CommonUtil.isEmpty(flowActuaryDTO.getName())) {
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
    public String getAttributes(String productId) {
        String attributes = iBrandSeriesMapper.getAttributesName(productId);
        if (CommonUtil.isEmpty(attributes)) {
            return "";
        }
        return attributes.replaceAll(",", " ");
    }

    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(Product product, List<Product> productList, List<String> imageList) {
        List<AttributeDTO> attributeDTOList = new ArrayList<>();
        //品牌
        if (productList.size() > 0) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("0");
            attributeDTO.setName("规格");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            for (Product atId : productList) {
                StringBuilder strbuf = new StringBuilder();
                if (!CommonUtil.isEmpty(atId.getBrandId())) {
                    Brand brand = iBrandMapper.selectByPrimaryKey(atId.getBrandId());
                    strbuf.append(brand.getName()).append(" ");
                }
                if (!CommonUtil.isEmpty(atId.getBrandSeriesId())) {
                    BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(atId.getBrandSeriesId());
                    strbuf.append(brandSeries.getName()).append(" ");
                    if (atId.getId().equals(product.getId())) {//如果包含该属性
                        if (!CommonUtil.isEmpty(brandSeries.getImage())) {
                            imageList.add(getImage(brandSeries.getImage()));//属性图
                        }
                    }
                }
                if (!CommonUtil.isEmpty(atId.getValueIdArr())) {
                    strbuf.append(atId.getValueNameArr().replaceAll(",", " "));
//                    if (atId.getId().equals(product.getId())) {//如果包含该属性
//                        String[] strAtIdArr = atId.getValueIdArr().split(",");
//                        for (String atValId : strAtIdArr) {
//                            AttributeValue strVIs = iAttributeValueMapper.selectByPrimaryKey(atValId);
//                            if (strVIs != null && !CommonUtil.isEmpty(strVIs.getImage())) {
//                                imageList.add(getImage(strVIs.getImage()));//属性图
//                            }
//                        }
//                    }
                }
                AttributeValueDTO avDTO = new AttributeValueDTO();
                avDTO.setAttributeValueId(atId.getId());
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
     * 精算详情 productType  0：材料；1：包工包料
     */
    public ServerResponse confirmActuary(String userToken, String houseId, String cityId) {
        //从master获取工序详情
        List<Map<String, String>> mapList = getForBudgetAPI.getFlowList(houseId);
        ActuaryDetailsDTO actuaryDetailsDTO = new ActuaryDetailsDTO();//最外层
        List<FlowDetailsDTO> flowDetailsDTOList = new ArrayList<>();
        for (Map<String, String> map : mapList) {
            String name = map.get("name");
            String workerTypeId = map.get("workerTypeId");
            FlowDetailsDTO flowDetailsDTO = new FlowDetailsDTO();
            flowDetailsDTO.setName(name);
            List<DetailsDTO> detailsDTOList = new ArrayList<>();//人工材料包工包料
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId, workerTypeId);//人工明细
            List<BudgetMaterial> materialCaiList = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);//材料明细
            List<BudgetMaterial> materialSerList = budgetMaterialMapper.getBudgetSerList(houseId, workerTypeId);//包工包料明细
            List<Map> mapworker = new ArrayList<>();
            Map<Integer, String> mapgx = new HashMap<>();
            mapgx.put(DjConstants.GXType.RENGGONG, "人工");
            mapgx.put(DjConstants.GXType.CAILIAO, "材料");
            mapgx.put(DjConstants.GXType.FUWU, "包工包料");
            for (Map.Entry<Integer, String> entry : mapgx.entrySet()) {
                Map<String, Object> m = new HashMap<>();
                m.put("key", String.valueOf(entry.getKey()));
                m.put("name", entry.getValue());
                int size = 0;
                if (DjConstants.GXType.RENGGONG.equals(entry.getKey())) {
                    size = budgetWorkerList.size();
                } else if (DjConstants.GXType.CAILIAO.equals(entry.getKey())) {
                    size = materialCaiList.size();
                } else if (DjConstants.GXType.FUWU.equals(entry.getKey())) {
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
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                            String.format(DjConstants.YZPageAddress.CONFIRMACTUARYDETAIL, userToken, cityId, names + "明细") +
                            "&houseId=" + houseId + "&workerTypeId=" + workerTypeId + "&type=" + key;
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
