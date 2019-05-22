package com.dangjia.acg.controller.product;


import com.dangjia.acg.api.product.MasterProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.service.product.MasterProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/7
 * Time: 15:34
 */
@RestController
public class MasterProductController implements MasterProductAPI {
    @Autowired
    private MasterProductService masterProductService;

    @Override
    @ApiMethod
    public ServerResponse updateProductByProductId(String products,String brandSeriesId, String brandId, String goodsId, String id) {
        return masterProductService.updateProductByProductId(products,brandSeriesId,brandId,goodsId,id);
    }
}
