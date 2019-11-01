package com.dangjia.acg.controller.actuary.app;

import com.dangjia.acg.api.actuary.app.AppBudgetMaterialAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.BudgetMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * /**
 *  * author: chenyufeng
 *  * Date: 2019/10/31
 *
 */
@RestController
public class AppBudgetMaterialController implements AppBudgetMaterialAPI {

    @Autowired
    private BudgetMaterialService budgetMaterialService;

    @Override
    @ApiMethod
    public ServerResponse queryActuarial(HttpServletRequest request,String cityId ,String houseId, String productType) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse conconfirmedActuarial(HttpServletRequest request,String cityId, String houseId, String productType) {
        return null;
    }

    @Override
    public ServerResponse getHouseBudgetStageCost(HttpServletRequest request, String cityId, String houseId, String workerTypeId) {
        return budgetMaterialService.getHouseBudgetStageCost(houseId,workerTypeId);
    }
}
