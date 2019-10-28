package com.dangjia.acg.service.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.BudgetLabelDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelGoodsDTO;
import com.dangjia.acg.dto.actuary.ShopGoodsDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.dto.product.StorefontInfoDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.mapper.sup.ISupplierMapper;
import com.dangjia.acg.mapper.sup.ISupplierProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.*;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.service.actuary.BudgetWorkerService;
import com.dangjia.acg.service.actuary.app.AppActuaryOperationService;
import com.dangjia.acg.service.actuary.app.SearchActuarialConfigServices;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/24 0024
 * Time: 11:47
 * 给master提供精算数据 修改精算数据
 */
@Service
public class ForMasterService {

    private static Logger logger = LoggerFactory.getLogger(ForMasterService.class);
    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private SearchActuarialConfigServices searchActuarialConfigServices;
    @Autowired
    private ITechnologyMapper technologyMapper;
    @Autowired
    private IBrandSeriesMapper brandSeriesMapper;
    @Autowired
    private ISupplierMapper supplierMapper;
    @Autowired
    private ISupplierProductMapper supplierProductMapper;
    @Autowired
    private IUnitMapper unitMapper;

    @Autowired
    private IBrandMapper iBrandMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
    @Autowired
    private DjBasicsGoodsMapper goodsMapper;
    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;
    @Autowired
    private BudgetWorkerService budgetWorkerService;

    public String getUnitName(String unitId){
        Unit unit = unitMapper.selectByPrimaryKey(unitId);
        return unit.getName();
    }


    public SupplierProduct getSupplierProduct(String supplierId,String productId){
        return supplierProductMapper.getSupplierProduct(supplierId,productId);
    }

    public Supplier getSupplier(String supplierId){
        return supplierMapper.selectByPrimaryKey(supplierId);
    }

    /**
     * 增加退数量
     */
    public void backCount (String houseId,String workerGoodsId,Double num){
        BudgetWorker budgetWorker = budgetWorkerMapper.byWorkerGoodsId(houseId,workerGoodsId);
        budgetWorker.setBackCount(budgetWorker.getBackCount() + num);
        budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
    }

    /**
     * 增加补数量
     */
    public void repairCount(String houseId,String workerGoodsId,Double num){
        BudgetWorker budgetWorker = budgetWorkerMapper.byWorkerGoodsId(houseId,workerGoodsId);
        budgetWorker.setRepairCount(budgetWorker.getRepairCount() + num);
        budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
    }

    public Technology byTechnologyId(String technologyId){
        return technologyMapper.selectByPrimaryKey(technologyId);
    }

    public String brandSeriesName(String productId){
        return brandSeriesMapper.brandSeriesName(productId);
    }
    public String brandName(String productId){
        return brandSeriesMapper.brandName(productId);
    }

    /**
     * 设置材料或者人工商品置顶或取消置顶
     * @param gid 商品ID
     * @param type 0=材料商品  1=人工商品
     */
    public void setProductOrWorkerGoodsIsTop(String gid,Integer type,String istop){
        DjBasicsProductTemplate djBasicsProduct= iBasicsProductTemplateMapper.selectByPrimaryKey(gid);
        if(djBasicsProduct!=null){
            djBasicsProduct.setIstop(Integer.parseInt(istop));
            iBasicsProductTemplateMapper.updateByPrimaryKeySelective(djBasicsProduct);
        }
        /*if(type==0){
            Product product= productMapper.selectByPrimaryKey(gid);
            if(product!=null){
                product.setIstop(istop);
                productMapper.updateByPrimaryKeySelective(product);
            }
        }else{
            WorkerGoods workerGoods= workerGoodsMapper.selectByPrimaryKey(gid);
            if(workerGoods!=null){
                workerGoods.setIstop(istop);
                workerGoodsMapper.updateByPrimaryKeySelective(workerGoods);
            }
        }*/
    }
    public ProductWorkerDTO getWorkerGoods(String workerGoodsId){
        DjBasicsProductTemplate djBasicsProduct = iBasicsProductTemplateMapper.selectByPrimaryKey(workerGoodsId);
        ProductWorkerDTO productWorkerDTO = JSON.parseObject(JSON.toJSONString(djBasicsProduct),new TypeReference<ProductWorkerDTO>() {});
         productWorkerDTO.setWorkerDec(djBasicsProduct.getWorkerDec());
        productWorkerDTO.setWorkerTypeId(djBasicsProduct.getWorkerTypeId());
        productWorkerDTO.setShowGoods(djBasicsProduct.getMaket());
        return productWorkerDTO;
    }
    public BasicsGoods getGoods(String goodsId){

        return iBasicsGoodsMapper.selectByPrimaryKey(goodsId);
    }
    public DjBasicsProductTemplate getProduct(String productId){
        return iBasicsProductTemplateMapper.selectByPrimaryKey(productId);
    }

    /**
     * 支付回调获取材料精算
     */
    public List<BudgetMaterial> caiLiao(String houseFlowId){
        try{
            Example example = new Example(BudgetMaterial.class);
            example.createCriteria()
                    .andEqualTo(BudgetMaterial.HOUSE_FLOW_ID, houseFlowId)
                    .andEqualTo(BudgetMaterial.DELETE_STATE, 0)
                    .andEqualTo(BudgetMaterial.STETA,1);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);
            for (BudgetMaterial budgetMaterial : budgetMaterialList){
                    budgetMaterial.setPrice(budgetMaterial.getPrice());
                    budgetMaterial.setCost(budgetMaterial.getCost());
                    budgetMaterial.setTotalPrice(budgetMaterial.getConvertCount() * budgetMaterial.getPrice());//已支付 记录总价
                    budgetMaterial.setDeleteState(3);//已支付
                    budgetMaterial.setModifyDate(new Date());
                    budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
            }
            return budgetMaterialList;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    /**
     * 支付回调修改人工精算
     * 业主取消的又改为待付款
     */
    public List<BudgetWorker> renGong(String houseFlowId){
        try{
            Example example = new Example(BudgetWorker.class);
            example.createCriteria().andEqualTo(BudgetWorker.HOUSE_FLOW_ID, houseFlowId).andEqualTo(BudgetWorker.DELETE_STATE, 0);
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
            for(BudgetWorker budgetWorker : budgetWorkerList){
                budgetWorker.setPrice(budgetWorker.getPrice());
                budgetWorker.setTotalPrice(budgetWorker.getShopCount() * budgetWorker.getPrice());
                budgetWorker.setDeleteState(3);//已支付
                budgetWorker.setModifyDate(new Date());
                budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
            }
            return budgetWorkerList;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    /**
     * 支付时工种人工总价
     */
    public Double getBudgetWorkerPrice(String houseId, String workerTypeId){
        return budgetWorkerMapper.getBudgetWorkerPrice(houseId,workerTypeId);
    }
    /**
     * 支付时工种材料总价
     */
    public Double getBudgetCaiPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getBudgetCaiPrice(houseId,workerTypeId);
    }
    public Double nonPaymentCai(String houseId,String workerTypeId){
        return budgetMaterialMapper.nonPaymentCai(houseId,workerTypeId);
    }


    /**
     * 支付时工种服务总价
     */
    public Double getBudgetSerPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getBudgetSerPrice(houseId,workerTypeId);
    }
    public Double nonPaymentSer(String houseId,String workerTypeId){
        return budgetMaterialMapper.nonPaymentSer(houseId,workerTypeId);
    }
    public Double getNotSerPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getNotSerPrice(houseId,workerTypeId);
    }
    /**
     * 支付时工种未选择材料总价
     */
    public Double getNotCaiPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getNotCaiPrice(houseId,workerTypeId);
    }




    /*********************商品3.0改造 **************************/

    public List<ShopGoodsDTO> queryShopGoods(String houseId, String workerTypeId){
        List<ShopGoodsDTO> budgetLabelDTOS =  budgetMaterialMapper.queryShopGoods(houseId,workerTypeId);
        for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
            budgetLabelDTO.setLabelDTOS(queryBudgetLabel(houseId,workerTypeId,budgetLabelDTO.getShopId()));
        }
        return budgetLabelDTOS;
    }
    public List<BudgetLabelDTO> queryBudgetLabel(String houseId, String workerTypeId,String storefontId){
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<BudgetLabelDTO> budgetLabelDTOS =  budgetMaterialMapper.queryBudgetLabel(houseId,workerTypeId,storefontId);//精算工钱
        List<BudgetLabelGoodsDTO> budgetLabelGoodsDTOS = queryBudgetLabelGoods(houseId,workerTypeId,storefontId);//精算工钱
        for (BudgetLabelDTO budgetLabelDTO : budgetLabelDTOS) {
            BigDecimal totalZPrice = new BigDecimal(0);//组总价
            String[] array = budgetLabelDTO.getCategoryIds().split(",");
            List<BudgetLabelGoodsDTO> budgetLabelGoodss= new ArrayList<>();
            for (BudgetLabelGoodsDTO budgetLabelGoodsDTO : budgetLabelGoodsDTOS) {
                boolean flag = Arrays.asList(array).contains(budgetLabelGoodsDTO.getCategoryId());
                if(flag){
                    if(budgetLabelGoodsDTO.getDeleteState()!=2) {
                        totalZPrice = totalZPrice.add(budgetLabelGoodsDTO.getTotalPrice());
                    }
                    if(!CommonUtil.isEmpty(budgetLabelGoodsDTO.getGoodsId())){
                        DjBasicsGoods goods = goodsMapper.selectByPrimaryKey(budgetLabelGoodsDTO.getGoodsId());
                        budgetLabelGoodsDTO.setBuy(goods.getBuy());
                        budgetLabelGoodsDTO.setSales(goods.getSales());
                        budgetLabelGoodsDTO.setIsInflueDecorationProgress(goods.getIsInflueDecorationProgress());

                        Brand brand =null;
                        if (!CommonUtil.isEmpty(goods.getBrandId())) {
                            brand = iBrandMapper.selectByPrimaryKey(goods.getBrandId());
                        }
                        if (!CommonUtil.isEmpty(budgetLabelGoodsDTO.getAttributeName())) {
                            budgetLabelGoodsDTO.setAttributeName(budgetLabelGoodsDTO.getAttributeName().replaceAll(",", " "));
                        }
                        if (brand!=null) {
                            budgetLabelGoodsDTO.setAttributeName(brand.getName()+" "+budgetLabelGoodsDTO.getAttributeName());
                        }
                    }
                    budgetLabelGoodsDTO.setImage(CommonUtil.isEmpty(budgetLabelGoodsDTO.getImage())?"":imageAddress+budgetLabelGoodsDTO.getImage());
                    budgetLabelGoodss.add(budgetLabelGoodsDTO);
                }
            }
            budgetLabelDTO.setTotalPrice(totalZPrice);
            budgetLabelDTO.setGoods(budgetLabelGoodss);
        }
        return budgetLabelDTOS;
    }
    public  List<BudgetLabelGoodsDTO> queryBudgetLabelGoods(String houseId, String workerTypeId,String storefontId){
        return budgetMaterialMapper.queryBudgetLabelGoods(houseId,workerTypeId,storefontId);
    }

    public StorefontInfoDTO getStroreProductInfo(String storefontId, String productId){
        //根据店铺商品ID查询对应的店铺数据
        DjBasicsProductTemplate djBasicsProductTemplate=iBasicsProductTemplateMapper.getProductListByStoreproductId(productId);
        StorefontInfoDTO storefontInfoDTO=new StorefontInfoDTO();
        if(djBasicsProductTemplate!=null){
            Map<String,Object> resMap= BeanUtils.beanToMap(djBasicsProductTemplate);
            //查询对应大类下符合条件的店铺货品及商品
            List<BasicsGoods> goodsList=iBasicsProductTemplateMapper.getGoodsListByStorefontId(storefontId,djBasicsProductTemplate.getCategoryId());
            List<DjBasicsProductTemplate> productList=iBasicsProductTemplateMapper.getProductTempListByStorefontId(storefontId,djBasicsProductTemplate.getGoodsId());
            resMap.put("goodsList",goodsList);
            resMap.put("productList",productList);
            storefontInfoDTO=BeanUtils.mapToBean(StorefontInfoDTO.class,resMap);
            storefontInfoDTO.setProductId(djBasicsProductTemplate.getId());
            storefontInfoDTO.setGoodsId(djBasicsProductTemplate.getGoodsId());
            storefontInfoDTO.setStorefontId(storefontId);
        }
        return storefontInfoDTO;
    }

    public StorefontInfoDTO getStroreProductInfoById(String productId){
        //根据店铺商品ID查询对应的店铺数据
        DjBasicsProductTemplate djBasicsProductTemplate=iBasicsProductTemplateMapper.getProductListByStoreproductId(productId);
        StorefontInfoDTO storefontInfoDTO=new StorefontInfoDTO();
        BeanUtils.beanToBean(storefontInfoDTO,djBasicsProductTemplate);
        return storefontInfoDTO;
    }


    /**
     * 添加对应的设计、精算信息到精算表中去
     * 添加设计精算信息
     * @param actuarialDesignAttr (设计精算信息）
     *                            设计精算列表 (
     *      *      *      * id	String	设计精算模板ID
     *      *      *      * configName	String	设计精算名称
     *      *      *      * configType	String	配置类型1：设计阶段 2：精算阶段
     *      *      *      * productList	List	商品列表
     *                      productList.productTemplateId 商品模板Id
     *      *      *      * productList.productId	String	商品ID
     *      *      *      * productList.productName	String	商品名称
     *      *      *      * productList.productSn	String	商品编码
     *      *      *      * productList.goodsId	String	货品ID
     *      *      *      * productList.storefrontId	String	店铺ID
     *      *      *      * productList.price	double	商品价格
     *      *      *      * productList.unit	String	商品单位
     *      *      *      * productList.unitName	String	单位名称
     *      *      *      * productList.image	String	图片
     *      *      *      * productList.imageUrl	String	详情图片地址
     *      *      *      * productList.valueIdArr	String	商品规格ID
     *      *      *      * productList.valueNameArr	String	商品规格名称
     * @param houseId 房子ID
     * @param square 房子面积
     * @return
     */
    public void insertActuarialDesignInfo(String actuarialDesignAttr,String houseId,BigDecimal square){
        logger.info("修改商品："+actuarialDesignAttr);
        JSONArray actuarialDesignList=JSONArray.parseArray(actuarialDesignAttr);
        if(actuarialDesignList!=null&&actuarialDesignList.size()>0){
            for(int i=0;i<actuarialDesignList.size();i++){
                JSONObject obj=(JSONObject)actuarialDesignList.get(i);
                String workerTypeId=(String)obj.get("configType");
                //获取商品信息
                JSONArray productList=obj.getJSONArray("productList");
                JSONArray listOfGoods=new JSONArray();
                for(int j=0;j<productList.size();j++){
                    JSONObject productObj=productList.getJSONObject(j);
                    JSONObject jsonObject = new JSONObject();
                    String goodsId = productObj.getString("goodsId");//货品Id
                    BasicsGoods basicsGoods=iBasicsGoodsMapper.selectByPrimaryKey(goodsId);
                    jsonObject.put("productId",productObj.getString("productTemplateId"));//商品模板ID
                    jsonObject.put("goodsId",goodsId);
                    jsonObject.put("productType",basicsGoods.getType());//0:材料；1：包工包料；2:人工
                    jsonObject.put("groupType","");
                    jsonObject.put("goodsGroupId","");
                    if(productObj.getString("shopCount")!=null&& StringUtils.isNotBlank(productObj.getString("shopCount"))){
                        Double shopCount = Double.parseDouble(productObj.getString("shopCount"));//数量
                        jsonObject.put("shopCount",shopCount);//数量
                    }else{
                        jsonObject.put("shopCount",square.doubleValue());//数量
                    }
                    listOfGoods.add(jsonObject);
                }

                budgetWorkerService.makeBudgets(null,houseId,workerTypeId,listOfGoods.toJSONString());
            }
        }

    }

    /**
     * 中台装修列表，我要装修信息查询
     * @param houseId
     * @param workerTypeId
     * @return
     */
    public Map<String, Object> getAllBudgetMaterialWorkerList(String houseId,String workerTypeId){
        Map<String,Object> resBudgetMap=new HashMap<>();
        resBudgetMap.put("configType",workerTypeId);
        resBudgetMap.put("configName","1".equals(workerTypeId)?"设计阶段":"精算阶段");
        List<Map<String,Object>> budgetList = budgetWorkerMapper.getAllBudgetMaterialWorkerList(houseId,workerTypeId);
        resBudgetMap.put("budgetProductList",budgetList);//设计精算商品列表
        return resBudgetMap;

    }

    /**
     * APP端，我要装修列表下单详情显示
     * @param houseId 房子ID
     * @return
     */
    public List<Map<String,Object>> getHouseDetailInfoList(String houseId) {
        //先查询店铺汇总信息，再查询对应的商品信息(包含 店铺ID，总价钱
        List<Map<String, Object>> houseDetailList = budgetWorkerMapper.getHouseDetailInfoList(houseId);
        if (houseDetailList != null && houseDetailList.size() > 0) {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (Map<String, Object> houseMap : houseDetailList) {
                List<ActuarialProductAppDTO> productlist = budgetWorkerMapper.getBudgetProductList(houseId, (String) houseMap.get("storefrontId"));
                searchActuarialConfigServices.getProductList(productlist, address);
                houseMap.put("productList", productlist);
            }
        }
        return houseDetailList;
    }
    public ServerResponse getProductTempListByStorefontId(String storefontId,String goodsId){
        List<DjBasicsProductTemplate> productList=iBasicsProductTemplateMapper.getProductTempListByStorefontId(storefontId,goodsId);
        return  ServerResponse.createBySuccess("查询成功",productList);
    }


}
