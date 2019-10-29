package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.ShoppingCartProductDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontProductListDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductViewDTO;
import com.dangjia.acg.mapper.storefront.IStorefrontProductMapper;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class StorefrontProductService {
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontProductMapper istorefrontProductMapper;

    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI ;

    /**
     * 根据店铺id查询商品
     * @param storefrontId
     * @param searchKey
     * @return
     */
    public List<StorefrontDTO> queryStorefrontListByStorefrontId(String storefrontId, String searchKey) {
        return istorefrontProductMapper.queryStorefrontListByStorefrontId( storefrontId,  searchKey);
    }
    /**
     * 根据id查询店铺商品信息
     *
     * @param id
     * @return
     */
    public StorefrontProductListDTO querySingleStorefrontProductById(String id) {
        return istorefrontProductMapper.querySingleStorefrontProductById(id);
    }

    /**
     * 供货设置-增加已选商品
     *
     * @return
     */
    public ServerResponse addStorefrontProduct(BasicsStorefrontProductDTO basicsStorefrontProductDTO) {
        try {
            //判断是否重复添加
            Example example = new Example(StorefrontProduct.class);
            example.createCriteria().andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID, basicsStorefrontProductDTO.getProdTemplateId())
            .andEqualTo(StorefrontProduct.STOREFRONT_ID,basicsStorefrontProductDTO.getStorefrontId());
            List<StorefrontProduct> list = istorefrontProductMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createByErrorMessage("店铺商品已经添加，不能重复添加!商品模板ID:"+list.get(0).getProdTemplateId());
            }
            DjBasicsProductTemplate djBasicsProductTemplate=null;
            ServerResponse serverResponse=djBasicsProductAPI.getProductById(null,basicsStorefrontProductDTO.getProdTemplateId());
            if (serverResponse != null && serverResponse.getResultObj() != null) {
                djBasicsProductTemplate = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), DjBasicsProductTemplate.class);
            }
            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setStorefrontId(basicsStorefrontProductDTO.getStorefrontId());
            storefrontProduct.setImage(djBasicsProductTemplate.getImage());//大图
            storefrontProduct.setDetailImage(djBasicsProductTemplate.getDetailImage());//缩略图
            storefrontProduct.setMarketName(djBasicsProductTemplate.getMarketingName());//营销名称
            storefrontProduct.setSellPrice(basicsStorefrontProductDTO.getSellPrice());//销售价格
            storefrontProduct.setSuppliedNum(basicsStorefrontProductDTO.getSuppliedNum());//供货数量
            storefrontProduct.setIsUpstairsCost(basicsStorefrontProductDTO.getIsUpstairsCost());//师傅是否按一层收取上楼费
            storefrontProduct.setIsDeliveryInstall(basicsStorefrontProductDTO.getIsDeliveryInstall());//是否送货与安装/施工分开
            storefrontProduct.setMoveCost(basicsStorefrontProductDTO.getMoveCost());// 搬运费
            storefrontProduct.setIsShelfStatus(basicsStorefrontProductDTO.getIsShelfStatus());//是否上下架
            storefrontProduct.setProdTemplateId(djBasicsProductTemplate.getId());//货品id
            storefrontProduct.setGoodsId( djBasicsProductTemplate.getGoodsId());// 商品id
            storefrontProduct.setProductName(djBasicsProductTemplate.getName());//模板名称
            int i = istorefrontProductMapper.insert(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("增加店铺商品成功");
            } else {
                return ServerResponse.createByErrorMessage("增加店铺商品失败");
            }
        } catch (Exception e) {
            logger.error("增加店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("增加店铺商品失败");
        }
    }

    /**
     * 供货设置-删除已选商品
     *
     * @return
     */
    public ServerResponse delStorefrontProductById(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("商品ID不能为空");
            }
            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setId(id);
            storefrontProduct.setDataStatus(1);//删除
            storefrontProduct.setCreateDate(null);
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("删除成功");
            } else {
                return ServerResponse.createByErrorMessage("删除失败");
            }
        } catch (Exception e) {
            logger.error("删除已选商品失败：", e);
            return ServerResponse.createByErrorMessage("删除已选商品失败");
        }
    }


    /**
     * 供货设置-已选商品-通过货品或者商品名称查询
     *
     * @param keyWord
     * @return
     */
    public ServerResponse queryStorefrontProductByKeyWord(String keyWord,String storefrontId,String cityId) {
        try {


            if (StringUtils.isEmpty(storefrontId)) {
                return ServerResponse.createByErrorMessage("店铺ID不能为空!");
            }

            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }

            List<BasicsStorefrontProductViewDTO> list = istorefrontProductMapper.queryStorefrontProductViewDTOList(keyWord,storefrontId,cityId);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }

            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 设置商品上下架
     *
     * @param id
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setSpStatusById(String id, String isShelfStatus) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("店铺商品ID不能为空");
            }

            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createByErrorMessage("商品上下架状态不能为空");
            }
            //判断，如果是人工商品，提示不能上架
            Integer k=istorefrontProductMapper.selectProductByGoodsType(id);

            if(k>0)
            {
                return ServerResponse.createByErrorMessage("温馨提示：人工商品提示不能上架");
            }
            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setId(id);
            storefrontProduct.setIsShelfStatus(isShelfStatus);
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i <= 0) {
                return ServerResponse.createByErrorMessage("商品上下架失败");
            }
            return ServerResponse.createBySuccessMessage("商品上下架成功");
        } catch (Exception e) {
            logger.error("商品上下架失败：", e);
            return ServerResponse.createByErrorMessage("商品上下架失败");
        }
    }

    /**
     * 设置商品批量上架
     *
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setAllStoreProductByIsShelfStatus(String id, String isShelfStatus) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("店铺商品ID集合不能为空");
            }
            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createByErrorMessage("商品上下架状态不能为空");
            }
            String[] iditem = id.split(",");
            Example example = new Example(StorefrontProduct.class);
            example.createCriteria().andIn(StorefrontProduct.ID, Arrays.asList(iditem));
            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setIsShelfStatus(isShelfStatus);
            storefrontProduct.setId(null);
            storefrontProduct.setCreateDate(null);
            int k = istorefrontProductMapper.updateByExampleSelective(storefrontProduct, example);
            if (k > 0) {
                return ServerResponse.createBySuccessMessage("设置商品上下架成功");
            } else {
                return ServerResponse.createByErrorMessage("设置商品上下架失败");
            }
        } catch (Exception e) {
            logger.error("设置商品批量上架失败：", e);
            return ServerResponse.createByErrorMessage("设置商品批量上架失败");
        }
    }


    /**
     * 根据id查询店铺商品
     *
     * @param id
     * @return
     */
    public ServerResponse editStorefrontProductById(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("商品ID不能为空");
            }
            StorefrontProduct storefrontProduct = istorefrontProductMapper.selectByPrimaryKey(id);
            return ServerResponse.createBySuccess("查询成功", storefrontProduct);
        } catch (Exception e) {
            logger.error("根据id修改店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("根据id修改店铺商品失败");
        }
    }

    /**
     * 供货设置-保存编辑店铺商品
     *
     * @param storefrontProduct
     * @return
     */
    public ServerResponse saveStorefrontProductById(StorefrontProduct storefrontProduct) {
        try {
            if (storefrontProduct == null || StringUtils.isEmpty(storefrontProduct.getId())) {
                return ServerResponse.createByErrorMessage("商品ID不能为空");
            }
            storefrontProduct.setCreateDate(null);
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("修改成功");
            } else {
                return ServerResponse.createByErrorMessage("修改失败");
            }
        } catch (Exception e) {
            logger.error("供货设置-保存编辑店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("供货设置-保存编辑店铺商品失败");
        }
    }


    /**
     * 查询商品信息
     * @param storefrontId
     * @param productId
     * @return
     */
    public List<ShoppingCartProductDTO> queryCartList(String storefrontId, String productId) {
        List<ShoppingCartProductDTO> shoppingCartProductDTOS = istorefrontProductMapper.queryCartList(storefrontId, productId);
        return shoppingCartProductDTOS;
    }


    /**
     * 查询收藏商品
     * @param productId
     * @return
     */
    public List<MemberCollectDTO> queryCollectGood(String productId,String storefrontId) {
        List<MemberCollectDTO> memberCollectDTOS = istorefrontProductMapper.queryCollectGood(productId,storefrontId);
        return memberCollectDTOS;
    }

}
