package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.budget.BudgetDTO;
import com.dangjia.acg.dto.budget.BudgetItemDTO;
import com.dangjia.acg.dto.budget.GoodsItemDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IGoodsCategoryMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.util.JdbcContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/2/26 0026
 * Time: 10:51
 */
@Service
public class ActuaryOpeService {
    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IGoodsCategoryMapper goodsCategoryMapper;

    @Autowired
    private HouseAPI houseAPI;
    @Autowired
    private RedisClient redisClient;
    /**
     * 根据分类list查询商品
     * 自定义查看
     */
    public ServerResponse getByCategoryId(String idArr, String houseId, Integer type) {
        if (CommonUtil.isEmpty(idArr)) {
            return ServerResponse.createByErrorMessage("请传入需要查看的ID");
        }
        if (CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorMessage("请传入房子ID");
        }
        if (CommonUtil.isEmpty(type)) {
            return ServerResponse.createByErrorMessage("请传入类型");
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        BudgetDTO budgetDTO = new BudgetDTO();
        List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
        if (type == 1) {
            budgetDTO.setWorkerPrice(0.0);
            budgetDTO.setCaiPrice(budgetMaterialMapper.getHouseCaiPrice(houseId));
            String[] workerTypeIdArr = idArr.split(",");
            for (String aWorkerTypeIdArr : workerTypeIdArr) {
                BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                Double rowPrice = budgetWorkerMapper.getTypeAllPrice(houseId, null, aWorkerTypeIdArr);
                budgetItemDTO.setRowPrice(rowPrice);
                budgetDTO.setWorkerPrice(budgetDTO.getWorkerPrice() + (rowPrice == null ? 0 : rowPrice));
                List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getTypeAllList(houseId, null, aWorkerTypeIdArr);
                List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                for (BudgetWorker budgetWorker : budgetWorkerList) {
                    GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                    goodsItemDTO.setGoodsImage(address + budgetWorker.getImage());
                    goodsItemDTO.setGoodsName(budgetWorker.getName());
                    goodsItemDTO.setConvertCount(budgetWorker.getShopCount());
                    goodsItemDTO.setPrice(budgetWorker.getPrice());
                    goodsItemDTO.setUnitName(budgetWorker.getUnitName());
                    goodsItemDTO.setId(budgetWorker.getWorkerGoodsId());//人工商品id
                    goodsItemDTOList.add(goodsItemDTO);
                }
                budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
                budgetItemDTOList.add(budgetItemDTO);
            }
            budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
        } else {
            JSONArray jsonArray=queryWorkerType();
            budgetDTO.setWorkerPrice(budgetWorkerMapper.getHouseWorkerPrice(houseId, null));
            budgetDTO.setCaiPrice(0.0);
            String[] categoryIdArr = idArr.split(",");
            for (String aCategoryIdArr : categoryIdArr) {
                BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(aCategoryIdArr);
                if (goodsCategory == null) {
                    continue;
                }
                budgetItemDTO.setRowImage(address + goodsCategory.getImage());
                budgetItemDTO.setRowName(goodsCategory.getName());
                Double rowPrice = budgetMaterialMapper.getCategoryAllPrice(houseId, aCategoryIdArr);
                budgetItemDTO.setRowPrice(rowPrice);
                budgetDTO.setCaiPrice(budgetDTO.getCaiPrice() + (rowPrice == null ? 0 : rowPrice));
                List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.getCategoryAllList(houseId, aCategoryIdArr);
                List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                    GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                    WorkerType workerType = getWorkerTypeId(jsonArray,budgetMaterial.getWorkerTypeId());
                    if (workerType == null) {
                        continue;
                    }
                    goodsItemDTO.setWorkerTypeName(workerType.getName());
                    goodsItemDTO.setGoodsImage(address + budgetMaterial.getImage());
                    if (budgetMaterial.getSteta() == 2) {//自购
                        goodsItemDTO.setGoodsName(budgetMaterial.getGoodsName());
                    } else {
                        goodsItemDTO.setGoodsName(budgetMaterial.getProductName());
                        goodsItemDTO.setId(budgetMaterial.getProductId());//货号id
                    }
                    goodsItemDTO.setConvertCount(budgetMaterial.getConvertCount());
                    goodsItemDTO.setPrice(budgetMaterial.getPrice());
                    goodsItemDTO.setUnitName(budgetMaterial.getUnitName());
                    goodsItemDTOList.add(goodsItemDTO);
                }
                budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
                budgetItemDTOList.add(budgetItemDTO);
            }
            budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
        }
        return ServerResponse.createBySuccess("查询成功", budgetDTO);
    }

    /**
     * 所有分类
     */
    public ServerResponse categoryIdList(String houseId, Integer type) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            if (type == 1) {
                JSONArray jsonArray=queryWorkerType();
                List<String> workerTypeIdList = budgetWorkerMapper.workerTypeList(houseId);
                for (String workerTypeId : workerTypeIdList) {
                    WorkerType workerType = getWorkerTypeId(jsonArray,workerTypeId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", workerType.getId());
                    map.put("name", workerType.getName());
                    mapList.add(map);
                }
            } else {
                List<String> categoryIdList = budgetMaterialMapper.categoryIdList(houseId);
                for (String categoryId : categoryIdList) {
                    GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", goodsCategory.getId());
                    map.put("name", goodsCategory.getName());
                    mapList.add(map);
                }
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查看房子已购买的人工详细列表
     */
    public List<BudgetItemDTO> getHouseWorkerInfo(String houseId, String deleteState, String address) {
        List<String> workerTypeIdList = budgetWorkerMapper.workerTypeList(houseId);
        JSONArray jsonArray=queryWorkerType();
        List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
        List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getTypeAllList(houseId, deleteState, null);
        for (String workerTypeId : workerTypeIdList) {
            BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
            WorkerType workerType = getWorkerTypeId(jsonArray,workerTypeId);
            budgetItemDTO.setRowImage(address + workerType.getImage());
            budgetItemDTO.setRowName(workerType.getName());
            Double rowPrice = budgetWorkerMapper.getTypeAllPrice(houseId, deleteState, workerTypeId);
            if (rowPrice <= 0) {
                continue;
            }
            budgetItemDTO.setRowPrice(rowPrice);
            List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
            for (BudgetWorker budgetWorker : budgetWorkerList) {
                if (!workerTypeId.equals(budgetWorker.getWorkerTypeId())) {
                    continue;
                }
                GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                goodsItemDTO.setGoodsImage(address + budgetWorker.getImage());
                goodsItemDTO.setGoodsName(budgetWorker.getName());
                goodsItemDTO.setConvertCount(budgetWorker.getShopCount());
                goodsItemDTO.setPrice(budgetWorker.getPrice());
                goodsItemDTO.setUnitName(budgetWorker.getUnitName());
                goodsItemDTO.setId(budgetWorker.getWorkerGoodsId());//人工商品id
                goodsItemDTO.setShopCount(budgetWorker.getShopCount());
                goodsItemDTO.setBackCount(budgetWorker.getBackCount());
                goodsItemDTO.setRepairCount(budgetWorker.getRepairCount());
                goodsItemDTO.setSurCount(budgetWorker.getShopCount() - budgetWorker.getBackCount() + budgetWorker.getRepairCount());
                goodsItemDTO.setTolPrice(goodsItemDTO.getSurCount()*goodsItemDTO.getPrice());
                goodsItemDTOList.add(goodsItemDTO);
            }
            budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
            budgetItemDTOList.add(budgetItemDTO);
        }
        return budgetItemDTOList;
    }

    public Double getHouseWorkerPrice(String houseId, String deleteState) {
        return budgetWorkerMapper.getHouseWorkerPrice(houseId, deleteState);
    }

    private JSONArray queryWorkerType(){
        ServerResponse serverResponse = workerTypeAPI.getWorkerTypeList(-1);
        JSONArray jsonArray = null;
        if (serverResponse.isSuccess()) {
            jsonArray = (JSONArray) serverResponse.getResultObj();
        }
        return jsonArray;
    }
    private WorkerType getWorkerTypeId(JSONArray jsonArray ,String workerTypeId){
        WorkerType workerType=null;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject job = jsonArray.getJSONObject(i);
            WorkerType jobT = job.toJavaObject(WorkerType.class);
            if(jobT.getId().equals(workerTypeId)){
                workerType=jobT;
                break;
            }
        }
        return workerType;
    }
    /**
     * 精算详情
     * type: 1人工 2材料包工包料
     */
    public ServerResponse actuary(String houseId, Integer type) {
        try {
            //切换数据源
            House house = houseAPI.getHouseById(houseId);
            JdbcContextHolder.putDataSource(house.getCityId());

            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            BudgetDTO budgetDTO = new BudgetDTO();
            budgetDTO.setCustomEdit(house.getCustomEdit());
            budgetDTO.setWorkerPrice(budgetWorkerMapper.getHouseWorkerPrice(houseId, null));
            budgetDTO.setCaiPrice(budgetMaterialMapper.getHouseCaiPrice(houseId));
            budgetDTO.setTotalPrice(new BigDecimal((budgetDTO.getWorkerPrice() + budgetDTO.getCaiPrice())));
            if (type == 1) {//人工
                List<BudgetItemDTO> budgetItemDTOList = getHouseWorkerInfo(houseId, null, address);
                budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
            } else if (type == 2) {//材料
                JSONArray jsonArray=queryWorkerType();
                List<String> categoryIdList = budgetMaterialMapper.categoryIdList(houseId);
                Map<String, BudgetItemDTO> maps = new HashMap<>();
                List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.getCategoryAllList(houseId, null);
                for (String categoryId : categoryIdList) {
                    //获取低级类别
                    GoodsCategory goodsCategoryNext = goodsCategoryMapper.selectByPrimaryKey(categoryId);
                    if (goodsCategoryNext == null) {
                        continue;
                    }
                    //获取顶级类别
                    GoodsCategory goodsCategoryParentTop = goodsCategoryMapper.selectByPrimaryKey(goodsCategoryNext.getParentTop());
                    GoodsCategory goodsCategory;
                    if (goodsCategoryParentTop == null) {
                        goodsCategory = goodsCategoryNext;
                    } else {
                        goodsCategory = goodsCategoryParentTop;
                    }
                    //重临时缓存maps中取出BudgetItemDTO
                    BudgetItemDTO budgetItemDTO = maps.get(goodsCategory.getId());
                    if (budgetItemDTO == null) {
                        //如果没有将BudgetItemDTO初始化
                        budgetItemDTO = new BudgetItemDTO();
                        budgetItemDTO.setRowImage(address + goodsCategory.getImage());
                        budgetItemDTO.setRowName(goodsCategory.getName());
                    }
                    //获取价格
                    Double rowPrice = budgetMaterialMapper.getCategoryAllPrice(houseId, categoryId);
                    Double rowPriceOld = budgetItemDTO.getRowPrice();
                    if (rowPriceOld == null) rowPriceOld = 0.0;
                    if (rowPrice == null) rowPrice = 0.0;
                    //将价格每次都相加
                    budgetItemDTO.setRowPrice(rowPriceOld + rowPrice);
                    for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                        if (!categoryId.equals(budgetMaterial.getCategoryId())) continue;
                        GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                        WorkerType workerType = getWorkerTypeId(jsonArray,budgetMaterial.getWorkerTypeId());
                        goodsItemDTO.setWorkerTypeName(workerType.getName());
                        goodsItemDTO.setGoodsImage(address + budgetMaterial.getImage());
                        if (budgetMaterial.getSteta() == 2) {//自购
                            goodsItemDTO.setGoodsName(budgetMaterial.getGoodsName());
                        } else {
                            goodsItemDTO.setGoodsName(budgetMaterial.getProductName());
                        }
                        goodsItemDTO.setProductType(budgetMaterial.getProductType());
                        goodsItemDTO.setConvertCount(budgetMaterial.getConvertCount());
                        goodsItemDTO.setPrice(budgetMaterial.getPrice());
                        goodsItemDTO.setUnitName(budgetMaterial.getUnitName());
                        goodsItemDTO.setId(budgetMaterial.getProductId());//货号id
                        budgetItemDTO.addGoodsItemDTO(goodsItemDTO);
                    }
                    maps.put(goodsCategory.getId(), budgetItemDTO);
                }
                List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
                for (Map.Entry<String, BudgetItemDTO> entry : maps.entrySet()) {
                    budgetItemDTOList.add(entry.getValue());
                }
                budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
            }
            return ServerResponse.createBySuccess("查询成功", budgetDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
