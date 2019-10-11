package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupApplicationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supplier.DjSupApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:43
 */
@RestController
public class DjSupApplicationController implements DjSupApplicationAPI {

    @Autowired
    private DjSupApplicationService djSupApplicationService;


    @Override
    @ApiMethod
    public ServerResponse queryDjSupApplicationByShopID(HttpServletRequest request, PageDTO pageDTO, String keyWord, String applicationStatus, String shopId) {
        return djSupApplicationService.queryDjSupApplicationByShopID(pageDTO,shopId,keyWord,applicationStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjSupApplicationProductByShopID(HttpServletRequest request, PageDTO pageDTO, String keyWord, String shopId) {
        return djSupApplicationService.queryDjSupApplicationProductByShopID(request,pageDTO,keyWord,shopId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertSupplierApplicationShop(HttpServletRequest request, String supId, String shopId) {
        return djSupApplicationService.insertSupplierApplicationShop(supId,shopId);
    }

}
