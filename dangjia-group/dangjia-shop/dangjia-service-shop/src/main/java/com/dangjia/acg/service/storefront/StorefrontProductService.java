package com.dangjia.acg.service.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontProductMapper;
import com.dangjia.acg.modle.storefront.Storefront;
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
     * 通过类别查询商品
     *
     * @param userToken
     * @param type
     * @return
     */
    public ServerResponse queryStorefrontProductByType(String userToken, String type) {
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
     * @param userToken
     * @param id
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setSpStatusById(String userToken, String id, String isShelfStatus) {
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
     * @param userToken
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setAllStoreProductByIsShelfStatus(String userToken, String isShelfStatus) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据主键删除商品
     *
     * @param userToken
     * @param id
     * @return
     */
    public ServerResponse delStorefrontProductById(String userToken, String id) {
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
     * @param userToken
     * @param id
     * @return
     */
    public ServerResponse updateStorefrontProductById(String userToken, String id) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询已选列表
     *
     * @param userToken
     * @param key
     * @return
     */
    public ServerResponse queryStorefrontProductBykey(String userToken, String key) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
