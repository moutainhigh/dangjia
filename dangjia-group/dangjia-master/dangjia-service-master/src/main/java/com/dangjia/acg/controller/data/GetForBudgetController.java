package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.service.core.HouseFlowService;
import com.dangjia.acg.service.design.HouseStyleTypeService;
import com.dangjia.acg.service.repair.MendMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:49
 */
@RestController
public class GetForBudgetController implements GetForBudgetAPI {

    @Autowired
    private HouseFlowService houseFlowService;
    @Autowired
    private HouseStyleTypeService houseStyleTypeService;
    @Autowired
    private MendMaterielService mendMaterielService;

    @Override
    public List<MendMateriel> askAndQuit(String workerTypeId, String houseId, String categoryId, String name) {
        return mendMaterielService.askAndQuit(workerTypeId,houseId,categoryId,name);
    }

    @Override
    @ApiMethod
    public HouseStyleType getStyleById(String style) {
        return houseStyleTypeService.getStyleById(style);
    }

    @Override
    @ApiMethod
    public List<Map<String,String>> getFlowList(String houseId){
        return houseFlowService.getFlowList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse actuarialForBudget(String houseId, String workerTypeId){
        return houseFlowService.makeOfBudget(houseId,workerTypeId);
    }
}
