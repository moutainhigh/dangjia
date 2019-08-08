package com.dangjia.acg.service.data;

import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.mapper.sup.ISupplierMapper;
import com.dangjia.acg.mapper.sup.ISupplierProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/24 0024
 * Time: 11:47
 * 给master提供精算数据 修改精算数据
 */
@Service
public class ForMasterService {

    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private ITechnologyMapper technologyMapper;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private IBrandSeriesMapper brandSeriesMapper;
    @Autowired
    private ISupplierMapper supplierMapper;
    @Autowired
    private ISupplierProductMapper supplierProductMapper;
    @Autowired
    private IUnitMapper unitMapper;


    public String getUnitName(String unitId){
        Unit unit = unitMapper.selectByPrimaryKey(unitId);
        return unit.getName();
    }


    public SupplierProduct getSupplierProduct(String supplierId,String productId){
        return supplierProductMapper.getSupplierProduct(supplierId,productId);
    }

    public Supplier getSupplier(String supplierId){
        return supplierMapper.selectByPrimaryKey(supplierId);
    }

    /**
     * 增加退数量
     */
    public void backCount (String houseId,String workerGoodsId,Double num){
        BudgetWorker budgetWorker = budgetWorkerMapper.byWorkerGoodsId(houseId,workerGoodsId);
        budgetWorker.setBackCount(budgetWorker.getBackCount() + num);
        budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
    }

    /**
     * 增加补数量
     */
    public void repairCount(String houseId,String workerGoodsId,Double num){
        BudgetWorker budgetWorker = budgetWorkerMapper.byWorkerGoodsId(houseId,workerGoodsId);
        budgetWorker.setRepairCount(budgetWorker.getRepairCount() + num);
        budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
    }

    public Technology byTechnologyId(String technologyId){
        return technologyMapper.selectByPrimaryKey(technologyId);
    }

    public String brandSeriesName(String productId){
        return brandSeriesMapper.brandSeriesName(productId);
    }
    public String brandName(String productId){
        return brandSeriesMapper.brandName(productId);
    }

    /**
     * 设置材料或者人工商品置顶或取消置顶
     * @param gid 商品ID
     * @param type 0=材料商品  1=人工商品
     */
    public void setProductOrWorkerGoodsIsTop(String gid,Integer type,String istop){
        if(type==0){
            Product product= productMapper.selectByPrimaryKey(gid);
            if(product!=null){
                product.setIstop(istop);
                productMapper.updateByPrimaryKeySelective(product);
            }
        }else{
            WorkerGoods workerGoods= workerGoodsMapper.selectByPrimaryKey(gid);
            if(workerGoods!=null){
                workerGoods.setIstop(istop);
                workerGoodsMapper.updateByPrimaryKeySelective(workerGoods);
            }
        }
    }
    public WorkerGoods getWorkerGoods(String workerGoodsId){
        return workerGoodsMapper.selectByPrimaryKey(workerGoodsId);
    }
    public Goods getGoods(String goodsId){
        return goodsMapper.selectByPrimaryKey(goodsId);
    }
    public Product getProduct(String productId){
        return productMapper.selectByPrimaryKey(productId);
    }

    /**
     * 支付回调获取材料精算
     */
    public List<BudgetMaterial> caiLiao(String houseFlowId){
        try{
            Example example = new Example(BudgetMaterial.class);
            example.createCriteria()
                    .andEqualTo(BudgetMaterial.HOUSE_FLOW_ID, houseFlowId)
                    .andEqualTo(BudgetMaterial.DELETE_STATE, 0)
                    .andEqualTo(BudgetMaterial.STETA,1);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);
            for (BudgetMaterial budgetMaterial : budgetMaterialList){
                Product product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());
                if(product == null){
                    budgetMaterialList.remove(budgetMaterial);//移除
                    budgetMaterial.setDeleteState(1);//找不到商品标记删除
                    budgetMaterial.setModifyDate(new Date());
                    budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
                }else {
                    //重新记录支付时精算价格
                    budgetMaterial.setPrice(product.getPrice());
                    budgetMaterial.setCost(product.getCost());
                    budgetMaterial.setTotalPrice(budgetMaterial.getConvertCount() * product.getPrice());//已支付 记录总价
                    budgetMaterial.setDeleteState(3);//已支付
                    budgetMaterial.setModifyDate(new Date());
                    budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
                }
            }
            return budgetMaterialList;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    /**
     * 支付回调修改人工精算
     * 业主取消的又改为待付款
     */
    public List<BudgetWorker> renGong(String houseFlowId){
        try{
            Example example = new Example(BudgetWorker.class);
            example.createCriteria().andEqualTo(BudgetWorker.HOUSE_FLOW_ID, houseFlowId).andEqualTo(BudgetWorker.DELETE_STATE, 0);
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
            for(BudgetWorker budgetWorker : budgetWorkerList){
                WorkerGoods wg = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());
                if (wg == null){
                    budgetWorkerList.remove(budgetWorker);//移除
                    budgetWorker.setDeleteState(1);//找不到商品标记删除
                    budgetWorker.setModifyDate(new Date());
                    budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
                }else {
                    budgetWorker.setPrice(wg.getPrice());
                    budgetWorker.setTotalPrice(budgetWorker.getShopCount() * wg.getPrice());
                    budgetWorker.setDeleteState(3);//已支付
                    budgetWorker.setModifyDate(new Date());
                    budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);
                }
            }
            return budgetWorkerList;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    /**
     * 支付时工种人工总价
     */
    public Double getBudgetWorkerPrice(String houseId, String workerTypeId){
        return budgetWorkerMapper.getBudgetWorkerPrice(houseId,workerTypeId);
    }
    /**
     * 支付时工种材料总价
     */
    public Double getBudgetCaiPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getBudgetCaiPrice(houseId,workerTypeId);
    }
    public Double nonPaymentCai(String houseId,String workerTypeId){
        return budgetMaterialMapper.nonPaymentCai(houseId,workerTypeId);
    }


    /**
     * 支付时工种服务总价
     */
    public Double getBudgetSerPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getBudgetSerPrice(houseId,workerTypeId);
    }
    public Double nonPaymentSer(String houseId,String workerTypeId){
        return budgetMaterialMapper.nonPaymentSer(houseId,workerTypeId);
    }
    public Double getNotSerPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getNotSerPrice(houseId,workerTypeId);
    }
    /**
     * 支付时工种未选择材料总价
     */
    public Double getNotCaiPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getNotCaiPrice(houseId,workerTypeId);
    }
}
