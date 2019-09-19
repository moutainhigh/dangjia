package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
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
    public String getUnitName(String cityId,String unitId){
        return forMasterService.getUnitName(unitId);
    }

    @Override
    @ApiMethod
    public SupplierProduct getSupplierProduct(String cityId,String supplierId, String productId){
        return forMasterService.getSupplierProduct(supplierId,productId);
    }

    @Override
    public Supplier getSupplier(String cityId,String supplierId){
        return forMasterService.getSupplier(supplierId);
    }

    /**
     * 增加退数量
     */
    @Override
    @ApiMethod
    public void backCount (String cityId,String houseId,String workerGoodsId,Double num){
        forMasterService.backCount(houseId,workerGoodsId,num);
    }

    /**
     * 增加补数量
     */
    @Override
    @ApiMethod
    public void repairCount(String cityId,String houseId,String workerGoodsId,Double num){
        forMasterService.repairCount(houseId,workerGoodsId,num);
    }

    @Override
    @ApiMethod
    public Technology byTechnologyId(String cityId,String technologyId){
        return forMasterService.byTechnologyId(technologyId);
    }

    @Override
    @ApiMethod
    public String brandSeriesName(String cityId,String productId){
        return forMasterService.brandSeriesName(productId);
    }
    @Override
    @ApiMethod
    public String brandName(String cityId,String productId){
        return forMasterService.brandName(productId);
    }
   @Override
    @ApiMethod
    public ProductWorkerDTO getWorkerGoods(String cityId, String workerGoodsId){
        return forMasterService.getWorkerGoods(workerGoodsId);
    }

    @Override
    @ApiMethod
    public ServerResponse setProductOrWorkerGoodsIsTop(String gid, Integer type,String istop){
        forMasterService.setProductOrWorkerGoodsIsTop(gid,type,istop);
        return ServerResponse.createBySuccessMessage("置顶设置更新成功");
    }
    @Override
    @ApiMethod
    public Goods getGoods(String cityId,String goodsId){
        return forMasterService.getGoods(goodsId);
    }
    @Override
    @ApiMethod
    public Product getProduct(String cityId, String productId){
        return forMasterService.getProduct(productId);
    }

    @Override
    @ApiMethod
    public List<BudgetMaterial> caiLiao(String cityId,String houseFlowId){
        return forMasterService.caiLiao(houseFlowId);
    }

    @Override
    @ApiMethod
    public List<BudgetWorker> renGong(String cityId,String houseFlowId){
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
    public Double nonPaymentCai(String houseId, String workerTypeId,String cityId){
        return forMasterService.nonPaymentCai(houseId,workerTypeId);
    }
    @Override
    @ApiMethod
    public Double getBudgetSerPrice(String houseId,String workerTypeId,String cityId){
        return forMasterService.getBudgetSerPrice(houseId,workerTypeId);
    }
    @Override
    @ApiMethod
    public Double nonPaymentSer(String houseId,String workerTypeId,String cityId){
        return forMasterService.nonPaymentSer(houseId,workerTypeId);
    }
    @Override
    @ApiMethod
    public Double getNotSerPrice(String houseId,String workerTypeId,String cityId){
        return forMasterService.getNotSerPrice(houseId,workerTypeId);
    }

    @Override
    @ApiMethod
    public Double getNotCaiPrice(String houseId, String workerTypeId,String cityId){
        return forMasterService.getNotCaiPrice(houseId,workerTypeId);
    }

}
