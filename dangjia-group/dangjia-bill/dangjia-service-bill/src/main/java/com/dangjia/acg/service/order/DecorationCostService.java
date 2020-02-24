package com.dangjia.acg.service.order;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.order.DecorationCostDTO;
import com.dangjia.acg.dto.order.DecorationCostItemDTO;
import com.dangjia.acg.dto.refund.DeliverOrderAddedProductDTO;
import com.dangjia.acg.mapper.actuary.IBillBudgetMapper;
import com.dangjia.acg.mapper.config.IBillConfigMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.mapper.delivery.IBillWorkerTypeMapper;
import com.dangjia.acg.mapper.order.IBillBasicsGoodsCategoryMapper;
import com.dangjia.acg.mapper.order.IBillCategoryLabelMapper;
import com.dangjia.acg.mapper.order.IBillDeliverOrderAddedProductMapper;
import com.dangjia.acg.mapper.order.IBillOrderNodeMapper;
import com.dangjia.acg.mapper.refund.IBillBasicsGoodsMapper;
import com.dangjia.acg.mapper.refund.IBillBrandMapper;
import com.dangjia.acg.mapper.refund.IBillProductTemplateMapper;
import com.dangjia.acg.mapper.refund.IBillUnitMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.order.OrderNode;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.CategoryLabel;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.product.BillProductTemplateService;
import com.dangjia.acg.service.refund.RefundAfterSalesService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class DecorationCostService {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesService.class);
    @Autowired
    private IBillDjDeliverOrderMapper iBillDjDeliverOrderMapper;
    @Autowired
    private IBillProductTemplateMapper iBillProductTemplateMapper;
    @Autowired
    private IBillBasicsGoodsMapper iBillBasicsGoodsMapper;
    @Autowired
    private IBillBrandMapper iBillBrandMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBillUnitMapper billUnitMapper;

    @Autowired
    private BillProductTemplateService billProductTemplateService;
    @Autowired
    private IBillBudgetMapper iBillBudgetMapper;
    @Autowired
    private IBillDeliverOrderAddedProductMapper iBillDeliverOrderAddedProductMapper;
    @Autowired
    private IBillConfigMapper iBillConfigMapper;
    @Autowired
    private IBillOrderNodeMapper iBillOrderNodeMapper;
    @Autowired
    private IBillWorkerTypeMapper iBillWorkerTypeMapper;
    @Autowired
    private IBillBasicsGoodsCategoryMapper billBasicsGoodsCategoryMapper;
    /**
     * 查询对应的当前花费信息
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param labelValId 标签 ID
     * @return
     */
    public ServerResponse searchDecorationCostList(PageDTO pageDTO,String userToken, String cityId, String houseId, String labelValId){
        try{
            logger.info("查询当前花费信息userToken={},cityId={},houseId={},labelValId={}",userToken,cityId,houseId,labelValId);
             //1.查询类别汇总数据
             PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
             List<DecorationCostDTO> decorationCostList=iBillDjDeliverOrderMapper.searchDecorationCostList(houseId,labelValId);
             /*if(decorationCostList!=null&&decorationCostList.size()>0){
                 String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                 for(DecorationCostDTO dc:decorationCostList){
                     String categoryId=dc.getCategoryId();
                     //2.查询对应的分类下的货品商品信息
                     List<DecorationCostItemDTO> decorationProductList = iBillDjDeliverOrderMapper.searchDecorationCostDetailList(categoryId,houseId,labelValId);
                     getProductList(decorationProductList,address);
                     dc.setDecorationCostItemList(decorationProductList);//设置详情商品
                 }
             }*/

             PageInfo pageResult = new PageInfo(decorationCostList);


            return ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e){
            logger.error("查询异常",e);
            return  ServerResponse.createByErrorMessage("查询异常");
        }

    }
    public ServerResponse searchDecorationCostProductList(String cityId,String houseId,String labelValId,String categoryId){
        try{
            logger.info("查询当前花费商品列表信息categoryId={},cityId={},houseId={},labelValId={}",categoryId,cityId,houseId,labelValId);
            //2.查询对应的分类下的货品商品信息
            List<DecorationCostItemDTO> decorationProductList = iBillDjDeliverOrderMapper.searchDecorationCostDetailList(categoryId,houseId,labelValId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            getProductList(decorationProductList,address);

            return ServerResponse.createBySuccess("查询成功",decorationProductList);
        }catch (Exception e){
            logger.error("查询异常",e);
            return  ServerResponse.createByErrorMessage("查询异常");
        }
    }

    /**
     * 按分类标签汇总分类花费信息
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    public ServerResponse searchDecorationCategoryLabelList(String userToken,String cityId,String houseId){
        logger.info("查询分类汇总花费信息userToken={},cityId={},houseId={}",userToken,cityId,houseId);
        try{
            Map<String,Object> decorationMap=new HashMap<>();
            List<DecorationCostDTO> categoryLabelList=iBillDjDeliverOrderMapper.searchDecorationCategoryLabelList(houseId);
            DecorationCostDTO dcd=iBillDjDeliverOrderMapper.searchDecorationTotalCost(houseId);
            decorationMap.put("actualPaymentPrice",dcd.getActualPaymentPrice());
            decorationMap.put("purchaseTotalPrice",dcd.getPurchaseTotalPrice());
            decorationMap.put("categoryLabelList",categoryLabelList);
            return ServerResponse.createBySuccess("查询成功",decorationMap);
        }catch (Exception e){
            logger.error("查询异常",e);
            return  ServerResponse.createByErrorMessage("查询异常");
        }
    }

    /**
     * 查询商品对应的规格详情，品牌，单位信息
     * @param productList
     * @param address
     */
    private  void getProductList(List<DecorationCostItemDTO> productList, String address){
        if(productList!=null&&productList.size()>0){
            for(DecorationCostItemDTO ap:productList){
                setProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setProductInfo(DecorationCostItemDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        String purchaseIcon = configUtil.getValue(SysConfig.PRODUCT_PURCHASE_ICON, String.class);//自购商品图片
        DjBasicsProductTemplate pt=iBillProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&& StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image == null) {
                image=pt.getImage();
            }
            if(image!=null&&StringUtils.isNotBlank(image)){
                ap.setImageUrl(StringTool.getImage(image,address));//图片详情地址设置
            }
            if(ap.getSteta()!=null&&ap.getSteta()==2){//自购商品
                ap.setImage(purchaseIcon);
                ap.setImageUrl(address+purchaseIcon);
            }
            Unit unit= billUnitMapper.selectByPrimaryKey(ap.getUnitId());
            if(unit!=null){
                ap.setUnitName(unit.getName());
            }

            BasicsGoods goods=iBillBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
            if(StringUtils.isNotBlank(goods.getBrandId())){
                Brand brand=iBillBrandMapper.selectByPrimaryKey(goods.getBrandId());
                ap.setBrandId(goods.getId());
                ap.setBrandName(brand!=null?brand.getName():"");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(billProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
            String orderItemId=ap.getOrderItemId();//订单详情ID
            if(StringUtils.isNotBlank(orderItemId)){
                //查询增值类商品信息
                List<DeliverOrderAddedProductDTO> orderAddedProductList = iBillDeliverOrderAddedProductMapper.queryOrderListByAnyOrderId(orderItemId);
                ap.setAddedProductList(orderAddedProductList);
            }
        }

    }

    /**
     * 录入自购商品价格信息
     * @param userToken 用户TOKEN
     * @param cityId  城市ID
     * @param actuaryBudgetId 精算设置ID
     * @return
     */
    public ServerResponse editPurchasePrice(String userToken,String cityId,String actuaryBudgetId,Double shopCount,Double totalPrice,Integer housekeeperAcceptance){
        logger.info("查询分类汇总花费信息userToken={},cityId={},actuaryBudgetId={}",userToken,cityId,actuaryBudgetId);
        try{
            BudgetMaterial budgetMaterial=iBillBudgetMapper.selectByPrimaryKey(actuaryBudgetId);
            if(budgetMaterial==null||StringUtils.isBlank(budgetMaterial.getId())){
                return ServerResponse.createByErrorMessage("录入失败，未找到对应的自由购商品");
            }
            budgetMaterial.setShopCount(shopCount);
            budgetMaterial.setTotalPrice(totalPrice);
            budgetMaterial.setModifyDate(new Date());
            budgetMaterial.setHousekeeperAcceptance(housekeeperAcceptance);//是否需要大管家验收（1是，0否）
            iBillBudgetMapper.updateByPrimaryKeySelective(budgetMaterial);
            //给大管家添加验收任务(后续和工匠端一起加）
            return  ServerResponse.createBySuccess("录入成功");
        }catch (Exception e){
            logger.error("查询异常",e);
            return  ServerResponse.createByErrorMessage("查询异常");
        }
    }

    /**
     * 精算--按工序查询精算(已支付精算）
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    public ServerResponse searchBudgetWorkerList(String userToken,String cityId,String houseId){
        try{
            Map<String,Object> map=new HashMap<>();
            //1.查询对应已支付精算的总金额
            Double totalPrice=iBillBudgetMapper.selectTotalPriceByHouseId(houseId,null,null,null);//查询已支付精算的所有金额
            //2.按工序查询已支付定单的汇总
            List<DecorationCostDTO> budgetList=iBillBudgetMapper.selectBudgetWorkerInfoList(houseId);
            //4.获取符合条件的据数返回给前端
            List<Map<String,Object>> list=getCommonList(budgetList,totalPrice,1);
            map.put("totalPrice",totalPrice);//总金额
            Config config= iBillConfigMapper.selectConfigInfoByParamKey("ACTUARIAL_REMARK");//获取对应阶段需处理剩余时间
            if(config!=null){
                map.put("remark",config.getParamDesc());//文字描述
            }
            map.put("totalPrice",totalPrice);//总金额
            map.put("list",list);
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 精算--按类别查询精算(已支付精算）
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    public ServerResponse searchBudgetCategoryList(String userToken,String cityId,String houseId){
        try{

            Map<String,Object> map=new HashMap<>();
            //1.查询对应已支付精算的总金额
            Double totalPrice=iBillBudgetMapper.selectTotalPriceByHouseId(houseId,null,null,null);//查询已支付精算的所有金额
            //2.按类别查询已支付定单的汇总
            List<DecorationCostDTO> budgetList=iBillBudgetMapper.selectBudgetCategoryInfoList(houseId);
            //4.获取符合条件的据数返回给前端
            List<Map<String,Object>> list=getCommonList(budgetList,totalPrice,2);
            map.put("totalPrice",totalPrice);//总金额
            Config config= iBillConfigMapper.selectConfigInfoByParamKey("ACTUARIAL_REMARK");//获取对应阶段需处理剩余时间
            if(config!=null){
                map.put("remark",config.getParamDesc());//文字描述
            }
            map.put("list",list);

            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 筛选条件查贸易
     * @param type 1按工序查，2按分类查
     * @return
     */
    public ServerResponse selectScreeningConditions(Integer type){
        try{
            if(type==1){//工种
                Example example=new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.DATA_STATUS,"0");
                List<WorkerType> list= iBillWorkerTypeMapper.selectAll();
                WorkerType workerType=new WorkerType();
                workerType.setId("");
                workerType.setName("全部");
                list.add(0,workerType);
                return ServerResponse.createBySuccess("查询成功",list);
            }else{//顶级分类
                Example example=new Example(BasicsGoodsCategory.class);
                example.createCriteria().andEqualTo(BasicsGoodsCategory.DATA_STATUS,"0")
                .andEqualTo(BasicsGoodsCategory.PARENT_ID,"1");
                example.orderBy(BasicsGoodsCategory.SORT);
                List<BasicsGoodsCategory> list=billBasicsGoodsCategoryMapper.selectByExample(example);
                BasicsGoodsCategory basicsGoodsCategory=new BasicsGoodsCategory();
                basicsGoodsCategory.setId("");
                basicsGoodsCategory.setName("全部");
                list.add(0,basicsGoodsCategory);
              return ServerResponse.createBySuccess("查询成功",list);
            }
        }catch (Exception e){
            logger.error("查询失败",e);
            return  ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     *
     * @param budgetList
     * @param totalPrice
     * @param type 1按工序查看，2按类别查看
     * @return
     */
    List<Map<String,Object>> getCommonList(List<DecorationCostDTO> budgetList,Double totalPrice,Integer type){
        List<Map<String,Object>> list=new ArrayList<>();
        if(budgetList!=null&&budgetList.size()>0){
            Map<String,Object> map;
            //3.查询对应颜色的显示表
            Example example=new Example(OrderNode.class);
            example.createCriteria().andEqualTo(OrderNode.TYPE,"COLOR_PROFIL");
            example.orderBy( OrderNode.SORT);
            List<OrderNode> colorList=iBillOrderNodeMapper.selectByExample(example);
            Double totalDul=0d;
            for(int i=0;i<budgetList.size();i++){
                map=new HashMap<>();
               DecorationCostDTO dc=budgetList.get(i);
               if(type==1){
                   map.put("id",dc.getWorkerTypeId());
                   map.put("name",dc.getWorkerTypeName());

               }else{
                   map.put("id",dc.getCategoryId());
                   map.put("name",dc.getCategoryName());
               }

                map.put("totalPrice",dc.getTotalPrice());
                Double dul=MathUtil.round(MathUtil.div(dc.getTotalPrice(),totalPrice)*100);
                if(i==budgetList.size()-1){
                    map.put("percentageValue", MathUtil.sub(100,totalDul));//百分比计算
                }else{
                    map.put("percentageValue", dul);//百分比计算
                    totalDul=MathUtil.add(totalDul,dul);
                }
                int index=i;
                if(i>=colorList.size()){
                    index=i%colorList.size();
                }
                OrderNode orderNode=colorList.get(index);
                map.put("colorVlue", orderNode.getName());//颜色设置
                list.add(map);
            }
        }
        return list;
    }
    /**
     * 精算--分类标签汇总信息查询
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param categoryTopId 顶级分类ID
     * @return
     */
    public ServerResponse searchBudgetCategoryLabelList(String userToken,String cityId,String houseId,
                                                        String workerTypeId,String categoryTopId){
        logger.info("查询分类汇总花费信息userToken={},cityId={},houseId={}",userToken,cityId,houseId);
       try{

           Map<String,Object> decorationMap=new HashMap<>();
           List<DecorationCostDTO> categoryLabelList=iBillBudgetMapper.searchBudgetCategoryLabelList(houseId,workerTypeId,categoryTopId);
           Double totalPrice=iBillBudgetMapper.selectTotalPriceByHouseId(houseId,workerTypeId,categoryTopId,null);
           Double totalzgPrice=iBillBudgetMapper.selectTotalPriceByHouseId(houseId,workerTypeId,categoryTopId,2);//自购商品汇总
           decorationMap.put("actualPaymentPrice",totalPrice);
           decorationMap.put("actualSelfPrice",totalzgPrice);
           decorationMap.put("categoryLabelList",categoryLabelList);
           return ServerResponse.createBySuccess("查询成功",decorationMap);
       }catch (Exception e){
           logger.error("查询失败",e);
           return ServerResponse.createByErrorMessage("查询失败");
       }
    }
    /**
     * 精算--分类汇总信息查询(末级分类)
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchTypeId 工种ID/顶级分类ID
     * @param labelValId 类别标签 ID
     * @return
     */
    public ServerResponse searchBudgetLastCategoryList(String userToken,PageDTO pageDTO,String cityId,String houseId,
                                                   String searchTypeId,String labelValId){
        logger.info("查询分类汇总信息userToken={},cityId={},houseId={}",userToken,cityId,houseId);
        try{

            Map<String,Object> decorationMap=new HashMap<>();
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DecorationCostDTO> categoryList=iBillBudgetMapper.searchBudgetLastCategoryList(houseId,searchTypeId,labelValId);
            Double totalPrice=iBillBudgetMapper.searchBudgetLastCategoryCount(houseId,searchTypeId);
            PageInfo pageResult = new PageInfo(categoryList);
            decorationMap.put("totalPrice",totalPrice);
            decorationMap.put("categoryList",pageResult);
            return ServerResponse.createBySuccess("查询成功",decorationMap);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 精算--商品信息
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchTypeId 工种ID/顶级分类ID
     * @param labelValId 类别标签 ID
     * @return
     */
    public ServerResponse searchBudgetProductList(String userToken,String cityId,String houseId,
                                                   String searchTypeId,String labelValId,
                                                  String categoryId){
        logger.info("查询商品信息userToken={},cityId={},houseId={}",userToken,cityId,houseId);
        try{

            //2.查询对应的分类下的货品商品信息
            List<DecorationCostItemDTO> decorationProductList = iBillBudgetMapper.searchBudgetProductList(houseId,searchTypeId,labelValId,categoryId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            getProductList(decorationProductList,address);
            return ServerResponse.createBySuccess("查询成功",decorationProductList);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
