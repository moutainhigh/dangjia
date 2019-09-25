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
import com.dangjia.acg.mapper.product.DjBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMaterialMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductWorkerMapper;
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
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.product.DjBasicsProductMaterial;
import com.dangjia.acg.modle.product.DjBasicsProductWorker;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import com.dangjia.acg.util.DateUtils;
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
    private DjBasicsProductMapper productMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IGroupLinkMapper iGroupLinkMapper;
    @Autowired
    private IGoodsGroupMapper iGoodsGroupMapper;
    @Autowired
    private IBrandMapper iBrandMapper;

    @Autowired
    private DjBasicsProductMaterialMapper djBasicsProductMaterialMapper;
    @Autowired
    private DjBasicsProductWorkerMapper djBasicsProductWorkerMapper;
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;

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
            if(!CommonUtil.isEmpty(budgetMaterial.getOriginalProductId())) {
                changeProduct(budgetMaterial.getOriginalProductId(),budgetMaterial.getId(),houseId,budgetMaterial.getWorkerTypeId());
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
                            DjBasicsProduct targetProduct = productMapper.selectByPrimaryKey(targetGroupLink.getProductId());//目标product 对象
                            DjBasicsProductMaterial targetProductMaterial = djBasicsProductMaterialMapper.queryProductMaterialByProductId(targetGroupLink.getProductId());//目标product 对象

                            newBudgetMaterial.setProductId(targetProduct.getId());
                            newBudgetMaterial.setProductSn(targetProduct.getProductSn());
                            newBudgetMaterial.setProductName(targetProduct.getName());
                            newBudgetMaterial.setImage(targetProduct.getImage());
                            newBudgetMaterial.setPrice(targetProduct.getPrice());
                            newBudgetMaterial.setGoodsGroupId(targetGroup.get(0).getGroupId());
                            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(targetGroup.get(0).getGroupId());
                            newBudgetMaterial.setGroupType(goodsGroup.getName());
                            newBudgetMaterial.setCost(targetProductMaterial.getCost());
                            //这里会更新 为 新product的 换算后的购买数量
//                            newBudgetMaterial.setConvertCount(Math.ceil(newBudgetMaterial.getShopCount() / targetProduct.getConvertQuality()));
                            double converCount = (newBudgetMaterial.getShopCount() / targetProductMaterial.getConvertQuality());
                            Unit convertUnit = iUnitMapper.selectByPrimaryKey(targetProductMaterial.getConvertUnit());
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
                DjBasicsProduct product = productMapper.selectByPrimaryKey(productId);//目标product 对象
                DjBasicsProductMaterial targetProductMaterial = djBasicsProductMaterialMapper.queryProductMaterialByProductId(productId);//目标product 对象

                newBudgetMaterial.setProductId(productId);
                newBudgetMaterial.setProductSn(product.getProductSn());
                newBudgetMaterial.setProductName(product.getName());
                newBudgetMaterial.setPrice(product.getPrice());
                newBudgetMaterial.setCost(targetProductMaterial.getCost());
                //这里会更新 为 新product的 换算后的购买数量
                double converCount = (newBudgetMaterial.getShopCount() / targetProductMaterial.getConvertQuality());
                Unit convertUnit = iUnitMapper.selectByPrimaryKey(targetProductMaterial.getConvertUnit());
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
            DjBasicsProduct product = productMapper.selectByPrimaryKey(selectVal);//目标product 对象
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
                DjBasicsProduct product = productMapper.selectByPrimaryKey(gId);//当前 货品
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
     * 商品详情
     * gId:  budgetWorkerId   budgetMaterialId
     */
    public Object getNewCommo(String gId,String budgetMaterialId) {
        try {
            DjBasicsProduct product = productMapper.selectByPrimaryKey(gId);//当前 货品
            if(product == null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "该商品已禁用！");
            }
            Object goodsDTO = goodsDetail(product, budgetMaterialId);
            if (goodsDTO != null) {
                return goodsDTO;
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
    public Object goodsDetail(DjBasicsProduct product, String budgetMaterialId) {
        try {
            DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
            //如果商品为0：材料；1：服务
            if(goods.getType()==1 || goods.getType()==0) {
                GoodsDTO goodsDTO = new GoodsDTO();//长图  品牌系列图+属性图(多个)
                goodsDTO.setSales(goods.getSales());
                goodsDTO.setIrreversibleReasons(goods.getIrreversibleReasons());
                DjBasicsProductMaterial targetProductMaterial = djBasicsProductMaterialMapper.queryProductMaterialByProductId(product.getId());//目标product 对象
                goodsDTO.setProductId(product.getId());
                goodsDTO.setGoodsId(goods.getId());
                goodsDTO.setMaket(1);
                if (product.getMaket() == 0 || product.getType() == 0) {
                    goodsDTO.setMaket(0);
                }
                goodsDTO.setImage(getImage(product.getImage()));//图一张
                String convertUnitName = iUnitMapper.selectByPrimaryKey(targetProductMaterial.getConvertUnit()).getName();
                goodsDTO.setPrice("¥" + String.format("%.2f", product.getPrice()) + "/" + convertUnitName);
                goodsDTO.setName(product.getName());
                goodsDTO.setUnitName(convertUnitName);//单位
                goodsDTO.setProductType(goods.getType());//材料类型

                List<String> imageList = new ArrayList<>();//长图片 多图组合
                imageList.add(getImage(targetProductMaterial.getDetailImage()));//属性图
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
                List<DjBasicsProduct> productList = new ArrayList<>();
                if (srcGoodsGroup != null) {//是关联组
                    for (String pId : pIdTargetGroupSet) {
                        DjBasicsProduct djBasicsProduct=djBasicsProductMapper.selectByPrimaryKey(pId);
                        //如果没有品牌，就只遍历属性
                        if (StringUtils.isNoneBlank(goods.getAttributeIdArr())
                                && StringUtils.isNoneBlank(djBasicsProduct.getValueIdArr())) {
                            DjBasicsProduct prot = productMapper.selectByPrimaryKey(pId);
                            productList.add(prot);
                        }
                    }
                } else {
                    Example example = new Example(Product.class);
                    example.createCriteria().andEqualTo(DjBasicsProduct.GOODS_ID, goods.getId()).andEqualTo(DjBasicsProduct.TYPE, "1");
                    example.orderBy(DjBasicsProduct.CATEGORY_ID);
                    productList = productMapper.selectByExample(example);
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
                WorkerGoodsDTO  workerGoodsDTO=assembleWorkerGoodsResult(product,goods);
                return workerGoodsDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public WorkerGoodsDTO assembleWorkerGoodsResult(DjBasicsProduct workerGoods,DjBasicsGoods goods) {
        try {
            DjBasicsProductWorker pt = djBasicsProductWorkerMapper.queryProductWorkerByProductId(workerGoods.getId());//目标product 对象
            WorkerGoodsDTO workerGoodsResult = new WorkerGoodsDTO();
            workerGoodsResult.setId(workerGoods.getId());
            workerGoodsResult.setProductType(goods.getType());//材料类型
            workerGoodsResult.setIrreversibleReasons(goods.getIrreversibleReasons());
            workerGoodsResult.setName(workerGoods.getName());
            workerGoodsResult.setWorkerGoodsSn(workerGoods.getProductSn());
            workerGoodsResult.setImage(getImage(workerGoods.getImage()));
            workerGoodsResult.setImageUrl(workerGoods.getImage());
            workerGoodsResult.setWorkerDec(getImage(pt.getWorkerDec()));
            workerGoodsResult.setWorkerDecUrl(pt.getWorkerDec());
            workerGoodsResult.setUnitId(workerGoods.getUnitId());
            workerGoodsResult.setUnitName(workerGoods.getUnitName());
            workerGoodsResult.setOtherName(workerGoods.getOtherName());
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(pt.getWorkerTypeId());
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            }
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setPrice(workerGoods.getPrice());
            workerGoodsResult.setIstops(goods.getIstop());
            workerGoodsResult.setSales(goods.getSales());
            workerGoodsResult.setWorkExplain(pt.getWorkExplain());
            workerGoodsResult.setWorkerStandard(pt.getWorkerStandard());
            workerGoodsResult.setWorkerTypeId(pt.getWorkerTypeId());
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setShowGoods(workerGoods.getMaket());

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
                technologyResult.setImage(getImage(technology.getImage()));
                technologyResult.setImageUrl(technology.getImage());
                technologyResult.setSampleImage(technology.getSampleImage());
                technologyResult.setSampleImageUrl(getImage(technology.getSampleImage()));
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

    //根据品牌系列找属性品牌
    private List<AttributeDTO> getAllAttributes(DjBasicsProduct product, List<DjBasicsProduct> productList) {
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
            for (DjBasicsProduct atId : productList) {
                 StringBuilder strbuf = new StringBuilder();
                if (brand!=null) {
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
            attributeDTO.setValueDTOList(attributeValueDTOList);
            attributeDTOList.add(attributeDTO);
        }
        return attributeDTOList;
    }


}
