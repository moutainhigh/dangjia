package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontProductListDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductViewDTO;
import com.dangjia.acg.mapper.storefront.IStorefrontProductMapper;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class StorefrontProductService {
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontProductMapper istorefrontProductMapper;


    /**
     * 根据id查询店铺商品信息
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
            example.createCriteria().andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID, basicsStorefrontProductDTO.getProdTemplateId());
            List<StorefrontProduct> list = istorefrontProductMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createBySuccessMessage("店铺商品已经添加，不能重复添加!");
            }

            StorefrontProduct storefrontProduct = new StorefrontProduct();
            BeanUtils.copyProperties(storefrontProduct, basicsStorefrontProductDTO);//实体对象赋值
            int i = istorefrontProductMapper.insertSelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("增加店铺商品成功");
            } else {
                return ServerResponse.createBySuccessMessage("增加店铺商品失败");
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
                return ServerResponse.createBySuccessMessage("商品ID不能为空");
            }
            int i = istorefrontProductMapper.deleteByPrimaryKey(id);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("删除成功");
            } else {
                return ServerResponse.createBySuccessMessage("删除失败");
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
    public ServerResponse queryStorefrontProductByKeyWord(String keyWord) {
        try {
            List<BasicsStorefrontProductViewDTO> list = istorefrontProductMapper.queryStorefrontProductViewDTOList(keyWord);
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
                return ServerResponse.createBySuccessMessage("店铺商品ID不能为空");
            }

            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createBySuccessMessage("商品上下架状态不能为空");
            }

            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setId(id);
            storefrontProduct.setIsShelfStatus(isShelfStatus);
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("设置商品上下架成功");
            } else {
                return ServerResponse.createBySuccessMessage("设置商品上下架失败");
            }
        } catch (Exception e) {
            logger.error("设置商品上下架失败：", e);
            return ServerResponse.createByErrorMessage("设置商品上下架失败");
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
                return ServerResponse.createBySuccessMessage("店铺商品ID集合不能为空");
            }
            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createBySuccessMessage("商品上下架状态不能为空");
            }
            String[] iditem = id.split(",");
            for (int i = 0; i < iditem.length; i++) {
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setId(iditem[i]);
                storefrontProduct.setIsShelfStatus(isShelfStatus);
                int k = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
                if (k > 0) {
                    return ServerResponse.createBySuccessMessage("设置商品上下架成功");
                } else {
                    return ServerResponse.createBySuccessMessage("设置商品上下架失败");
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("设置商品批量上架失败：", e);
            return ServerResponse.createByErrorMessage("设置商品批量上架失败");
        }
    }


    /**
     * 根据id查询店铺商品
     * @param id
     * @return
     */
    public ServerResponse editStorefrontProductById(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createBySuccessMessage("商品ID不能为空");
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
            if (StringUtils.isEmpty(storefrontProduct.getId())) {
                return ServerResponse.createBySuccessMessage("商品ID不能为空");
            }
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("修改成功");
            } else {
                return ServerResponse.createBySuccessMessage("修改失败");
            }
        } catch (Exception e) {
            logger.error("供货设置-保存编辑店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("供货设置-保存编辑店铺商品失败");
        }
    }

}
