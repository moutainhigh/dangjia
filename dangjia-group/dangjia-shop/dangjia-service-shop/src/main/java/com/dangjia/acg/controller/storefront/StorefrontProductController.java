package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.ShoppingCartProductDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontProductListDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.storefront.StorefrontProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: StorefrontController
 * @Description: 店铺商品管理接口类
 * @author: chenyufeng
 * @date: 2019-10-10
 */
@RestController
public class StorefrontProductController implements StorefrontProductAPI {

    @Autowired
    private StorefrontProductService storefrontProductService;


//    @Override
//    @ApiMethod
//    public List<StorefrontDTO> queryStorefrontListByStorefrontId(String storefrontId, String searchKey) {
//        return storefrontProductService.queryStorefrontListByStorefrontId(storefrontId,searchKey);
//    }

    @Override
    @ApiMethod
    public ServerResponse countStorefrontProduct(String userId, String cityId) {
        return storefrontProductService.countStorefrontProduct( userId,  cityId);
    }

    @Override
    @ApiMethod
    public StorefrontProductListDTO querySingleStorefrontProductById(String id) {
        return storefrontProductService.querySingleStorefrontProductById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse addStorefrontProduct(BasicsStorefrontProductDTO basicsStorefrontProductDTO) {

        return storefrontProductService.addStorefrontProduct(basicsStorefrontProductDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse delStorefrontProductById(String id) {
        return storefrontProductService.delStorefrontProductById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse delProductByProIdAndStoreIdAndCityId(String productId, String storefrontId, String cityId) {
        return storefrontProductService.delProductByProIdAndStoreIdAndCityId(productId,storefrontId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductByKeyWord(String keyWord, String userId,PageDTO pageDTO, String cityId) {
        return storefrontProductService.queryStorefrontProductByKeyWord(keyWord,userId,pageDTO,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStorefrontProductGroundByKeyWord(String keyWord, String userId, PageDTO pageDTO, String cityId) {
        return storefrontProductService.queryStorefrontProductGroundByKeyWord(keyWord,userId,pageDTO,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryProductAdjustmentPriceListByKeyWord(String keyWord, String userId, PageDTO pageDTO, String cityId) {
        return storefrontProductService.queryProductAdjustmentPriceListByKeyWord(keyWord,userId,pageDTO,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse fixModityPrice(String keyWord, String userId, PageDTO pageDTO, String cityId) {
        return storefrontProductService.fixModityPrice(keyWord,userId,pageDTO,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse setSpStatusById(String userId,String cityId,String id, String isShelfStatus) {
        return storefrontProductService.setSpStatusById(userId,cityId,id, isShelfStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse setAllStoreProductByIsShelfStatus(String userId,String cityId,String id, String isShelfStatus) {
        return storefrontProductService.setAllStoreProductByIsShelfStatus(userId,cityId,id, isShelfStatus);
    }



    @Override
    @ApiMethod
    public ServerResponse saveStorefrontProductById(StorefrontProduct storefrontProduct) {
        return storefrontProductService.saveStorefrontProductById(storefrontProduct);
    }

    @Override
    @ApiMethod
    public List<ShoppingCartProductDTO> queryCartList(String storefrontId, String productId) {
        return storefrontProductService.queryCartList(storefrontId,productId);
    }

    @Override
    @ApiMethod
    public List<MemberCollectDTO> queryCollectGood(String productId) {
        return storefrontProductService.queryCollectGood(productId);
    }


}
