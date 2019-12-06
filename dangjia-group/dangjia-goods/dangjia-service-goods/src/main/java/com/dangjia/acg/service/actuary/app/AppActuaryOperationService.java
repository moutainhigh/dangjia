package com.dangjia.acg.service.actuary.app;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.AttributeDTO;
import com.dangjia.acg.dto.actuary.AttributeValueDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.mapper.sup.IShopMapper;
import com.dangjia.acg.mapper.sup.IShopProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.GoodsGroup;
import com.dangjia.acg.modle.basics.GroupLink;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.storefront.StorefrontProductAddedRelation;
import com.dangjia.acg.modle.sup.Shop;
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
    private IGoodsWorkerTypeMapper iGoodsWorkerTypeMapper;
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
    private IProductAddedRelationMapper iProductAddedRelationMapper;
    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;


    @Autowired
    private IGoodsDeliverOrderAddedProductMapper goodsDeliverOrderAddedProductMapper;
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
                example=new Example(DeliverOrderAddedProduct.class);
                example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,budgetMaterial.getId()).andEqualTo(DeliverOrderAddedProduct.SOURCE,"5");
                goodsDeliverOrderAddedProductMapper.deleteByExample(example);
                BasicsGoods goods = goodsMapper.selectByPrimaryKey(budgetMaterial.getGoodsId());
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
                    changeProduct(budgetMaterial.getOriginalProductId(), null,budgetMaterial.getId(), houseId, budgetMaterial.getWorkerTypeId());
                }
            }
        }

        return ServerResponse.createBySuccessMessage("操作成功" );
    }
    /**
     * 更换货品
     */
    public ServerResponse changeProduct(String productId,String addedProductIds, String budgetMaterialId,
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
                            StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(targetGroupLink.getProductId());
                            DjBasicsProductTemplate targetProduct = iBasicsProductTemplateMapper.selectByPrimaryKey(product.getProdTemplateId());//目标product 对象

                            newBudgetMaterial.setProductId(product.getId());
                            newBudgetMaterial.setProductSn(targetProduct.getProductSn());
                            newBudgetMaterial.setProductName(product.getProductName());
                            newBudgetMaterial.setImage(product.getImage());
                            newBudgetMaterial.setPrice(product.getSellPrice());
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
                            newBudgetMaterial.setTotalPrice(product.getSellPrice() * newBudgetMaterial.getConvertCount());

                            newBudgetMaterial.setCategoryId(targetProduct.getCategoryId());
                            newBudgetMaterial.setImage(product.getImage());
                            newBudgetMaterial.setUnitName(convertUnit.getName());
                            BasicsGoods goods = goodsMapper.selectByPrimaryKey( targetProduct.getGoodsId());
                            newBudgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
                            budgetMaterialMapper.updateByPrimaryKeySelective(newBudgetMaterial);
                            //添加增值商品
                            setAddedProduct(newBudgetMaterial.getId(),addedProductIds,"5");
                        }

                    }
                }
                return ServerResponse.createBySuccessMessage("更换成功，相关商品连带更换" );
            } else {

                BudgetMaterial newBudgetMaterial = budgetMaterialMapper.selectByPrimaryKey(budgetMaterialId);
                StorefrontProduct shopProduct = iShopProductMapper.selectByPrimaryKey(productId);
                DjBasicsProductTemplate product = iBasicsProductTemplateMapper.selectByPrimaryKey(shopProduct.getProdTemplateId());//目标product 对象

                newBudgetMaterial.setProductId(productId);
                newBudgetMaterial.setProductSn(product.getProductSn());
                newBudgetMaterial.setProductName(shopProduct.getProductName());
                newBudgetMaterial.setPrice(shopProduct.getSellPrice());
                newBudgetMaterial.setCost(product.getCost());
                //这里会更新 为 新product的 换算后的购买数量
                double converCount = (newBudgetMaterial.getShopCount() / product.getConvertQuality());
                Unit convertUnit = iUnitMapper.selectByPrimaryKey(product.getConvertUnit());
                if (convertUnit.getType() == 1) {
                    converCount = Math.ceil(converCount);
                }
                newBudgetMaterial.setConvertCount(converCount);
                newBudgetMaterial.setTotalPrice(shopProduct.getSellPrice() * newBudgetMaterial.getConvertCount());

                newBudgetMaterial.setCategoryId(product.getCategoryId());
                newBudgetMaterial.setImage(shopProduct.getImage());
                newBudgetMaterial.setUnitName(convertUnit.getName());
                BasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
                newBudgetMaterial.setProductType(goods.getType());//0：材料；1：包工包料
                budgetMaterialMapper.updateByPrimaryKeySelective(newBudgetMaterial);

                //添加增值商品
                setAddedProduct(newBudgetMaterial.getId(),addedProductIds,"5");
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
    public ServerResponse getCommo(String productId,String budgetMaterialId) {
        try {
                StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(productId);//目标product 对象
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

            BasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            ActuarialProductAppDTO goodsDTO =assembleGoodsResult(product,goods);
            //如果商品为0：材料；1：服务
            GoodsGroup srcGoodsGroup = null;
            List<String> pIdTargetGroupSet = new ArrayList<>();
            if(goods.getType()==1 || goods.getType()==0) {
                //找到一个groupId 的可以切换的目标关联组
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
            List<AttributeDTO> attrList = getAllAttributes(product, productList,goods);
            goodsDTO.setAttrList(attrList);
            return goodsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param productId 商品ID，
     * @return AttributeDTO
     */
    public ServerResponse getAttributeData(String productId) {
        try {
            StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(productId);//目标product 对象
            if(product == null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "该商品已禁用！");
            }
            BasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            ActuarialProductAppDTO goodsDTO =assembleGoodsInfo(product,goods);
            List<DjBasicsProductTemplate>  productList= iBasicsProductTemplateMapper.getProductTempListByStorefontId(product.getStorefrontId(),goods.getId());
            List<AttributeDTO> attrList = getAllAttributes(product, productList,goods);
            goodsDTO.setAttrList(attrList);
            return ServerResponse.createBySuccess("查询成功", attrList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取规格异常"+e.getMessage());
        }
    }
    public ActuarialProductAppDTO assembleGoodsInfo(StorefrontProduct product, BasicsGoods goods ) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        try {
            ActuarialProductAppDTO goodsDTO = new ActuarialProductAppDTO();
            DjBasicsProductTemplate productTemplate = iBasicsProductTemplateMapper.selectByPrimaryKey(product.getProdTemplateId());//目标product 对象
            BasicsGoodsCategory goodsCategory= iBasicsGoodsCategoryMapper.selectByPrimaryKey(goods.getCategoryId());

            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setProductTemplateId(productTemplate.getId());
            if(goodsCategory!=null) {
                goodsDTO.setCategoryId(goodsCategory.getId());
                goodsDTO.setPurchaseRestrictions(goodsCategory.getPurchaseRestrictions());
            }
            goodsDTO.setUnit(productTemplate.getUnitId());
            goodsDTO.setIsCalculatedArea("0");
            goodsDTO.setShopCount(0d);//购买总数 (精算的时候，用户手动填写的购买数量， 该单位是 product 的convertUnit换算单位 )
            goodsDTO.setConvertCount(0d);
            goodsDTO.setValueIdArr(productTemplate.getValueIdArr());


            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setProductTemplateId(productTemplate.getId());
            goodsDTO.setUnit(productTemplate.getUnitId());
            goodsDTO.setIsCalculatedArea("0");
            goodsDTO.setShopCount(0d);//购买总数 (精算的时候，用户手动填写的购买数量， 该单位是 product 的convertUnit换算单位 )
            goodsDTO.setConvertCount(0d);
            goodsDTO.setValueIdArr(productTemplate.getValueIdArr());
            goodsDTO.setValueNameArr(productTemplate.getValueNameArr());
            goodsDTO.setBrandId(goods.getBrandId());
            if(!CommonUtil.isEmpty(goods.getBrandId())){
                Brand brand=iBrandMapper.selectByPrimaryKey(goods.getBrandId());
                goodsDTO.setBrandName(brand.getName());
                if(!CommonUtil.isEmpty(goodsDTO.getValueNameArr())) {
                    goodsDTO.setValueNameArr(goodsDTO.getBrandName()+" "+productTemplate.getValueNameArr());
                }
            }

            if(!CommonUtil.isEmpty(goodsDTO.getValueNameArr())) {
                goodsDTO.setValueNameArr(goodsDTO.getValueNameArr().replaceAll(",", " "));
            }else{
                goodsDTO.setValueNameArr(productTemplate.getName());
            }
            goodsDTO.setConvertQuality(productTemplate.getConvertQuality());
            goodsDTO.setConvertUnit(productTemplate.getConvertUnit());
            goodsDTO.setSales(goods.getSales());
            goodsDTO.setIrreversibleReasons(goods.getIrreversibleReasons());
            goodsDTO.setIsShelfStatus(product.getIsShelfStatus());
            goodsDTO.setProductName(product.getProductName());
            goodsDTO.setGoodsType(goods.getType());//材料类型
            goodsDTO.setMarketingName(product.getMarketName());//营销名称
            goodsDTO.setIsInflueWarrantyPeriod(productTemplate.getIsInflueWarrantyPeriod());//是否影响质保年限（1是，0否）
            goodsDTO.setRefundPolicy(productTemplate.getRefundPolicy());//退款政策
            goodsDTO.setGuaranteedPolicy(productTemplate.getGuaranteedPolicy());//保修政策
            goodsDTO.setProductId(product.getId());
            goodsDTO.setGoodsId(goods.getId());
            goodsDTO.setImage(product.getImage());//图多张
            goodsDTO.setImageUrl(StringTool.getImage(product.getImage(),imageAddress));//图多张
            goodsDTO.setImageSingle(StringTool.getImageSingle(product.getImage(),imageAddress));//图一张
            goodsDTO.setPrice(new BigDecimal(product.getSellPrice()));
            goodsDTO.setProductSn(productTemplate.getProductSn());
            goodsDTO.setUnit(productTemplate.getConvertUnit());
            goodsDTO.setOtherName(productTemplate.getOtherName());
            goodsDTO.setWorkerTypeId(productTemplate.getWorkerTypeId());

            //当前时间小于调价的时间时则展示调价预告信息
            if(product.getAdjustedPrice()!=null&&product.getModityPriceTime().getTime()>(new Date()).getTime()) {
                goodsDTO.setLastPrice(product.getAdjustedPrice());
                goodsDTO.setLastTime(product.getModityPriceTime());
            }
            //查询单位
            String unitId=goodsDTO.getUnit();
            //查询单位
            if(goodsDTO.getConvertQuality()!=null&&goodsDTO.getConvertQuality()>0){
                unitId=goodsDTO.getConvertUnit();
            }
            if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                Unit unit= iUnitMapper.selectByPrimaryKey(unitId);
                goodsDTO.setUnitName(unit!=null?unit.getName():"");
                goodsDTO.setUnitType(unit.getType());
            }


            //人工相关
            goodsDTO.setWorkerDec(StringTool.getImage(productTemplate.getWorkerDec(),imageAddress));
            goodsDTO.setWorkerDecUrl(productTemplate.getWorkerDec());
            goodsDTO.setUnit(productTemplate.getConvertUnit());
            goodsDTO.setOtherName(productTemplate.getOtherName());
            goodsDTO.setWorkExplain(productTemplate.getWorkExplain());
            goodsDTO.setWorkerStandard(productTemplate.getWorkerStandard());
            goodsDTO.setLastPrice(productTemplate.getLastPrice());
            goodsDTO.setLastTime(productTemplate.getLastTime());
            goodsDTO.setTechnologyIds(productTemplate.getTechnologyIds());
            goodsDTO.setConsiderations(productTemplate.getConsiderations());
            goodsDTO.setCalculateContent(productTemplate.getCalculateContent());
            goodsDTO.setBuildContent(productTemplate.getBuildContent());

            return goodsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ActuarialProductAppDTO assembleGoodsResult(StorefrontProduct product, BasicsGoods goods ) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        try {
            //获取基本信息
            ActuarialProductAppDTO goodsDTO = assembleGoodsInfo(product,goods);
            Storefront storefront = iShopMapper.selectByPrimaryKey(product.getStorefrontId());

            Shop shop = new Shop();
            BeanUtils.beanToBean(storefront,shop);
            shop.setStorefrontLogo(StringTool.getImage(shop.getStorefrontLogo(),imageAddress));
            shop.setSystemLogo(StringTool.getImage(shop.getSystemLogo(),imageAddress));
            goodsDTO.setShop(shop);
            goodsDTO.setStorefrontId(shop.getId());
            goodsDTO.setStorefrontName(shop.getStorefrontName());
            goodsDTO.setStorefrontIcon(shop.getSystemLogo());

            goodsDTO.setDetailImage(StringTool.getImage(product.getDetailImage(),imageAddress));//图多张

            if(!CommonUtil.isEmpty(goodsDTO.getWorkerTypeId())) {
                WorkerType workerType = iGoodsWorkerTypeMapper.selectByPrimaryKey(goodsDTO.getWorkerTypeId());
                goodsDTO.setWorkerTypeName(workerType.getName());
            }

            //将工艺列表返回
            List<TechnologyDTO> technologies = new ArrayList<>();
            if(!CommonUtil.isEmpty(goodsDTO.getTechnologyIds())) {
                List<Technology> technologyList = iTechnologyMapper.queryTechnologyList(goodsDTO.getTechnologyIds());
                for (Technology technology : technologyList) {
                    TechnologyDTO technologyResult = new TechnologyDTO();
                    technologyResult.setId(technology.getId());
                    technologyResult.setName(technology.getName());
                    technologyResult.setWorkerTypeId(technology.getWorkerTypeId());
                    technologyResult.setContent(technology.getContent());
                    technologyResult.setImage(StringTool.getImage(technology.getImage(), imageAddress));
                    technologyResult.setImageUrl(technology.getImage());
                    technologyResult.setSampleImage(technology.getSampleImage());
                    technologyResult.setSampleImageUrl(StringTool.getImage(technology.getSampleImage(), imageAddress));
                    technologyResult.setType(technology.getType());
                    technologyResult.setCreateDate(DateUtils.timedate(String.valueOf(technology.getCreateDate().getTime())));
                    technologyResult.setModifyDate(DateUtils.timedate(String.valueOf(technology.getModifyDate().getTime())));
                    technologies.add(technologyResult);
                }
                goodsDTO.setTechnologies(technologies);
            }
            return goodsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(StorefrontProduct product, List<DjBasicsProductTemplate> productList,BasicsGoods goods) {
        List<AttributeDTO> attributeDTOList = new ArrayList<>();
        //品牌
        if (productList.size() > 0) {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("0");
            attributeDTO.setName("规格");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            Brand brand =null;
            if (!CommonUtil.isEmpty(goods.getBrandId())) {
                 brand = iBrandMapper.selectByPrimaryKey(goods.getBrandId());
            }
            for (DjBasicsProductTemplate atId : productList) {
                StringBuilder strbuf = new StringBuilder();
                if (brand != null) {
                    strbuf.append(brand.getName()).append(" ");
                }
                if (!CommonUtil.isEmpty(atId.getValueNameArr())) {
                    strbuf.append(atId.getValueNameArr().replaceAll(",", " "));
                }else{
                    strbuf.append(atId.getName());
                }
                AttributeValueDTO avDTO = new AttributeValueDTO();
                avDTO.setAttributeValueId(atId.getId());
                avDTO.setName(strbuf.toString().trim());
                if (atId.getId().equals(product.getId())) {//如果包含该属性
                    avDTO.setState(1);//选中
                } else {
                    avDTO.setState(0);//未选中
                }
                avDTO.setType(0);
                attributeValueDTOList.add(avDTO);//添加属性值
            }
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }
        List<StorefrontProductAddedRelation> list =  iProductAddedRelationMapper.getAddedrelationGoodsData(product.getId());
        if(list.size()>0){
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setId("1");
            attributeDTO.setName("增值服务");
            List<AttributeValueDTO> attributeValueDTOList = new ArrayList<>();
            for (StorefrontProductAddedRelation atId : list) {
                AttributeValueDTO avDTO = new AttributeValueDTO();
                avDTO.setAttributeValueId(atId.getAddedProductId());
                avDTO.setName(atId.getAddedProductName());
                avDTO.setState(0);//未选中
                avDTO.setType(1);
                attributeValueDTOList.add(avDTO);//添加属性值
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
            BasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            Brand brand =null;
            if (!CommonUtil.isEmpty(goods.getBrandId())) {
                brand = iBrandMapper.selectByPrimaryKey(goods.getBrandId());
            }
            if (brand!=null) {
                strbuf.append(brand.getName()).append(" ");
            }
            if (!CommonUtil.isEmpty(product.getValueNameArr())) {
                strbuf.append(product.getValueNameArr().replaceAll(",", " "));
            }
        }
        return strbuf.toString().trim();
    }
    /**
     *  更新/设置增值商品
     * @param orderId 来源订单ID
     * @param addedProductIds 增值商品 多个以逗号分隔
     * @param source 来源类型
     */
    private void setAddedProduct(String orderId,String addedProductIds,String source){
        if(!CommonUtil.isEmpty(orderId)) {
            Example example=new Example(DeliverOrderAddedProduct.class);
            example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,orderId).andEqualTo(DeliverOrderAddedProduct.SOURCE,source);
            goodsDeliverOrderAddedProductMapper.deleteByExample(example);
            if(!CommonUtil.isEmpty(addedProductIds)) {
                String[] addedProductIdList = addedProductIds.split(",");
                for (String addedProductId : addedProductIdList) {
                    StorefrontProduct product = iShopProductMapper.selectByPrimaryKey(addedProductId);
                    DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                    deliverOrderAddedProduct1.setAnyOrderId(orderId);
                    deliverOrderAddedProduct1.setAddedProductId(addedProductId);
                    deliverOrderAddedProduct1.setPrice(product.getSellPrice());
                    deliverOrderAddedProduct1.setProductName(product.getProductName());
                    deliverOrderAddedProduct1.setSource(source);
                    goodsDeliverOrderAddedProductMapper.insert(deliverOrderAddedProduct1);
                }
            }
        }
    }
}
