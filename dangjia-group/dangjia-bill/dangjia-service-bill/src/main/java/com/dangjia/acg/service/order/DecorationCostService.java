package com.dangjia.acg.service.order;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.order.DecorationCostDTO;
import com.dangjia.acg.dto.order.DecorationCostItemDTO;
import com.dangjia.acg.mapper.actuary.IBillBudgetMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.mapper.refund.*;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.refund.RefundAfterSalesService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IBillAttributeValueMapper iBillAttributeValueMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IBillBudgetMapper iBillBudgetMapper;
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
                //添加图片详情地址字段
                String[] imgArr = image.split(",");
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                ap.setImageUrl(imgStr.toString());//图片详情地址设置
            }
            if(ap.getSteta()==2){//自购商品
                ap.setImage(purchaseIcon);
                ap.setImageUrl(address+purchaseIcon);
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(getNewValueNameArr(pt.getValueIdArr()));
            }
            BasicsGoods goods=iBillBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
            if(StringUtils.isNotBlank(goods.getBrandId())){
                Brand brand=iBillBrandMapper.selectByPrimaryKey(goods.getBrandId());
                ap.setBrandId(goods.getId());
                ap.setBrandName(brand!=null?brand.getName():"");
            }
        }

    }
    /**
     * 获取对应的属性值信息
     * @param valueIdArr
     * @return
     */
    private String getNewValueNameArr(String valueIdArr){
        String strNewValueNameArr = "";
        String[] newValueNameArr = valueIdArr.split(",");
        for (int i = 0; i < newValueNameArr.length; i++) {
            String valueId = newValueNameArr[i];
            if (StringUtils.isNotBlank(valueId)) {
                AttributeValue attributeValue = iBillAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    if (i == 0) {
                        strNewValueNameArr = attributeValue.getName();
                    } else {
                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                    }
                }

            }
        }
        return strNewValueNameArr;
    }
    /**
     * 录入自购商品价格信息
     * @param userToken 用户TOKEN
     * @param cityId  城市ID
     * @param actuaryBudgetId 精算设置ID
     * @return
     */
    public ServerResponse editPurchasePrice(String userToken,String cityId,String actuaryBudgetId,Double shopCount,Double totalPrice){
        logger.info("查询分类汇总花费信息userToken={},cityId={},actuaryBudgetId={}",userToken,cityId,actuaryBudgetId);
        try{
            BudgetMaterial budgetMaterial=iBillBudgetMapper.selectByPrimaryKey(actuaryBudgetId);
            if(budgetMaterial==null||StringUtils.isBlank(budgetMaterial.getId())){
                return ServerResponse.createByErrorMessage("录入失败，未找到对应的自由购商品");
            }
            budgetMaterial.setShopCount(shopCount);
            budgetMaterial.setTotalPrice(totalPrice);
            budgetMaterial.setModifyDate(new Date());
            iBillBudgetMapper.updateByPrimaryKeySelective(budgetMaterial);
            return  ServerResponse.createBySuccess("录入成功");
        }catch (Exception e){
            logger.error("查询异常",e);
            return  ServerResponse.createByErrorMessage("查询异常");
        }
    }
}
