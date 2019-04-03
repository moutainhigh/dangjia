package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuaryOpeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.budget.BudgetItemDTO;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.service.actuary.ActuaryOpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * author: Ronalcheng
 * Date: 2019/2/26 0026
 * Time: 10:49
 */
@RestController
public class ActuaryOpeController implements ActuaryOpeAPI {
    @Autowired
    private ActuaryOpeService actuaryOpeService;

    @Override
    @ApiMethod
    public ServerResponse getByCategoryId(String idArr, String houseId,String cityId,Integer type) {
        return actuaryOpeService.getByCategoryId(idArr,houseId,type);
    }

    @Override
    @ApiMethod
    public ServerResponse categoryIdList(String houseId,String cityId,Integer type) {
        return actuaryOpeService.categoryIdList(houseId,type);
    }

    @Override
    @ApiMethod
    public ServerResponse actuary(String houseId, String cityId,Integer type) {
        return actuaryOpeService.actuary(houseId, type);
    }

    /**
     * 查看房子已购买的人工详细列表
     * @param houseId
     * @param address
     * @return
     */
    @Override
    public List<BudgetItemDTO> getHouseWorkerInfo( HttpServletRequest request, String houseId, String address){
        String deleteState="";
        if(request.getAttribute(BudgetWorker.DATA_STATUS)!=null){
            deleteState=(String) request.getAttribute(BudgetWorker.DATA_STATUS);
        }
        return actuaryOpeService.getHouseWorkerInfo(houseId,deleteState, address);
    }
    @Override
    public Double getHouseWorkerPrice(HttpServletRequest request,String houseId){
        String deleteState="";
        if(request.getAttribute(BudgetWorker.DATA_STATUS)!=null){
            deleteState=(String) request.getAttribute(BudgetWorker.DATA_STATUS);
        }
        return actuaryOpeService.getHouseWorkerPrice(houseId,deleteState);
    }
}
