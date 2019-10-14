package com.dangjia.acg.controller.storefront;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.storefront.StorefrontProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
    public ServerResponse queryStorefrontProductByKeyWord(String keyWord) {
        return storefrontProductService.queryStorefrontProductByKeyWord(keyWord);
    }

    @Override
    @ApiMethod
    public ServerResponse setSpStatusById(String id, String isShelfStatus) {
        return storefrontProductService.setSpStatusById(id, isShelfStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse setAllStoreProductByIsShelfStatus(String id, String isShelfStatus) {
        return storefrontProductService.setAllStoreProductByIsShelfStatus(id, isShelfStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse updateStorefrontProductById(String id) {
        return storefrontProductService.updateStorefrontProductById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse saveStorefrontProductById(StorefrontProduct storefrontProduct) {
        return storefrontProductService.saveStorefrontProductById(storefrontProduct);
    }


}
