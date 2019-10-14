package com.dangjia.acg.service.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontProductMapper;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorefrontProductService {
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontProductMapper istorefrontProductMapper;

    /**
     * 供货设置-增加已选商品
     *
     * @return
     */
    public ServerResponse addStorefrontProduct() {
        try {

            StorefrontProduct storefrontProduct = new StorefrontProduct();
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
     * @param type
     * @return
     */
    public ServerResponse queryStorefrontProductByType(String type) {
        try {
            return null;
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
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 设置商品批量架
     *
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setAllStoreProductByIsShelfStatus(String isShelfStatus) {
        try {

            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据id修改店铺商品
     *
     * @param id
     * @return
     */
    public ServerResponse updateStorefrontProductById(String id) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
