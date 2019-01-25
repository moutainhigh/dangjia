package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.sup.Supplier;
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

    public Supplier getSupplier(String supplierId){
        return forMasterService.getSupplier(supplierId);
    }

    /**
     * 增加退数量
     */
    public void backCount (String houseId,String workerGoodsId,Double num){
        forMasterService.backCount(houseId,workerGoodsId,num);
    }

    /**
     * 增加补数量
     */
    public void repairCount(String houseId,String workerGoodsId,Double num){
        forMasterService.repairCount(houseId,workerGoodsId,num);
    }

    @Override
    @ApiMethod
    public Technology byTechnologyId(String technologyId){
        return forMasterService.byTechnologyId(technologyId);
    }

    @Override
    @ApiMethod
    public String brandSeriesName(String productId){
        return forMasterService.brandSeriesName(productId);
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
