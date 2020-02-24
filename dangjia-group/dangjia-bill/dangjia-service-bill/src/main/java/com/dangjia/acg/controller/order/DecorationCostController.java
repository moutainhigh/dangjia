package com.dangjia.acg.controller.order;


import com.dangjia.acg.api.order.DecorationCostAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.order.DecorationCostService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DecorationCostController implements DecorationCostAPI {
    protected static final Logger logger = LoggerFactory.getLogger(DecorationCostController.class);

    @Autowired
    private DecorationCostService decorationCostService;
    /**
     * 查询当前花费信息
     * @param userToken
     * @param cityId
     * @param houseId
     * @param labelValId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchDecorationCostList(PageDTO pageDTO,String userToken, String cityId, String houseId, String labelValId) {
        return decorationCostService.searchDecorationCostList(pageDTO,userToken,cityId,houseId,labelValId);
    }

    /**
     * 查询当前花费列表商品信息
     */
    @Override
    @ApiMethod
    public ServerResponse searchDecorationCostProductList(String cityId,String houseId,String labelValId,String categoryId){
        return decorationCostService.searchDecorationCostProductList(cityId,houseId,labelValId,categoryId);
    }

    /**
     * 查询分类标签汇总信息
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchDecorationCategoryLabelList(String userToken,String cityId,String houseId){
        return decorationCostService.searchDecorationCategoryLabelList(userToken,cityId,houseId);
    }
    /**
     * 录入自购商品价格信息
     * @param userToken 用户TOKEN
     * @param cityId  城市ID
     * @param actuaryBudgetId 精算设置ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editPurchasePrice(String userToken, String cityId,String actuaryBudgetId,Double shopCount,Double totalPrice,Integer housekeeperAcceptance){
        return decorationCostService.editPurchasePrice(userToken,cityId,actuaryBudgetId,shopCount,totalPrice,housekeeperAcceptance);
    }

    /**
     * 精算--按工序查询精算(已支付精算）
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetWorkerList(String userToken,String cityId,String houseId){
        return decorationCostService.searchBudgetWorkerList(userToken,cityId,houseId);
    }

    /**
     * 精算--按类别查询精算(已支付精算）
     * @param userToken
     * @param cityId
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetCategoryList(String userToken,String cityId,String houseId){
        return decorationCostService.searchBudgetCategoryList(userToken,cityId,houseId);
    }

    /**
     *
     * @param userToken
     * @param type 1按工序查，2按分类查
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse selectScreeningConditions(String userToken,String houseId,Integer type){
        return decorationCostService.selectScreeningConditions(houseId,type);
    }

    /**
     * 精算--分类标签汇总信息查询
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param categoryTopId 顶级分类ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetCategoryLabelList(String userToken,String cityId,String houseId,
                                                 String workerTypeId,String categoryTopId){
        return decorationCostService.searchBudgetCategoryLabelList(userToken,cityId,houseId,workerTypeId,categoryTopId);
    }

    /**
     * 精算--分类汇总信息查询(末级分类)
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchTypeId 工种ID/顶级分类ID
     * @param labelValId 类别标签 ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetLastCategoryList(String userToken,PageDTO pageDTO,String cityId,String houseId,
                                                        String searchTypeId,String labelValId){
        return decorationCostService.searchBudgetLastCategoryList(userToken,pageDTO,cityId,houseId,searchTypeId,labelValId);
    }

    /**
     * 精算--商品
     * @param userToken 用户TOKEN
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchTypeId 工种ID/ 顶级分类ID
     * @param labelValId 类别标签 ID
     * @param categoryId 末级分类ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetProductList(String userToken,String cityId,String houseId,
                                                   String searchTypeId,String labelValId,String categoryId){
        return decorationCostService.searchBudgetProductList(userToken,cityId,houseId,searchTypeId,labelValId,categoryId);
    }
}
