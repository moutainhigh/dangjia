package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.deliver.IOrderItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IProductChangeMapper;
import com.dangjia.acg.mapper.house.IMaterialRecordMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.ProductChange;
import com.dangjia.acg.modle.house.MaterialRecord;
import com.dangjia.acg.modle.house.SurplusWareHouse;
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
    @Autowired
    private IProductChangeMapper iProductChangeMapper;


    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateProductByProductId(String products,String brandSeriesId,String brandId,String goodsId,String id) throws RuntimeException {
        try {
            JSONArray lists = JSONArray.parseArray(products);
            iProductChangeMapper.updateProductNameById(lists,brandSeriesId,brandId,goodsId,id);
            iProductChangeMapper.updateProductNameById2(lists,brandSeriesId,brandId,goodsId,id);
            iMendMaterialMapper.updateMendMaterialById(lists,brandSeriesId,brandId,goodsId,id);
            iWarehouseMapper.updateWareHouseById(lists,brandSeriesId,brandId,goodsId,id);
            iSurplusWareHouseItemMapper.updateSurplusWareHouseItemById(lists,brandSeriesId,brandId,goodsId,id);
            iMaterialRecordMapper.updateMaterialRecordById(lists,brandSeriesId,brandId,goodsId,id);
            iOrderSplitItemMapper.updateOrderSplitItemById(lists,brandSeriesId,brandId,goodsId,id);
            iOrderItemMapper.updateOrderItemById(lists,brandSeriesId,brandId,goodsId,id);
            return ServerResponse.createBySuccessMessage("更新成功");
        } catch (Exception e) {
           return ServerResponse.createByErrorMessage("更新失败");
        }
    }

}
