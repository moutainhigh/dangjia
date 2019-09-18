package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjActuaryBudgetMaterialAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjActuaryBudgetMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/17
 * Time: 14:19
 */
@RestController
public class DjActuaryBudgetMaterialController implements DjActuaryBudgetMaterialAPI {
    @Autowired
    private DjActuaryBudgetMaterialService djActuaryBudgetMaterialService;

    @Override
    @ApiMethod
    public ServerResponse makeBudgets(HttpServletRequest request, String actuarialTemplateId, String houseId, String workerTypeId, String listOfGoods) {
        return djActuaryBudgetMaterialService.makeBudgets(actuarialTemplateId, houseId, workerTypeId, listOfGoods);
    }
}
