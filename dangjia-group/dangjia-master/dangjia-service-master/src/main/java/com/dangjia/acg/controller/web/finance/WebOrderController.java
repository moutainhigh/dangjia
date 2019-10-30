package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.finance.WebOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.finance.WebOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0004
 * Time: 17:49
 */
@RestController
public class WebOrderController implements WebOrderAPI {

    @Autowired
    private WebOrderService webOrderService;

    @Autowired
    private RedisClient redisClient;

    @Override
    @ApiMethod
    public ServerResponse getAllOrders(HttpServletRequest request, PageDTO pageDTO, Integer state, String searchKey) {
        String cityId = request.getParameter(Constants.CITY_ID);
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        StorefrontDTO storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID, StorefrontDTO.class);
        return webOrderService.getAllOrders(pageDTO,cityId, state, searchKey,storefront.getId());
    }

    @Override
    @ApiMethod
    public ServerResponse getOrderItem(HttpServletRequest request,PageDTO pageDTO, String businessNumber) {
        return webOrderService.getOrderItem(pageDTO,businessNumber);
    }
    @Override
    @ApiMethod
    public ServerResponse getOrderRedItem(HttpServletRequest request, String businessId) {
        return webOrderService.getOrderRedItem(businessId);
    }

    @Override
    @ApiMethod
    public void autoOrderCancel() {
         webOrderService.autoOrderCancel();
    }
}
