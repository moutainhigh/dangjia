package com.dangjia.acg.service.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.deliver.IOrderItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.house.IMaterialRecordMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.MaterialRecord;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.repair.MendMateriel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/7
 * Time: 9:19
 */
@Service
public class MasterProductService {
    @Autowired
    private IMendMaterialMapper iMendMaterialMapper;
    @Autowired
    private IWarehouseMapper iWarehouseMapper;
    @Autowired
    private ISurplusWareHouseItemMapper iSurplusWareHouseItemMapper;
    @Autowired
    private IMaterialRecordMapper iMaterialRecordMapper;
    @Autowired
    private IOrderSplitItemMapper iOrderSplitItemMapper;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;


    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateProductByProductId(String id, String  categoryId, String brandSeriesId, String brandId,
                                                   String name, String unitId, String unitName) throws RuntimeException {
        try {
            Product product=new Product();
            product.setId(id);
            product.setCategoryId(categoryId);
            product.setBrandSeriesId(brandSeriesId);
            product.setBrandId(brandId);
            product.setName(name);
            product.setUnitId(unitId);
            product.setUnitName(unitName);
            Example example=new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.PRODUCT_ID,product.getId());
            List<MendMateriel> mendMateriels = iMendMaterialMapper.selectByExample(example);
            if(mendMateriels.size()>0||null!=mendMateriels) {
                for (MendMateriel mendMateriel : mendMateriels) {
                    if (!(mendMateriel.getProductName().equals(product.getName())) || !(mendMateriel.getUnitName().equals(product.getUnitName()))) {
                        mendMateriel.setProductId(product.getId());
                        mendMateriel.setProductName(product.getName());
                        mendMateriel.setUnitName(product.getUnitName());
                        iMendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
                    }
                }
            }
            example.createCriteria().andEqualTo(Warehouse.PRODUCT_ID,product.getId());
            List<Warehouse> warehouses = iWarehouseMapper.selectByExample(example);
            if(warehouses.size()>0||null!=warehouses) {
                for (Warehouse warehouse : warehouses) {
                    if (!(warehouse.getUnitName().equals(product.getName())) || !(warehouse.getUnitName().equals(product.getUnitName()))) {
                        warehouse.setProductId(product.getId());
                        warehouse.setProductName(product.getName());
                        warehouse.setUnitName(product.getUnitName());
                        iWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }
                }
            }
            example.createCriteria().andEqualTo(SurplusWareHouseItem.PRODUCT_ID,product.getId());
            List<SurplusWareHouseItem> surplusWareHouseItems = iSurplusWareHouseItemMapper.selectByExample(example);
            if(surplusWareHouseItems.size()>0||null!=surplusWareHouseItems) {
                for (SurplusWareHouseItem surplusWareHouseItem : surplusWareHouseItems) {
                    if (!(surplusWareHouseItem.getProductName().equals(product.getName()))) {
                        surplusWareHouseItem.setProductId(product.getId());
                        surplusWareHouseItem.setProductName(product.getName());
                        iSurplusWareHouseItemMapper.updateByPrimaryKeySelective(surplusWareHouseItem);
                    }
                }
            }
            example.createCriteria().andEqualTo(MaterialRecord.PRODUCT_ID,product.getId());
            List<MaterialRecord> materialRecords = iMaterialRecordMapper.selectByExample(example);
            if(materialRecords.size()>0||null!=materialRecords) {
                for (MaterialRecord materialRecord : materialRecords) {
                    materialRecord.setProductId(product.getId());
                    materialRecord.setProductName(product.getName());
                    iMaterialRecordMapper.updateByPrimaryKeySelective(materialRecord);
                }
            }
            example.createCriteria().andEqualTo(OrderSplitItem.PRODUCT_ID,product.getId());
            List<OrderSplitItem> orderSplitItems = iOrderSplitItemMapper.selectByExample(example);
            if(orderSplitItems.size()>0||null!=orderSplitItems) {
                for (OrderSplitItem orderSplitItem : orderSplitItems) {
                    if (!(orderSplitItem.getProductName().equals(product.getName())) || !(orderSplitItem.getUnitName().equals(product.getUnitName()))) {
                        orderSplitItem.setProductId(product.getId());
                        orderSplitItem.setProductName(product.getName());
                        orderSplitItem.setUnitName(product.getUnitName());
                        iOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                    }
                }
            }
            example.createCriteria().andEqualTo(OrderItem.PRODUCT_ID,product.getId());
            List<OrderItem> orderItems = iOrderItemMapper.selectByExample(example);
            if(orderItems.size()>0||null!=orderItems) {
                for (OrderItem orderItem : orderItems) {
                    if (!(orderItem.getProductName().equals(product.getName())) || !(orderItem.getUnitName().equals(product.getUnitName()))) {
                        orderItem.setProductId(product.getId());
                        orderItem.setProductName(product.getName());
                        orderItem.setUnitName(product.getUnitName());
                        iOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("更新成功");
        } catch (Exception e) {
           return ServerResponse.createByErrorMessage("更新失败");
        }
    }

}
