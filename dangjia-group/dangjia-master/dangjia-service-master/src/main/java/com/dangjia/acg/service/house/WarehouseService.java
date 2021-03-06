package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.actuary.ActuaryOpeAPI;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.budget.BudgetItemDTO;
import com.dangjia.acg.dto.budget.GoodsItemDTO;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.dto.house.WarehouseGoodsDTO;
import com.dangjia.acg.mapper.deliver.IOrderItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IProductChangeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IMaterialRecordMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.MaterialRecord;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private GoodsCategoryAPI goodsCategoryAPI;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private ActuaryOpeAPI actuaryOpeAPI;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IProductChangeMapper productChangeMapper;

    @Autowired
    private IMendWorkerMapper mendWorkerMapper;

    @Autowired
    private IMaterialRecordMapper materialRecordMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IMendMaterialMapper mendMaterielMapper;


    private static Logger LOG = LoggerFactory.getLogger(WarehouseService.class);


    /**
     * 查询仓库剩余总金额
     */
    public ServerResponse checkWarehouseSurplus(String userToken,  String houseId) {
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("未找到该房产");
            }
            Example example = new Example(Warehouse.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
            List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
            Double tolPrice=0d;
            for (Warehouse warehouse : warehouseList) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());//剩余数量 所有买的数量 - 业主退货 - 要的
                warehouseDTO.setTolPrice(warehouseDTO.getSurCount() * warehouse.getPrice());
                if(warehouseDTO.getSurCount()>0) {
                    tolPrice = tolPrice + warehouseDTO.getTolPrice();
                }
            }
            return ServerResponse.createBySuccess("查询成功", tolPrice);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询仓库材料
     * type 0材料 1包工包料 2所有
     */
    public ServerResponse warehouseList(String userToken, PageDTO pageDTO, String houseId, String categoryId, String name, String type) {
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("未找到该房产");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Example example = new Example(Warehouse.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
            if (!CommonUtil.isEmpty(type)) {
                criteria.andEqualTo(Warehouse.PRODUCT_TYPE, type);
            }
            if (!CommonUtil.isEmpty(categoryId)) {
                criteria.andEqualTo(Warehouse.CATEGORY_ID, categoryId);
            }
            if (!CommonUtil.isEmpty(name)) {
                criteria.andLike(Warehouse.PRODUCT_NAME, "%" + name + "%");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
            LOG.info(" warehouseList size:" + warehouseList.size());
            PageInfo pageResult = new PageInfo(warehouseList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            for (Warehouse warehouse : warehouseList) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                BeanUtils.beanToBean(warehouse, warehouseDTO);
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setBackCount((warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));
                warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());//剩余数量 所有买的数量 - 业主退货 - 要的
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), warehouse.getProductId()));
                warehouseDTO.setRepairCount(warehouse.getRepairCount());
                Product product = forMasterAPI.getProduct(house.getCityId(), warehouse.getProductId());
                if (product != null) {
                    Goods goods = forMasterAPI.getGoods(house.getCityId(), product.getGoodsId());
                    if (goods != null) {
                        warehouseDTO.setSales(goods.getSales());
                    }
                }
                warehouseDTOS.add(warehouseDTO);
            }
            pageResult.setList(warehouseDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 查询仓库材料（已购买）
     * type 0材料 1包工包料 2人工
     */
    public ServerResponse warehouseGmList(HttpServletRequest request, String userToken, String houseId, String name, String type) {
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("未找到该房产");
            }
            request.setAttribute(Constants.CITY_ID,house.getCityId());
            Map<String, Map> maps = new HashMap<>();
            Map map = new HashMap();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if (!CommonUtil.isEmpty(type) && "2".equals(type)) {
                List<BudgetItemDTO> budgetItemDTOS = actuaryOpeAPI.getHouseWorkerInfo(house.getCityId(), "3", houseId, address);
                for (BudgetItemDTO budgetItemDTO : budgetItemDTOS) {
                    Double rowPrice =0d;
                    List<MendWorker> budgetWorkerList =mendWorkerMapper.mendWorkerList(houseId,budgetItemDTO.getWorkerTypeId());
                    List<GoodsItemDTO> goodsItemDTOList = budgetItemDTO.getGoodsItemDTOList();
                    Map<String,GoodsItemDTO> goodsItemDTOMap=new HashMap<>();
                    for (GoodsItemDTO goodsItemDTO : goodsItemDTOList) {
                        goodsItemDTOMap.put(goodsItemDTO.getId(),goodsItemDTO);
                    }
                    //从list集合中，取出字段name的列表
                    List<String> ids = goodsItemDTOList.stream().map(p -> p.getId()).collect(Collectors.toList());

                    for (MendWorker budgetWorker : budgetWorkerList) {
                        if(!ids.contains(budgetWorker.getWorkerGoodsId())){
                            MendWorker mendWorker=mendMaterielMapper.getMendWorker(budgetWorker.getWorkerGoodsId(),houseId);
                            GoodsItemDTO goodsItemDTO = goodsItemDTOMap.get(budgetWorker.getWorkerGoodsId());
                            if(goodsItemDTO==null){
                                goodsItemDTO = new GoodsItemDTO();
                            }
                            goodsItemDTO.setGoodsImage(address + mendWorker.getImage());
                            goodsItemDTO.setGoodsName(mendWorker.getWorkerGoodsName());
                            goodsItemDTO.setConvertCount(mendWorker.getShopCount());
                            goodsItemDTO.setPrice(mendWorker.getPrice());
                            goodsItemDTO.setUnitName(mendWorker.getUnitName());
                            goodsItemDTO.setId(mendWorker.getWorkerGoodsId());//人工商品id
                            goodsItemDTO.setShopCount(0d);
                            goodsItemDTO.setBackCount(0d);
                            goodsItemDTO.setRepairCount(mendWorker.getShopCount());
                            goodsItemDTO.setSurCount(mendWorker.getShopCount());
                            goodsItemDTO.setTolPrice(goodsItemDTO.getSurCount()*goodsItemDTO.getPrice());
                            rowPrice+=goodsItemDTO.getTolPrice();
                            goodsItemDTOMap.put(goodsItemDTO.getId(),goodsItemDTO);
                            goodsItemDTOList.add(goodsItemDTO);
                        }
                    }
                    budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
                    budgetItemDTO.setRowPrice(budgetItemDTO.getRowPrice()+rowPrice);
                }
                map.put("goodsItemDTOList", budgetItemDTOS);
            } else {
                Example example = new Example(Warehouse.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
                if (!CommonUtil.isEmpty(type)) {
                    criteria.andEqualTo(Warehouse.PRODUCT_TYPE, type);
                }
                if (!CommonUtil.isEmpty(name)) {
                    criteria.andLike(Warehouse.PRODUCT_NAME, "%" + name + "%");
                }
                List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                LOG.info(" warehouseList size:" + warehouseList.size());
                List<String> categoryIdList = warehouseMapper.categoryIdList(houseId);
                for (String categoryId : categoryIdList) {
                    //获取低级类别
                    GoodsCategory goodsCategoryNext = goodsCategoryAPI.getGoodsCategory(house.getCityId(), categoryId);
                    if (goodsCategoryNext == null) {
                        continue;
                    }
                    //获取顶级类别
                    GoodsCategory goodsCategoryParentTop = goodsCategoryAPI.getGoodsCategory(house.getCityId(), goodsCategoryNext.getParentTop());
                    GoodsCategory goodsCategory;
                    if (goodsCategoryParentTop == null) {
                        goodsCategory = goodsCategoryNext;
                    } else {
                        goodsCategory = goodsCategoryParentTop;
                    }
                    //重临时缓存maps中取出BudgetItemDTO
                    Map budgetItemDTO = maps.get(goodsCategory.getId());
                    BigDecimal rowPrice = new BigDecimal(0);
                    List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
                    if (budgetItemDTO == null) {
                        //如果没有将BudgetItemDTO初始化
                        budgetItemDTO = new HashMap();
                        budgetItemDTO.put("rowImage", address + goodsCategory.getImage());
                        budgetItemDTO.put("rowName", goodsCategory.getName());
                        budgetItemDTO.put("rowPrice", rowPrice);
                    } else {
                        rowPrice = (BigDecimal) budgetItemDTO.get("rowPrice");
                        warehouseDTOS = (List<WarehouseDTO>) budgetItemDTO.get("goodsItems");
                    }
                    for (Warehouse warehouse : warehouseList) {
                        if (!categoryId.equals(warehouse.getCategoryId())) continue;
                        WarehouseDTO warehouseDTO = new WarehouseDTO();
                        warehouse.setOwnerBack((warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));
                        warehouse.setWorkBack((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                        BeanUtils.beanToBean(warehouse, warehouseDTO);
                        warehouseDTO.setImage(address + warehouse.getImage());
                        warehouseDTO.setShopCount(warehouse.getShopCount());
                        warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                        warehouseDTO.setBackCount((warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));
                        warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                        warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                        warehouseDTO.setTolPrice((warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack())  - warehouse.getWorkBack()) * warehouse.getPrice());
                        warehouseDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), warehouse.getProductId()));
                        // type为空时查询 是否更换
                        if (CommonUtil.isEmpty(type)) {
                            if (productChangeMapper.queryProductChangeExist(warehouse.getHouseId(), warehouse.getProductId(), "0") > 0) {
                                warehouseDTO.setChangeType(1);
                            }
                        }
                        warehouseDTOS.add(warehouseDTO);
                        rowPrice = rowPrice.add(new BigDecimal(warehouseDTO.getTolPrice()));
                    }
                    budgetItemDTO.put("rowPrice", rowPrice);
                    budgetItemDTO.put("goodsItems", warehouseDTOS);
                    maps.put(goodsCategory.getId(), budgetItemDTO);
                }
                List<Map> budgetItemDTOList = new ArrayList<>();
                for (Map.Entry<String, Map> entry : maps.entrySet()) {
                    budgetItemDTOList.add(entry.getValue());
                }
                map.put("goodsItemDTOList", budgetItemDTOList);
            }

            Double workerPrice = actuaryOpeAPI.getHouseWorkerPrice(house.getCityId(), "3", houseId);
            workerPrice+=mendWorkerMapper.getMendWorkerPrice(houseId);
            Double caiPrice = warehouseMapper.getHouseGoodsPrice(houseId, name);
            Double totalPrice = workerPrice + caiPrice;
            map.put("workerPrice", workerPrice);
            map.put("caiPrice", caiPrice);
            map.put("totalPrice", totalPrice);
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }





    public ServerResponse editProductData(String cityId, String productJson) {

        Product product = JSON.parseObject(productJson, Product.class);
        if(product!=null) {
            Example example = new Example(MaterialRecord.class);
            example.createCriteria().andEqualTo(MaterialRecord.CITY_ID, cityId)
                    .andEqualTo(MaterialRecord.PRODUCT_ID, product.getId());
            MaterialRecord materialRecord = new MaterialRecord();
            materialRecord.setId(null);
            materialRecord.setCreateDate(null);
            materialRecord.setModifyDate(new Date());
            materialRecord.setProductName(product.getName());
            materialRecordMapper.updateByExampleSelective(materialRecord, example);

            example = new Example(OrderItem.class);
            example.createCriteria().andEqualTo(OrderItem.CITY_ID, cityId)
                    .andEqualTo(OrderItem.PRODUCT_ID, product.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setId(null);
            orderItem.setCreateDate(null);
            orderItem.setModifyDate(new Date());
            orderItem.setProductName(product.getName());
            orderItem.setImage(product.getImage());
            orderItemMapper.updateByExampleSelective(orderItem, example);

            example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.CITY_ID, cityId)
                    .andEqualTo(OrderSplitItem.PRODUCT_ID, product.getId());
            OrderSplitItem orderSplitItem = new OrderSplitItem();
            orderSplitItem.setId(null);
            orderSplitItem.setCreateDate(null);
            orderSplitItem.setProductName(product.getName());
            orderSplitItem.setModifyDate(new Date());
            orderSplitItem.setImage(product.getImage());
            orderSplitItemMapper.updateByExampleSelective(orderSplitItem, example);

            example = new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.CITY_ID, cityId)
                    .andEqualTo(MendMateriel.PRODUCT_ID, product.getId());
            MendMateriel mendMateriel = new MendMateriel();
            mendMateriel.setId(null);
            mendMateriel.setCreateDate(null);
            mendMateriel.setModifyDate(new Date());
            mendMateriel.setProductName(product.getName());
            mendMateriel.setImage(product.getImage());
            mendMaterielMapper.updateByExampleSelective(mendMateriel, example);


            example = new Example(Warehouse.class);
            example.createCriteria().andEqualTo(Warehouse.CITY_ID, cityId)
                    .andEqualTo(Warehouse.PRODUCT_ID, product.getId());
            Warehouse warehouse = new Warehouse();
            warehouse.setId(null);
            warehouse.setCreateDate(null);
            warehouse.setModifyDate(new Date());
            warehouse.setProductName(product.getName());
            warehouse.setImage(product.getImage());
            warehouseMapper.updateByExampleSelective(warehouse, example);
        }

//         dj_house_surplus_ware_house_item ;
//         dj_house_surplus_ware_divert ;
//         dj_deliver_cart ;
//         dj_deliver_product_change ;
        return ServerResponse.createBySuccessMessage("查询成功");
    }


    /**
     * 查询指定已购买的商品仓库明细
     * @param houseId 工地ID
     * @param gid 商品ID
     * @param type 类型：  0 = 材料/包工包料   1=人工
     * @return
     */
    public ServerResponse getWarehouseData(String houseId, String gid, Integer type){

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        if(type==0){
            Example example = new Example(Warehouse.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
            criteria.andEqualTo(Warehouse.PRODUCT_ID, gid);
            List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
            Map warehouseDTO = new HashMap();
            warehouseDTO.put("type",type);
            if(warehouseList.size()>0) {
                for (Warehouse warehouse : warehouseList) {
                    warehouse.setOwnerBack((warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));
                    warehouse.setWorkBack((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                    warehouseDTO.put("name", warehouse.getProductName());//商品名字
                    warehouseDTO.put("image", address+warehouse.getImage());//图片地址

                    warehouseDTO.put("repairCount", warehouse.getRepairCount());//补货数
                    warehouseDTO.put("shopCount", warehouse.getShopCount());//购买数
                    warehouseDTO.put("receive", warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));//收货数
                    warehouseDTO.put("workBack", (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));//工匠退货数
                    warehouseDTO.put("ownerBack", (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()));//业主退货数
                    warehouseDTO.put("surCount", warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());//剩余数
                    warehouseDTO.put("tolPrice", (warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack())  - warehouse.getWorkBack()) * warehouse.getPrice());//实际花费
                }
                List<WarehouseGoodsDTO> goodsDTOS=mendMaterielMapper.getWarehouseGoods(gid,houseId);
                warehouseDTO.put("list",goodsDTOS);
                return ServerResponse.createBySuccess("查询成功", warehouseDTO);
            }
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }else{
            House house = houseMapper.selectByPrimaryKey(houseId);
            BudgetWorker budgetWorker= budgetWorkerAPI.getHouseBudgetWorkerId(house.getCityId(),house.getId(),gid);
            Map warehouseDTO = new HashMap();
            if(budgetWorker!=null) {
                warehouseDTO.put("name", budgetWorker.getName());//商品名字
                warehouseDTO.put("image", address+budgetWorker.getImage());//图片地址
                warehouseDTO.put("type", type);
                warehouseDTO.put("repairCount", budgetWorker.getRepairCount());//补人工数
                warehouseDTO.put("shopCount", budgetWorker.getShopCount());//购买数
                warehouseDTO.put("workBack", budgetWorker.getBackCount());//退人工数
                warehouseDTO.put("tolPrice", (budgetWorker.getShopCount() + budgetWorker.getRepairCount()) * budgetWorker.getPrice());//实际花费
                List<WarehouseGoodsDTO> goodsDTOS=mendMaterielMapper.getWarehouseWorker(gid,houseId);
                warehouseDTO.put("list",goodsDTOS);
                return ServerResponse.createBySuccess("查询成功", warehouseDTO);
            }

            MendWorker mendWorker=mendMaterielMapper.getMendWorker(gid,houseId);
            if(mendWorker!=null) {
                warehouseDTO.put("name", mendWorker.getWorkerGoodsName());//商品名字
                warehouseDTO.put("image", address+mendWorker.getImage());//图片地址
                warehouseDTO.put("type", type);
                warehouseDTO.put("repairCount", mendWorker.getShopCount());//补人工数
                warehouseDTO.put("shopCount", 0);//购买数
                warehouseDTO.put("workBack", 0);//退人工数
                warehouseDTO.put("tolPrice", mendWorker.getShopCount()  * mendWorker.getPrice());//实际花费
                List<WarehouseGoodsDTO> goodsDTOS=mendMaterielMapper.getWarehouseWorker(gid,houseId);
                warehouseDTO.put("list",goodsDTOS);
                return ServerResponse.createBySuccess("查询成功", warehouseDTO);
            }
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }

}
