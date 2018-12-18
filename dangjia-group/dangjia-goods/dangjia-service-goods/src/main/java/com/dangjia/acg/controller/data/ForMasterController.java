package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.service.data.ForMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/24 0024
 * Time: 11:42
 */
@RestController
public class ForMasterController implements ForMasterAPI {

    @Autowired
    private ForMasterService forMasterService;

    @Override
    @ApiMethod
    public String brandSeriesName(String productId){
        return forMasterService.brandSeriesName(productId);
    }
    @Override
    @ApiMethod
    public void addTechnologyRecord(String goodsId, String houseFlowId){
        forMasterService.addTechnologyRecord(goodsId,houseFlowId);
    }
    @Override
    @ApiMethod
    public WorkerGoods getWorkerGoods(String workerGoodsId){
        return forMasterService.getWorkerGoods(workerGoodsId);
    }
    @Override
    @ApiMethod
    public Goods getGoods(String goodsId){
        return forMasterService.getGoods(goodsId);
    }
    @Override
    @ApiMethod
    public Product getProduct(String productId){
        return forMasterService.getProduct(productId);
    }

    @Override
    @ApiMethod
    public List<BudgetMaterial> caiLiao(String houseFlowId){
        return forMasterService.caiLiao(houseFlowId);
    }

    @Override
    @ApiMethod
    public List<BudgetWorker> renGong(String houseFlowId){
       return forMasterService.renGong(houseFlowId);
    }

    @Override
    @ApiMethod
    public Double getBudgetWorkerPrice(String houseId, String workerTypeId, String cityId){
        return forMasterService.getBudgetWorkerPrice(houseId,workerTypeId);
    }
    @Override
    @ApiMethod
    public Double getBudgetCaiPrice(String houseId, String workerTypeId,String cityId){
        return forMasterService.getBudgetCaiPrice(houseId,workerTypeId);
    }
    @Override
    @ApiMethod
    public Double getBudgetSerPrice(String houseId,String workerTypeId,String cityId){
        return forMasterService.getBudgetSerPrice(houseId,workerTypeId);
    }



}
