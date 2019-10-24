package com.dangjia.acg.service.actuary.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.repair.MendOrderAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.dto.basics.WorkerGoodsDTO;
import com.dangjia.acg.dto.repair.MendOrderInfoDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.mapper.sup.IShopMapper;
import com.dangjia.acg.mapper.sup.IShopProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.GoodsGroup;
import com.dangjia.acg.modle.basics.GroupLink;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.product.*;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.util.DateUtils;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: qiyuxiang
 * Date: 2019/09/18
 */
@Service
public class AppActuaryOperationService {

    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private DjBasicsGoodsMapper goodsMapper;
    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
    @Autowired
    private IShopProductMapper iShopProductMapper;
    @Autowired
    private IShopMapper iShopMapper;

    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IGroupLinkMapper iGroupLinkMapper;
    @Autowired
    private IGoodsGroupMapper iGoodsGroupMapper;
    @Autowired
    private IBrandMapper iBrandMapper;

    @Autowired
    private ITechnologyMapper iTechnologyMapper;

    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;
    protected static final Logger LOG = LoggerFactory.getLogger(AppActuaryOperationService.class);

    /**
     * 取消精算
     * buy": 0必买；1可选选中；2自购; 3可选没选中(业主已取消)
     * <p>
     * 这里往精算表插入最新价格
     */
    public ServerResponse choiceGoods(String houseId,String productId) {
        try {
            Example example=new Example(BudgetMaterial.class);
            example.createCriteria()
                    .andEqualTo(BudgetMaterial.HOUSE_ID,houseId)
                    .andCondition("  FIND_IN_SET(product_id,'" + productId + "') ");
            List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.selectByExample(example);
            for (BudgetMaterial budgetMaterial : budgetMaterials) {
                DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(budgetMaterial.getGoodsId());
                if(goods.getBuy()==1) {//可选商品取消
                    budgetMaterial.setDeleteState(2);//取消
                    budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }
    /**
     * 恢复精算货品
     */
    public ServerResponse recoveryProduct(String houseId,String productId) {


        Example example=new Example(BudgetMaterial.class);
        example.createCriteria()
                .andEqualTo(BudgetMaterial.HOUSE_ID,houseId)
                .andCondition("  FIND_IN_SET(product_id,'" + productId + "') ");
        List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.selectByExample(example);
        for (BudgetMaterial budgetMaterial : budgetMaterials) {
            if(budgetMaterial.getDeleteState()==2){
                budgetMaterial.setDeleteState(0);
                budgetMaterialMapper.updateByPrimaryKey(budgetMaterial);
            }else {
                if (!CommonUtil.isEmpty(budgetMaterial.getOriginalProductId())) {
                    changeProduct(budgetMaterial.getOriginalProductId(), budgetMaterial.getId(), houseId, budgetMaterial.getWorkerTypeId());
                }
            }
        }

        return ServerResponse.createBySuccessMessage("操作成功" );
    }
    /**
     * 更换货品
     */
    public ServerResponse changeProduct(String productId, String budgetMaterialId,
                                        String houseId, String workerTypeId) {
        try {
            BudgetMaterial budgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
            List<GroupLink> srcGroup = iGroupLinkMapper.queryGroupLinkByPid(budgetMaterial.getProductId());
            List<GroupLink> targetGroup = iGroupLinkMapper.queryGroupLinkByPid(productId);
            if (srcGroup.size()>0 && targetGroup.size()>0)//不为空  可以切换
            {
                //找到 原关联组的goods成员， 把 goods 下的product 更换 成 目标关联组的 goods下的product
                List<GroupLink> srcGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(srcGroup.get(0).getGroupId());
                List<GroupLink> targetGroupLinkLists = iGroupLinkMapper.queryGroupLinkByGid(targetGroup.get(0).getGroupId());
                Set<String> allNoPayProductIds = new HashSet<>();//所有未支付的product 是单品的
                Set<String> danProductIds = new HashSet<>();//所有未支付的product 是单品的 或者是 其他关联组的商品，都不参与切换
                //未支付的材料1
                List<BudgetMaterial> budgetMaterials = budgetMaterialMapper.getBudgetCaiList(houseId, workerTypeId);
                for (BudgetMaterial budgetMaterial1 : budgetMaterials) {
                    if (budgetMaterial.getGoodsGroupId().equals(srcGroup.get(0).getGroupId())) {
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
                            BudgetMaterial newBudgetMaterial = budgetMaterialMapper.getBudgetCaiListByGoodsId(houseId, workerTypeId, srcGroupLink.getGoodsId());
                            DjBasicsProductTemplate targetProduct = iBasicsProductTemplateMapper.selectByPrimaryKey(targetGroupLink.getProductId());//目标product 对象

                            newBudgetMaterial.setProductId(targetProduct.getId());
                            newBudgetMaterial.setProductSn(targetProduct.getProductSn());
                            newBudgetMaterial.setProductName(targetProduct.getName());
                            newBudgetMaterial.setImage(targetProduct.getImage());
                            newBudgetMaterial.setPrice(targetProduct.getPrice());
                            newBudgetMaterial.setGoodsGroupId(targetGroup.get(0).getGroupId());
                            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(targetGroup.get(0).getGroupId());
                            newBudgetMaterial.setGroupType(goodsGroup.getName());
                            newBudgetMaterial.setCost(targetProduct.getCost());
                            //这里会更新 为 新product的 换算后的购买数量
//                            newBudgetMaterial.setConvertCount(Math.ceil(newBudgetMaterial.getShopCount() / targetProduct.getConvertQuality()));
                            double converCount = (newBudgetMaterial.getShopCount() / targetProduct.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(targetProduct.getConvertUnit());
                            if (convertUnit.getType() == 1) {
                                converCount = Math.ceil(converCount);
                            }
                            newBudgetMaterial.setConvertCount(converCount);
                            newBudgetMaterial.setTotalPrice(targetProduct.getPrice() * newBudgetMaterial.getConvertCount());

                            newBudgetMaterial.setCategoryId(targetProduct.getCategoryId());
                            newBudgetMaterial.setImage(targetProduct.getImage());
                            newBudgetMaterial.setUnitName(convertUnit.getName());
                            DjBasicsGoods goods = goodsMapper.selectByPrimaryKey( targetProduct.getGoodsId());
                            newBudgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
                            budgetMaterialMapper.updateByPrimaryKeySelective(newBudgetMaterial);
                        }

                    }
                }
                return ServerResponse.createBySuccessMessage("更换成功，相关商品连带更换" );
            } else {

                BudgetMaterial newBudgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
                DjBasicsProductTemplate product = iBasicsProductTemplateMapper.selectByPrimaryKey(productId);//目标product 对象

                newBudgetMaterial.setProductId(productId);
                newBudgetMaterial.setProductSn(product.getProductSn());
                newBudgetMaterial.setProductName(product.getName());
                newBudgetMaterial.setPrice(product.getPrice());
                newBudgetMaterial.setCost(product.getCost());
                //这里会更新 为 新product的 换算后的购买数量
                double converCount = (newBudgetMaterial.getShopCount() / product.getConvertQuality());
                Unit convertUnit = iUnitMapper.selectByPrimaryKey(product.getConvertUnit());
                if (convertUnit.getType() == 1) {
                    converCount = Math.ceil(converCount);
                }
                newBudgetMaterial.setConvertCount(converCount);
                newBudgetMaterial.setTotalPrice(product.getPrice() * newBudgetMaterial.getConvertCount());

                newBudgetMaterial.setCategoryId(product.getCategoryId());
                newBudgetMaterial.setImage(product.getImage());
                newBudgetMaterial.setUnitName(convertUnit.getName());
                DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
                newBudgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
                budgetMaterialMapper.updateByPrimaryKeySelective(newBudgetMaterial);
                return ServerResponse.createBySuccessMessage("更换成功" );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 选择货品刷新页面
     */
    public ServerResponse selectProduct(String goodsId, String selectVal, String budgetMaterialId) {
        try {

            StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(selectVal);//目标product 对象

            Object goodsDTO = goodsDetail(product, budgetMaterialId);
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
    public ServerResponse getCommo(String gId,String budgetMaterialId) {
        try {
                StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(gId);//目标product 对象
                if(product == null){
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "该商品已禁用！");
                }
                Object goodsDTO = goodsDetail(product, budgetMaterialId);
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
     * @param budgetMaterialId 传null ：表示不是精算里的商品。 如果是精算里的商品 ，可能有 关联组，关联组id 在 精算表里存的，所以，需要传精算id  ，
     * @return GoodsDTO
     */
    public Object goodsDetail(StorefrontProduct product, String budgetMaterialId) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            DjBasicsProductTemplate productTemplate = iBasicsProductTemplateMapper.selectByPrimaryKey(product.getProdTemplateId());//目标product 对象
            DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            BasicsGoodsCategory goodsCategory= iBasicsGoodsCategoryMapper.selectByPrimaryKey(goods.getCategoryId());
            Storefront storefront= iShopMapper.selectByPrimaryKey(product.getStorefrontId());

            //如果商品为0：材料；1：服务
            if(goods.getType()==1 || goods.getType()==0) {
                GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
                goodsDTO.setStorefront(storefront);
                goodsDTO.setPurchaseRestrictions(goodsCategory.getPurchaseRestrictions());
                goodsDTO.setSales(goods.getSales());
                goodsDTO.setIrreversibleReasons(goods.getIrreversibleReasons());
                goodsDTO.setProductId(product.getId());
                goodsDTO.setGoodsId(goods.getId());
                goodsDTO.setMaket(product.getIsShelfStatus());
                goodsDTO.setImage(StringTool.getImage(product.getImage(),imageAddress));//图一张
                String convertUnitName = iUnitMapper.selectByPrimaryKey(productTemplate.getConvertUnit()).getName();
                goodsDTO.setPrice(String.format("%.2f", product.getSellPrice()));
                goodsDTO.setName(product.getProductName());
                goodsDTO.setUnitName(convertUnitName);//单位
                goodsDTO.setProductType(goods.getType());//材料类型

                goodsDTO.setMarketingName(product.getMarketName());//营销名称
                goodsDTO.setIsInflueWarrantyPeriod(productTemplate.getIsInflueWarrantyPeriod());//是否影响质保年限（1是，0否）
                goodsDTO.setRefundPolicy(productTemplate.getRefundPolicy());//退款政策
                goodsDTO.setGuaranteedPolicy(productTemplate.getGuaranteedPolicy());//保修政策
                List<String> imageList = new ArrayList<>();//长图片 多图组合
                imageList.add(StringTool.getImage(product.getDetailImage(),imageAddress));//属性图
                GoodsGroup srcGoodsGroup = null;
                //找到一个groupId 的可以切换的目标关联组
                List<String> pIdTargetGroupSet = new ArrayList<>();
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
                List<DjBasicsProductTemplate> productList = new ArrayList<>();
                if (srcGoodsGroup != null) {//是关联组
                    for (String pId : pIdTargetGroupSet) {
                        DjBasicsProductTemplate djBasicsProduct=iBasicsProductTemplateMapper.getProductListByStoreproductId(pId);
                        //如果没有品牌，就只遍历属性
                        if (StringUtils.isNoneBlank(goods.getAttributeIdArr())
                                && StringUtils.isNoneBlank(djBasicsProduct.getValueIdArr())) {
                            productList.add(djBasicsProduct);
                        }
                    }
                } else {
                   productList=iBasicsProductTemplateMapper.getProductTempListByStorefontId(product.getStorefrontId(),goods.getId());
                }
                List<AttributeDTO> attrList = getAllAttributes(product, productList);
                goodsDTO.setAttrList(attrList);
                if (imageList.size() > 0) {
                    String img = StringUtils.join(imageList, ",");
                    imageList.remove(0);
                    imageList.add(0, img);
                }
                goodsDTO.setImageList(imageList);
                return goodsDTO;
            }else{
                WorkerGoodsDTO  workerGoodsDTO=assembleWorkerGoodsResult(product);
                workerGoodsDTO.setPurchaseRestrictions(goodsCategory.getPurchaseRestrictions());
                workerGoodsDTO.setProductType(goods.getType());//材料类型
                workerGoodsDTO.setIrreversibleReasons(goods.getIrreversibleReasons());
                workerGoodsDTO.setIstops(goods.getIstop());
                workerGoodsDTO.setSales(goods.getSales());
                workerGoodsDTO.setStorefront(storefront);
                List<DjBasicsProductTemplate> productList=iBasicsProductTemplateMapper.getProductTempListByStorefontId(product.getStorefrontId(),goods.getId());
                List<AttributeDTO> attrList = getAllAttributes(product, productList);
                workerGoodsDTO.setAttrList(attrList);

                return workerGoodsDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public WorkerGoodsDTO assembleWorkerGoodsResult(StorefrontProduct workerGoods) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        try {
            DjBasicsProductTemplate pt = iBasicsProductTemplateMapper.selectByPrimaryKey(workerGoods.getId());//目标product 对象
            WorkerGoodsDTO workerGoodsResult = new WorkerGoodsDTO();
            workerGoodsResult.setId(workerGoods.getId());
            workerGoodsResult.setName(workerGoods.getProductName());
            workerGoodsResult.setWorkerGoodsSn(pt.getProductSn());
            workerGoodsResult.setImage(StringTool.getImage(workerGoods.getImage(),imageAddress));
            workerGoodsResult.setImageUrl(workerGoods.getImage());
            workerGoodsResult.setWorkerDec(StringTool.getImage(pt.getWorkerDec(),imageAddress));
            workerGoodsResult.setWorkerDecUrl(pt.getWorkerDec());
            workerGoodsResult.setUnitId(pt.getUnitId());
            workerGoodsResult.setUnitName(pt.getUnitName());
            workerGoodsResult.setOtherName(pt.getOtherName());
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(pt.getWorkerTypeId());
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            }
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setPrice(workerGoods.getSellPrice());
            workerGoodsResult.setWorkExplain(pt.getWorkExplain());
            workerGoodsResult.setWorkerStandard(pt.getWorkerStandard());
            workerGoodsResult.setWorkerTypeId(pt.getWorkerTypeId());
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setShowGoods(workerGoods.getIsShelfStatus());

            workerGoodsResult.setLastPrice(pt.getLastPrice());
            workerGoodsResult.setLastTime(pt.getLastTime());
            workerGoodsResult.setTechnologyIds(pt.getTechnologyIds());
            workerGoodsResult.setConsiderations(pt.getConsiderations());
            workerGoodsResult.setCalculateContent(pt.getCalculateContent());
            workerGoodsResult.setBuildContent(pt.getBuildContent());

            //将工艺列表返回
            List<TechnologyDTO> technologies = new ArrayList<>();
            List<Technology> technologyList = iTechnologyMapper.queryTechnologyList(pt.getTechnologyIds());
            for (Technology technology : technologyList) {
                TechnologyDTO technologyResult = new TechnologyDTO();
                technologyResult.setId(technology.getId());
                technologyResult.setName(technology.getName());
                technologyResult.setWorkerTypeId(technology.getWorkerTypeId());
                technologyResult.setContent(technology.getContent());
                technologyResult.setImage(StringTool.getImage(technology.getImage(),imageAddress));
                technologyResult.setImageUrl(technology.getImage());
                technologyResult.setSampleImage(technology.getSampleImage());
                technologyResult.setSampleImageUrl(StringTool.getImage(technology.getSampleImage(),imageAddress));
                technologyResult.setType(technology.getType());
                technologyResult.setCreateDate(DateUtils.timedate(String.valueOf(technology.getCreateDate().getTime())));
                technologyResult.setModifyDate(DateUtils.timedate(String.valueOf(technology.getModifyDate().getTime())));
                technologies.add(technologyResult);
            }
            workerGoodsResult.setTechnologies(technologies);
            workerGoodsResult.setCreateDate(DateUtils.timedate(String.valueOf(workerGoods.getCreateDate().getTime())));
            workerGoodsResult.setModifyDate(DateUtils.timedate(String.valueOf(workerGoods.getModifyDate().getTime())));
            return workerGoodsResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(StorefrontProduct product, List<DjBasicsProductTemplate> productList) {
        List<AttributeDTO> attributeDTOList = new ArrayList<>();
        //品牌
        if (productList.size() > 0) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("0");
            attributeDTO.setName("规格");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            Brand brand =null;
            if (!CommonUtil.isEmpty(goods.getBrandId())) {
                 brand = iBrandMapper.selectByPrimaryKey(goods.getBrandId());
            }
            for (DjBasicsProductTemplate atId : productList) {
                if(atId.getType()==1) {
                    StringBuilder strbuf = new StringBuilder();
                    if (brand != null) {
                        strbuf.append(brand.getName()).append(" ");
                    }
                    if (!CommonUtil.isEmpty(atId.getValueIdArr())) {
                        strbuf.append(atId.getValueNameArr().replaceAll(",", " "));
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
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }
        return attributeDTOList;
    }

    //根据品牌系列找属性品牌
    public String getAttributeName(String productId) {
        StringBuilder strbuf = new StringBuilder();
        DjBasicsProductTemplate product = iBasicsProductTemplateMapper.selectByPrimaryKey(productId);//目标product 对象
        //品牌
        if (product!=null) {
            DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            Brand brand =null;
            if (!CommonUtil.isEmpty(goods.getBrandId())) {
                brand = iBrandMapper.selectByPrimaryKey(goods.getBrandId());
            }
            if (brand!=null) {
                strbuf.append(brand.getName()).append(" ");
            }
            if (!CommonUtil.isEmpty(product.getValueIdArr())) {
                strbuf.append(product.getValueNameArr().replaceAll(",", " "));
            }
        }
        return strbuf.toString().trim();
    }

}
