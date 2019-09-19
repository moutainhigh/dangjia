package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjActuaryBudgetMaterialAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
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

    /**
     * 查询精算首页列表
     * @param request
     * @param bclId
     * @param categoryId
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMakeBudgetsList(HttpServletRequest request,String bclId,String categoryId,String houseId){
        return djActuaryBudgetMaterialService.queryMakeBudgetsList(bclId,categoryId,houseId);
    }


    /**
     * 第四部分：二级商品列表搜索页面
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryBasicsProduct(HttpServletRequest request,
                                             String productId,
                                             PageDTO pageDTO,
                                             String cityId,
                                             String categoryId,
                                             String name,
                                             String attributeVal,
                                             String brandVal,
                                             String orderKey){
        return djActuaryBudgetMaterialService.queryBasicsProduct(productId,pageDTO,cityId,categoryId,name,attributeVal,brandVal,orderKey);
    }

}
