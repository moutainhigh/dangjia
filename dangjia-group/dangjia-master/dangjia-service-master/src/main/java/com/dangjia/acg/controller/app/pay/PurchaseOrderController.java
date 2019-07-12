package com.dangjia.acg.controller.app.pay;

import com.dangjia.acg.api.app.pay.PurchaseOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.pay.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ruking.Cheng
 * @descrilbe 购买单
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/29 11:50 AM
 */
@RestController
public class PurchaseOrderController implements PurchaseOrderAPI {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Override
    @ApiMethod
    public ServerResponse getBudgetMaterialList(String houseId) {
        return purchaseOrderService.getBudgetMaterialList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse setPurchaseOrder(String houseId, String budgetIds) {
        return purchaseOrderService.setPurchaseOrder(houseId, budgetIds);
    }
}
