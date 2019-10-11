package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supplier.DjSupApplicationProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjSupApplicationProductController implements DjSupApplicationProductAPI {

    @Autowired
    private DjSupApplicationProductService djSupApplicationProductService;

    @Override
    @ApiMethod
    public ServerResponse getExaminedProduct(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationProductService.getExaminedProduct(request, supId, shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationProductService.getSuppliedProduct(request, supId, shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectAllProduct(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationProductService.rejectAllProduct(request, supId, shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectPartProduct(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationProductService.rejectPartProduct(request, supId, shopId);
    }
}
