package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.SupplyDimensionDTO;
import com.dangjia.acg.service.supplier.DjSupApplicationProductService;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.model.PageDTO;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class DjSupApplicationProductController implements DjSupApplicationProductAPI {

    @Autowired
    private DjSupApplicationProductService djSupApplicationProductService;


    @Override
    @ApiMethod
    public ServerResponse insertDjSupApplicationProduct(HttpServletRequest request, String jsonStr, String cityId, String supId, String shopId) {
        return djSupApplicationProductService.insertDjSupApplicationProduct(jsonStr,cityId,supId,shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryHaveGoods(HttpServletRequest request, String supId, String shopId,String applicationStatus , PageDTO pageDTO) {
        return djSupApplicationProductService.queryHaveGoods(supId, shopId,applicationStatus, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse updateHaveGoods(HttpServletRequest request, String jsonStr, String userId) {
        return djSupApplicationProductService.updateHaveGoods(jsonStr,userId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateReapply(HttpServletRequest request, String jsonStr) {
        return djSupApplicationProductService.updateReapply(jsonStr);
    }

    @Override
    @ApiMethod
    public List<SupplyDimensionDTO> queryDjSupSupplierProductList(String supId, String searchKey) {
        return djSupApplicationProductService.queryDjSupSupplierProductList(supId, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getExaminedProduct(HttpServletRequest request, PageDTO pageDTO, String applicationStatus, String shopId, String keyWord) {
        return djSupApplicationProductService.getExaminedProduct(pageDTO,applicationStatus, shopId,keyWord);
    }



    @Override
    @ApiMethod
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId,String applicationStatus ) {
        return djSupApplicationProductService.getSuppliedProduct(supId, shopId,applicationStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectAllProduct(HttpServletRequest request, String id) {
        return djSupApplicationProductService.rejectAllProduct(id);
    }

    @Override
    @ApiMethod
    public ServerResponse rejectPartProduct(HttpServletRequest request, String id) {
        return djSupApplicationProductService.rejectPartProduct(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryNotForTheGoods(HttpServletRequest request, String supId, String shopId,PageDTO pageDTO) {
        return djSupApplicationProductService.queryNotForTheGoods(supId,shopId,pageDTO);
    }
}
