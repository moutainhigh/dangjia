package com.dangjia.acg.service.storefront;


import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.mapper.storefront.IGoodsCityMapper;
import com.dangjia.acg.mapper.storefront.IGoodsStorefrontMapper;
import com.dangjia.acg.mapper.storefront.IGoodsStorefrontProductAddedRelationMapper;
import com.dangjia.acg.mapper.storefront.IGoodsStorefrontProductMapper;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.storefront.StorefrontProductAddedRelation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoodsStorefrontProductService {
    @Autowired
    private IGoodsStorefrontProductMapper iGoodsStorefrontProductMapper;
    @Autowired
    private IGoodsStorefrontProductAddedRelationMapper iGoodsStorefrontProductAddedRelationMapper;
    @Autowired
    private IGoodsStorefrontMapper iGoodsStorefrontMapper;
    @Autowired
    private IGoodsCityMapper iGoodsCityMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 获取店铺地址
     * @param cityId
     * @param userId
     * @return
     */
    public String getStorefrontId(String cityId,String userId){
        Storefront storefront=iGoodsStorefrontMapper.selectStoreByTypeCityId(cityId,"worker");
        if(storefront==null||StringUtils.isBlank(storefront.getId())){
            /**
             * 当家装修{城市}店
             * 当家{城市}总部地址
             * 当家装修定义了统一的人工标准，由符合要求的工匠提供服务，请放心选购
             */
            //MainUser mainUser=iGoodsUserMapper.selectByPrimaryKey(userId);
            City city=iGoodsCityMapper.selectByPrimaryKey(cityId);
            String cityName="";
            if(city!=null&&StringUtils.isNotBlank(city.getName())){
                cityName=city.getName();
            }
            storefront=new Storefront();
            storefront.setUserId(userId);
            storefront.setCityId(cityId);
            storefront.setStorefrontName("当家装修"+cityName+"店");
            storefront.setStorefrontAddress("当家"+cityName+"总部地址");
            storefront.setStorefrontDesc("当家装修定义了统一的人工标准，由符合要求的工匠提供服务，请放心选购");
            storefront.setStorefrontLogo("");//店铺logo暂无
            storefront.setIfDjselfManage(1);
            storefront.setStorefrontType("worker");
            String systemlogo = configUtil.getValue(SysConfig.ORDER_DANGJIA_ICON, String.class);
            storefront.setSystemLogo(systemlogo);
            iGoodsStorefrontMapper.insertSelective(storefront);
        }
        return storefront.getId();
    }
    //添加店铺商品信息
    public String  insertExitStorefrontProduct(String storefrontId, String productId, BasicsProductDTO basicsProductDTO, String cityId){
        Example example=new Example(StorefrontProduct.class);
        example.createCriteria().andEqualTo(StorefrontProduct.STOREFRONT_ID,storefrontId)
                .andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID,productId);
        List<StorefrontProduct> storefrontProductList=iGoodsStorefrontProductMapper.selectByExample(example);
        StorefrontProduct storefrontProduct;
        if(storefrontProductList!=null&&storefrontProductList.size()>0){
            //修改
            storefrontProduct=storefrontProductList.get(0);
        }else{
            //新增
            storefrontProduct=new StorefrontProduct();
        }
        storefrontProduct.setProdTemplateId(productId);
        storefrontProduct.setStorefrontId(storefrontId);
        storefrontProduct.setImage(basicsProductDTO.getImage());//大图
        storefrontProduct.setDetailImage(basicsProductDTO.getDetailImage());//缩略图
        storefrontProduct.setMarketName(basicsProductDTO.getMarketingName());//营销名称
        storefrontProduct.setSellPrice(basicsProductDTO.getPrice());//销售价格
        storefrontProduct.setSuppliedNum(999.0);//供货数量
        storefrontProduct.setIsUpstairsCost("0");//师傅是否按一层收取上楼费
        storefrontProduct.setIsDeliveryInstall("0");//是否送货与安装/施工分开
        storefrontProduct.setMoveCost(new BigDecimal(basicsProductDTO.getCartagePrice()==null?0:basicsProductDTO.getCartagePrice()));// 搬运费
        storefrontProduct.setIsShelfStatus("1");//是否上下架
        storefrontProduct.setGoodsId( basicsProductDTO.getGoodsId());// 货品ID
        storefrontProduct.setProductName(basicsProductDTO.getName());//模板名称
        storefrontProduct.setCityId(cityId);
        storefrontProduct.setImage(basicsProductDTO.getImage());
        storefrontProduct.setDetailImage(basicsProductDTO.getDetailImage());
        if(storefrontProductList!=null&&storefrontProductList.size()>0){
            //修改
            iGoodsStorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
        }else{
            //新增
            iGoodsStorefrontProductMapper.insertSelective(storefrontProduct);
        }
        return storefrontProduct.getId();
    }

    /**
     * 添加增值类商品关联关系
     * @param productTemplateId
     * @param storefrontProductId
     * @param storefrontId
     */
    public void insertAddReation(String productTemplateId, String storefrontProductId, String storefrontId){
        Example example=new Example(StorefrontProduct.class);
        example.createCriteria().andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID, productTemplateId)
        .andEqualTo(StorefrontProduct.STOREFRONT_ID,storefrontId);
        List<StorefrontProduct> storefrontProductList=iGoodsStorefrontProductMapper.selectByExample(example);
        if(storefrontProductList!=null&&storefrontProductList.size()>0){
            StorefrontProduct storefrontProduct=storefrontProductList.get(0);
            StorefrontProductAddedRelation spd=new StorefrontProductAddedRelation();
            spd.setAddedProductId(storefrontProductId);
            spd.setProductId(storefrontProduct.getId());
            iGoodsStorefrontProductAddedRelationMapper.insertSelective(spd);
        }
    }

}
