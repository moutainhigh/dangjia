package com.dangjia.acg.controller.product;


import com.dangjia.acg.api.product.MasterProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.MasterProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
    public ServerResponse updateProductByProductId(String id, String  categoryId, String brandSeriesId,
                                                   String brandId, String name, String unitId, String unitName) {
        return masterProductService.updateProductByProductId(id,categoryId,  brandSeriesId,
                 brandId,  name,  unitId,  unitName);
    }
}
