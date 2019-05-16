package com.dangjia.acg.controller.web.deliver;

import com.dangjia.acg.api.web.deliver.WebProductChangeAPI;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.ProductChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Yinjianbo
 * Date: 2019-5-16
 * Web端商品换货Controller
 */
@RestController
public class WebProductChangeController implements WebProductChangeAPI {

    @Autowired
    private ProductChangeService productChangeService;

    @Override
    public ServerResponse changeOrderState(String houseId, Integer pageNum, Integer pageSize, String beginDate, String endDate, String likeAddress) {
        return productChangeService.changeOrderState(houseId, pageNum, pageSize, beginDate, endDate, likeAddress);
    }

    @Override
    public ServerResponse queryChangeDetail(String orderId, String houseId) {
        return productChangeService.queryChangeDetail(orderId, houseId);
    }

    @Override
    public ServerResponse queryPayChangeDetail(String number, String orderId, String houseId) {
        return null;
    }
}
