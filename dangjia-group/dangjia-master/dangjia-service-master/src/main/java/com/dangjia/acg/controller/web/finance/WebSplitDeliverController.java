package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.web.finance.WebSplitDeliverAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.service.finance.WebSplitDeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0004
 * Time: 17:49
 */
@RestController
public class WebSplitDeliverController implements WebSplitDeliverAPI {

    @Autowired
    private WebSplitDeliverService webSplitDeliverService;


    @Override
    @ApiMethod
    public ServerResponse getAllSplitDeliver(HttpServletRequest request, PageDTO pageDTO,
                                             Integer applyState, String searchKey,
                                             String beginDate, String endDate) {
        return webSplitDeliverService.getAllSplitDeliver(pageDTO, applyState, searchKey, beginDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse setSplitDeliver(HttpServletRequest request, SplitDeliver splitDeliver) {
        return webSplitDeliverService.setSplitDeliver(splitDeliver);
    }

    @Override
    @ApiMethod
    public ServerResponse getOrderSplitList(HttpServletRequest request, PageDTO pageDTO, String supplierId, String searchKey, String beginDate, String endDate) {
        return webSplitDeliverService.getOrderSplitList(pageDTO, supplierId, searchKey, beginDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(String splitDeliverId) {
        return webSplitDeliverService.splitDeliverList(splitDeliverId);
    }

    @Override
    @ApiMethod
    public ServerResponse mendDeliverList(String supplierId, String shipAddress, String beginDate, String endDate, Integer applyState) {
        return webSplitDeliverService.mendDeliverList(supplierId, shipAddress, beginDate, endDate, applyState);
    }

    @Override
    @ApiMethod
    public ServerResponse settlemen(String image, String merge, String supplierId) {
        return webSplitDeliverService.settlemen(image, merge, supplierId);
    }

    @Override
    @ApiMethod
    public ServerResponse clsdMendDeliverList(String shipAddress, String beginDate, String endDate, String supplierId) {
        return webSplitDeliverService.clsdMendDeliverList(shipAddress, beginDate, endDate, supplierId);
    }

    @Override
    @ApiMethod
    public ServerResponse selectReceipt(String id) {
        return webSplitDeliverService.selectReceipt(id);
    }

    @Override
    @ApiMethod
    public ServerResponse mendDeliverDetail(String id, String cityId) {
        return webSplitDeliverService.mendDeliverDetail(id,cityId);
    }

}

