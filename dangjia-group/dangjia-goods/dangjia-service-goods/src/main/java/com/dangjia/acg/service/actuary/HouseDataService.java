package com.dangjia.acg.service.actuary;

import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.FlowActuaryDTO;
import com.dangjia.acg.dto.actuary.FlowDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/12 0012
 * Time: 19:01
 */
@Service
public class HouseDataService {
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private ConfigUtil configUtil;


    /**
     * 自购清单
     */
    public ServerResponse selfBuyingList(String houseId){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String,String>> flowList  = getForBudgetAPI.getFlowList(houseId);
            List<FlowDTO> returnList = new ArrayList<>();
            for (Map<String,String> flowMap : flowList){
                String name = flowMap.get("name");
                String workerTypeId = flowMap.get("workerTypeId");
                //自购
                Example example = new Example(BudgetMaterial.class);
                example.createCriteria().andEqualTo(BudgetMaterial.WORKER_TYPE_ID,workerTypeId).andEqualTo(BudgetMaterial.HOUSE_ID,houseId)
                        .andEqualTo(BudgetMaterial.STETA, 2).andNotEqualTo(BudgetMaterial.DELETE_STATE,1);
                List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);

                if (budgetMaterialList.size() > 0){
                    FlowDTO flowDTO = new FlowDTO();
                    flowDTO.setName(name);
                    List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<>();
                    for (BudgetMaterial budgetMaterial : budgetMaterialList){
                        FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                        flowActuaryDTO.setImage(address + "icon/zigou.png");
                        flowActuaryDTO.setBuy(budgetMaterial.getProductType());//0:材料；1：服务
                        flowActuaryDTO.setName(budgetMaterial.getGoodsName());
                        flowActuaryDTO.setShopCount(budgetMaterial.getShopCount());
                        flowActuaryDTO.setUnitName(budgetMaterial.getUnitName());//单位
                        flowActuaryDTOList.add(flowActuaryDTO);
                    }
                    flowDTO.setFlowActuaryDTOList(flowActuaryDTOList);
                    returnList.add(flowDTO);
                }
            }

            return ServerResponse.createBySuccess("查询成功", returnList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
