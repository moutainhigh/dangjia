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
//    @Autowired
//    private CraftsmanConstructionService constructionService;

    /**
     * 供货设置-增加已选商品
     *
     * @param userToken
     * @return
     */
    public ServerResponse addStorefrontProduct(String userToken) {
        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;

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
     * @param userToken
     * @return
     */
    public ServerResponse delStorefrontProductById(String userToken, String id) {
        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;

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
     * @param userToken
     * @param type
     * @return
     */
    public ServerResponse queryStorefrontProductByType(String userToken, String type) {
        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;
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
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;
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
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;
            //获取列表集合
            //批量插入到dj_basics_storefront_product中

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
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;
            return null;
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
