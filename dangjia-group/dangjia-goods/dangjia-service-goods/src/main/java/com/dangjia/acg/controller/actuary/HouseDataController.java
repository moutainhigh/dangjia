package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.HouseDataAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import com.dangjia.acg.service.actuary.HouseDataService;
import com.dangjia.acg.service.repair.FillMaterielService;
import com.dangjia.acg.service.repair.FillWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2018/12/12 0012
 * Time: 19:01
 */
@RestController
public class HouseDataController implements HouseDataAPI {

    @Autowired
    private HouseDataService houseDataService;
    @Autowired
    private FillMaterielService fillMaterielService;
    @Autowired
    private FillWorkerService fillWorkerService;
    @Autowired
    private ActuaryOperationService actuaryOperationService;

    /**
     * 工匠工艺查询商品库人工
     */
    @Override
    @ApiMethod
    public ServerResponse workerGoodsAll(String cityId, String workerTypeId,PageDTO pageDTO){
        return fillWorkerService.repairBudgetWorker(1,workerTypeId,null,null,pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    /**
     * 自购清单
     */
    @Override
    @ApiMethod
    public ServerResponse selfBuyingList(String houseId){
        return houseDataService.selfBuyingList(houseId);
    }

    /**
     * 人工详情
     */
    @Override
    @ApiMethod
    public ServerResponse workerGoodsDetail(String cityId,String workerGoodsId){
        return actuaryOperationService.getCommo(workerGoodsId, 1);//人工详情
    }
    /**
     * 查询工序人工
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetWorker(String cityId, String houseId, String workerTypeId, PageDTO pageDTO){
        return fillWorkerService.repairBudgetWorker(0,workerTypeId,houseId,"",pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    /**
     * 材料详情
     */
    @Override
    @ApiMethod
    public ServerResponse goodsDetail(String cityId,String productId){
        return actuaryOperationService.getCommo(productId, 2);//材料详情
    }
    /**
     * 查询工序材料
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetMaterial(String cityId,String houseId, String workerTypeId, PageDTO pageDTO){
        return fillMaterielService.repairBudgetMaterial(workerTypeId,"",houseId,"",pageDTO.getPageNum(),pageDTO.getPageSize());
    }


    /**
     * 导出精算汇总表
     */
    @Override
    @ApiMethod
    public ServerResponse exportActuaryTotal(HttpServletResponse response,String houseId){
        return houseDataService.exportActuaryTotal(response,houseId);
    }

}
