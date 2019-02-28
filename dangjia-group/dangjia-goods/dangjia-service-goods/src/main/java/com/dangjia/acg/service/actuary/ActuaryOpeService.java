package com.dangjia.acg.service.actuary;

import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
     * 精算详情
     * type: 1人工 2材料服务
     */
    public ServerResponse actuary(String houseId, Integer type) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            BudgetDTO budgetDTO = new BudgetDTO();
            budgetDTO.setWorkerPrice(budgetWorkerMapper.getHouseWorkerPrice(houseId));
            budgetDTO.setCaiPrice(budgetMaterialMapper.getHouseCaiPrice(houseId));
            budgetDTO.setTotalPrice(budgetDTO.getWorkerPrice() + budgetDTO.getCaiPrice());
            if(type == 1){//人工
                List<String> workerTypeIdList = budgetWorkerMapper.workerTypeList(houseId);
                List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
                for (String workerTypeId : workerTypeIdList){
                    BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                    WorkerType workerType = workerTypeAPI.queryWorkerType(workerTypeId);
                    budgetItemDTO.setRowImage(address + workerType.getImage());
                    budgetItemDTO.setRowName(workerType.getName());
                    Double rowPrice = budgetWorkerMapper.getTypeAllPrice(houseId, workerTypeId);
                    budgetItemDTO.setRowPrice(rowPrice);

                    List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getTypeAllList(houseId, workerTypeId);
                    List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                    for (BudgetWorker budgetWorker : budgetWorkerList){
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
            }else if (type == 2){
                List<String> categoryIdList = budgetMaterialMapper.categoryIdList(houseId);
                List<BudgetItemDTO> budgetItemDTOList = new ArrayList<>();
                for (String categoryId : categoryIdList){
                    BudgetItemDTO budgetItemDTO = new BudgetItemDTO();
                    GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
                    budgetItemDTO.setRowImage(address + goodsCategory.getImage());
                    budgetItemDTO.setRowName(goodsCategory.getName());
                    Double rowPrice = budgetMaterialMapper.getCategoryAllPrice(houseId, categoryId);
                    budgetItemDTO.setRowPrice(rowPrice);

                    List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.getCategoryAllList(houseId, categoryId);
                    List<GoodsItemDTO> goodsItemDTOList = new ArrayList<>();
                    for (BudgetMaterial budgetMaterial : budgetMaterialList){
                        GoodsItemDTO goodsItemDTO = new GoodsItemDTO();
                        goodsItemDTO.setGoodsImage(address + budgetMaterial.getImage());
                        goodsItemDTO.setGoodsName(budgetMaterial.getProductName());
                        goodsItemDTO.setConvertCount(budgetMaterial.getConvertCount());
                        goodsItemDTO.setPrice(budgetMaterial.getPrice());
                        goodsItemDTO.setUnitName(budgetMaterial.getUnitName());
                        goodsItemDTO.setId(budgetMaterial.getProductId());//货号id
                        goodsItemDTOList.add(goodsItemDTO);
                    }
                    budgetItemDTO.setGoodsItemDTOList(goodsItemDTOList);
                    budgetItemDTOList.add(budgetItemDTO);
                }
                budgetDTO.setBudgetItemDTOList(budgetItemDTOList);
            }

            return ServerResponse.createBySuccess("查询成功",budgetDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
