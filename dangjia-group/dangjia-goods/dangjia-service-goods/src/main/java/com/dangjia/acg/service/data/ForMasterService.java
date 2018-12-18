package com.dangjia.acg.service.data;

import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    private TechnologyRecordAPI technologyRecordAPI;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private IBrandSeriesMapper brandSeriesMapper;


    public String brandSeriesName(String productId){
        return brandSeriesMapper.brandSeriesName(productId);
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
        Example example = new Example(BudgetMaterial.class);
        example.createCriteria().andEqualTo(BudgetMaterial.HOUSE_FLOW_ID, houseFlowId).andEqualTo(BudgetMaterial.DELETE_STATE, 0)
            .andEqualTo(BudgetMaterial.STETA,1);
        List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);

        for (BudgetMaterial budgetMaterial : budgetMaterialList){
            Product product = productMapper.selectByPrimaryKey(budgetMaterial.getProductId());
            //重新记录支付时价格
            budgetMaterial.setPrice(product.getPrice());
            budgetMaterial.setCost(product.getCost());
            budgetMaterial.setTotalPrice(budgetMaterial.getShopCount() * product.getPrice());//已支付 记录总价
            budgetMaterial.setDeleteState(3);//已支付
            budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);

            //查询验收节点加入节点记录
            addTechnologyRecord(product.getId(), houseFlowId);
        }

        return budgetMaterialList;
    }

    /**
     * 支付回调修改人工精算
     * 业主取消的又改为待付款
     */
    public List<BudgetWorker> renGong(String houseFlowId){
        Example example = new Example(BudgetWorker.class);
        example.createCriteria().andEqualTo(BudgetWorker.HOUSE_FLOW_ID, houseFlowId).andEqualTo(BudgetWorker.DELETE_STATE, 0);
        List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
        for(BudgetWorker budgetWorker : budgetWorkerList){
            WorkerGoods wg = workerGoodsMapper.selectByPrimaryKey(budgetWorker.getWorkerGoodsId());
            budgetWorker.setPrice(wg.getPrice());
            budgetWorker.setTotalPrice(budgetWorker.getShopCount() * wg.getPrice());
            budgetWorker.setDeleteState(3);//已支付
            budgetWorkerMapper.updateByPrimaryKeySelective(budgetWorker);

            //查询验收节点加入节点记录
            addTechnologyRecord(wg.getId(), houseFlowId);
        }

        //业主取消的材料又改为待付款
        budgetMaterialMapper.updateSelf(houseFlowId);

        return budgetWorkerList;
    }
    /**
     * 保存工艺验收节点
     * productId  workerGoodsId 共用 goodsId
     * */
    public void addTechnologyRecord(String goodsId, String houseFlowId){
        List<Technology> technologyList = technologyMapper.queryTechnologyList(goodsId);
        for (Technology technology : technologyList){
            TechnologyRecord technologyRecord = new TechnologyRecord();
            technologyRecord.setTechnologyId(technology.getId());
            technologyRecord.setName(technology.getName());
            technologyRecord.setMaterialOrWorker(technology.getMaterialOrWorker());
            technologyRecord.setWorkerTypeId(technology.getWorkerTypeId());
            technologyRecord.setContent(technology.getContent());
            technologyRecord.setType(technology.getType());
            technologyRecord.setImage(technology.getImage());
            technologyRecord.setState(0);//初始状态
            technologyRecord.setHouseFlowId(houseFlowId);
            technologyRecordAPI.addTechnologyRecord(technologyRecord);//保存验收节点
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
    /**
     * 支付时工种服务总价
     */
    public Double getBudgetSerPrice(String houseId,String workerTypeId){
        return budgetMaterialMapper.getBudgetSerPrice(houseId,workerTypeId);
    }
}
