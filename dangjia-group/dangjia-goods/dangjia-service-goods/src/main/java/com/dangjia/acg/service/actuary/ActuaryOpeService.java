package com.dangjia.acg.service.actuary;

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
import com.dangjia.acg.modle.config.ConfigAppHistory;
import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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


    /**
     * 根据分类list查询商品
     * 自定义查看
     */
    public ServerResponse getByCategoryId(String idArr, String houseId, Integer type) {
        try {
            BudgetDTO budgetDTO = new BudgetDTO();
            if (idArr == null) idArr = "";
            if (type == 1) {
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                budgetDTO.setWorkerPrice(0.0);
                budgetDTO.setCaiPrice(budgetMaterialMapper.getHouseCaiPrice(houseId));
                String[] workerTypeIdArr = idArr.split(",");
                List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
                for (String aWorkerTypeIdArr : workerTypeIdArr) {
                    BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                    WorkerType workerType = workerTypeAPI.queryWorkerType(aWorkerTypeIdArr);
                    budgetItemDTO.setRowImage(address + workerType.getImage());
                    budgetItemDTO.setRowName(workerType.getName());
                    Double rowPrice = budgetWorkerMapper.getTypeAllPrice(houseId, aWorkerTypeIdArr);
                    budgetItemDTO.setRowPrice(rowPrice);
                    budgetDTO.setWorkerPrice(budgetDTO.getWorkerPrice() + rowPrice);

                    List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getTypeAllList(houseId, aWorkerTypeIdArr);
                    List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                    for (BudgetWorker budgetWorker : budgetWorkerList) {
                        GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                        goodsItemDTO.setWorkerTypeName(workerType.getName());
                        goodsItemDTO.setGoodsImage(address + budgetWorker.getImage());
                        goodsItemDTO.setGoodsName(budgetWorker.getName());
                        goodsItemDTO.setConvertCount(budgetWorker.getShopCount().intValue());
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
                budgetDTO.setWorkerPrice(budgetWorkerMapper.getHouseWorkerPrice(houseId));
                budgetDTO.setCaiPrice(0.0);
                List<String> categoryIdList = Arrays.asList(idArr.split(","));
                budgetDTO.setBudgetItemDTOList(getBudgetItemDTOList(categoryIdList, houseId));
            }
            //budgetDTO.setTotalPrice(budgetDTO.getWorkerPrice() + budgetDTO.getCaiPrice());
            return ServerResponse.createBySuccess("查询成功", budgetDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 所有分类
     */
    public ServerResponse categoryIdList(String houseId, Integer type) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            if (type == 1) {
                List<String> workerTypeIdList = budgetWorkerMapper.workerTypeList(houseId);
                for (String workerTypeId : workerTypeIdList) {
                    WorkerType workerType = workerTypeAPI.queryWorkerType(workerTypeId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", workerType.getId());
                    map.put("name", workerType.getName());
                    mapList.add(map);
                }
            } else {
                List<String> categoryIdList = budgetMaterialMapper.categoryIdList(houseId);
                Map<String, Map<String, Object>> maps = new HashMap<>();
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
                    Map<String, Object> map = maps.get(goodsCategory.getId());
                    if (map == null) {
                        //如果没有将BudgetItemDTO初始化
                        map = new HashMap<>();
                        map.put("name", goodsCategory.getName());
                    }
                    String id = (String) map.get("id");
                    if (CommonUtil.isEmpty(id)) {
                        id = goodsCategoryNext.getId();
                    } else {
                        id = id + "," + goodsCategoryNext.getId();
                    }
                    map.put("id", id);
                    maps.put(goodsCategory.getId(), map);
                }
                for (Map.Entry<String, Map<String, Object>> entry : maps.entrySet()) {
                    mapList.add(entry.getValue());
                }
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 精算详情
     * type: 1人工 2材料服务
     */
    public ServerResponse actuary(String houseId, Integer type) {
        try {
            BudgetDTO budgetDTO = new BudgetDTO();
            budgetDTO.setWorkerPrice(budgetWorkerMapper.getHouseWorkerPrice(houseId));
            budgetDTO.setCaiPrice(budgetMaterialMapper.getHouseCaiPrice(houseId));
            budgetDTO.setTotalPrice(budgetDTO.getWorkerPrice() + budgetDTO.getCaiPrice());
            if (type == 1) {//人工
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                List<String> workerTypeIdList = budgetWorkerMapper.workerTypeList(houseId);
                List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
                for (String workerTypeId : workerTypeIdList) {
                    BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                    WorkerType workerType = workerTypeAPI.queryWorkerType(workerTypeId);
                    budgetItemDTO.setRowImage(address + workerType.getImage());
                    budgetItemDTO.setRowName(workerType.getName());
                    Double rowPrice = budgetWorkerMapper.getTypeAllPrice(houseId, workerTypeId);
                    budgetItemDTO.setRowPrice(rowPrice);

                    List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getTypeAllList(houseId, workerTypeId);
                    List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                    for (BudgetWorker budgetWorker : budgetWorkerList) {
                        GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                        goodsItemDTO.setGoodsImage(address + budgetWorker.getImage());
                        goodsItemDTO.setGoodsName(budgetWorker.getName());
                        goodsItemDTO.setConvertCount(budgetWorker.getShopCount().intValue());
                        goodsItemDTO.setPrice(budgetWorker.getPrice());
                        goodsItemDTO.setUnitName(budgetWorker.getUnitName());
                        goodsItemDTO.setId(budgetWorker.getWorkerGoodsId());//人工商品id
                        goodsItemDTOList.add(goodsItemDTO);
                    }
                    budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
                    budgetItemDTOList.add(budgetItemDTO);
                }
                budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
            } else if (type == 2) {//材料
                List<String> categoryIdList = budgetMaterialMapper.categoryIdList(houseId);
                budgetDTO.setBudgetItemDTOList(getBudgetItemDTOList(categoryIdList, houseId));
            }
            return ServerResponse.createBySuccess("查询成功", budgetDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List<BudgetItemDTO> getBudgetItemDTOList(List<String> categoryIdList, String houseId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Map<String, BudgetItemDTO> maps = new HashMap<>();
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
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.getCategoryAllList(houseId, categoryId);
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                WorkerType workerType = workerTypeAPI.queryWorkerType(budgetMaterial.getWorkerTypeId());
                goodsItemDTO.setWorkerTypeName(workerType.getName());
                goodsItemDTO.setGoodsImage(address + budgetMaterial.getImage());
                if (budgetMaterial.getSteta() == 2) {//自购
                    goodsItemDTO.setGoodsName(budgetMaterial.getGoodsName());
                } else {
                    goodsItemDTO.setGoodsName(budgetMaterial.getProductName());
                }
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
        return budgetItemDTOList;
    }
}
