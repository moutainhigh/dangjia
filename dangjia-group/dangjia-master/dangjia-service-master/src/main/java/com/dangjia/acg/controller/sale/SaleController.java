package com.dangjia.acg.controller.sale;

import com.dangjia.acg.api.sale.SaleAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sale.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SaleController implements SaleAPI {
    @Autowired
    private SaleService saleService;

    @Override
    @ApiMethod
    public ServerResponse getUserStoreList(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        return saleService.getUserStoreList(userToken, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse getUserStore(HttpServletRequest request, String userToken) {
        return saleService.getUserStore(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse setUserStore(HttpServletRequest request, String userToken, String storeId) {
        return saleService.setUserStore(userToken, storeId);
    }
}
