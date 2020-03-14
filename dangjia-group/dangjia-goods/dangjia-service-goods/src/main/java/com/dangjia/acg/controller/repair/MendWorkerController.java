package com.dangjia.acg.controller.repair;

import com.dangjia.acg.api.repair.MendWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.FillWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:11
 */
@RestController
public class MendWorkerController implements MendWorkerAPI {

    @Autowired
    private FillWorkerService mendWorkerService;

    @Override
    @ApiMethod
    public ServerResponse repairBudgetWorker(HttpServletRequest request, Integer type, String workerTypeId, String houseId,
                                             PageDTO pageDTO,String cityId) {
        return mendWorkerService.repairBudgetWorker(type, workerTypeId, houseId, pageDTO,cityId,"3");
    }

    /**
     *  查询符合条件的人工商品大类
     * @param request
     * @param workerId
     * @param cityId
     * @return
     */
   /* @Override
    @ApiMethod
    public ServerResponse getWorkerProductCategoryList(HttpServletRequest request, String workerId,String cityId){
        return mendWorkerService.getWorkerProductCategoryList(workerId,cityId);
    }*/


    /**
     * 查询符合条件的人工商品
     * @param request
     * @param userToken
     * @param searchKey
     * @param pageDTO
     * @param cityId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getWorkerProductList(HttpServletRequest request,String userToken,String houseId,String searchKey,PageDTO pageDTO,String cityId){
        return mendWorkerService.getWorkerProductList(userToken,houseId,searchKey,pageDTO,cityId);
    }

}
