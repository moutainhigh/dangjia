package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.finance.WebOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.finance.WebOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    @Override
    @ApiMethod
    public ServerResponse getAllOrders(HttpServletRequest request, PageDTO pageDTO,String cityId, Integer state, String searchKey, String beginDate,String endDate) {
        if(StringUtils.isBlank(cityId)){
            cityId = request.getParameter(Constants.CITY_ID);
        }
        return webOrderService.getAllOrders(pageDTO,cityId, state, searchKey,beginDate, endDate);
    }

    /**
     * 财务--订单管理--订单详情
     * @param request
     * @param businessNumber
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getOrderItemInfoList(HttpServletRequest request,String businessNumber){
        return webOrderService.getOrderItemInfoList(businessNumber);
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
