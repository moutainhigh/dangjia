package com.dangjia.acg.controller.web.deliver;

import com.dangjia.acg.api.web.deliver.WebSplitReportAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.SplitDeliverReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebSplitReportController implements WebSplitReportAPI {

    @Autowired
    private SplitDeliverReportService splitDeliverReportService;

    /**
     * 指定房子所有发货的供应商
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getSplitReportSuppliers(String houseId){
        return splitDeliverReportService.getSplitReportSuppliers(houseId);
    }

    /**
     * 指定供应商所有的要货订单
     * @param supplierId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getSplitReportDeliverOrders(String houseId,String supplierId){
        return splitDeliverReportService.getSplitReportDeliverOrders(houseId,supplierId);
    }

    /**
     * 要货订单明细
     * @param number
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getSplitReportDeliverOrderItems(String number){
        return splitDeliverReportService.getSplitReportDeliverOrderItems(number);
    }



}
