package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supplier.DjSupApplicationProductService;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
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
    public ServerResponse insertDjSupApplicationProduct(HttpServletRequest request, String jsonStr) {
        return djSupApplicationProductService.insertDjSupApplicationProduct(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse queryHaveGoods(HttpServletRequest request, String supId, String shopId,String applicationStatus , PageDTO pageDTO) {
        return djSupApplicationProductService.queryHaveGoods(supId, shopId,applicationStatus, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse updateHaveGoods(HttpServletRequest request, String jsonStr) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse getExaminedProduct(HttpServletRequest request, PageDTO pageDTO, String applicationStatus, String shopId, String keyWord) {
        return djSupApplicationProductService.getExaminedProduct(request, pageDTO,applicationStatus, shopId,keyWord);
    }



    @Override
    @ApiMethod
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId,String applicationStatus ,PageDTO pageDTO) {
        return djSupApplicationProductService.getSuppliedProduct(request, supId, shopId,applicationStatus,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectAllProduct(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationProductService.rejectAllProduct(request, supId, shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectPartProduct(HttpServletRequest request, String id, String supId, String shopId) {
        return djSupApplicationProductService.rejectPartProduct(request, id,supId, shopId);
    }
}
