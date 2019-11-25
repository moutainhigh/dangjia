package com.dangjia.acg.service.storefront;


import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.mapper.storefront.IGoodsStorefrontProductAddedRelationMapper;
import com.dangjia.acg.mapper.storefront.IGoodsStorefrontProductMapper;
import com.dangjia.acg.modle.product.ProductAddedRelation;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.storefront.StorefrontProductAddedRelation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.VoidDescriptor;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoodsStorefrontProductService {
    @Autowired
    private IGoodsStorefrontProductMapper iGoodsStorefrontProductMapper;
    @Autowired
    private IGoodsStorefrontProductAddedRelationMapper iGoodsStorefrontProductAddedRelationMapper;

    //添加店铺商品信息
    public String  insertExitStorefrontProduct(String storefrontId, String productId, BasicsProductDTO basicsProductDTO, String cityId){
        Example example=new Example(StorefrontProduct.class);
        example.createCriteria().andEqualTo(StorefrontProduct.ID,storefrontId)
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
