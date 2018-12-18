package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.web.repair.WebMendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:40
 */
@RestController
public class WebMendMaterielController implements WebMendMaterielAPI {
    @Autowired
    private MendMaterielService mendMaterielService;

    /**
     * 通过 不通过
     */
    @Override
    @ApiMethod
    public ServerResponse checkMaterialBackState(String mendOrderId,int state){
        return mendMaterielService.checkMaterialBackState(mendOrderId, state);
    }

    /**
     * 房子id查询退货单列表
     * material_back_state
     * 0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(String houseId,Integer pageNum, Integer pageSize){
        return mendMaterielService.materialBackState(houseId,pageNum,pageSize);
    }

    /**
     * 通过 不通过
     */
    @Override
    @ApiMethod
    public ServerResponse checkMaterialOrderState(String mendOrderId, int state){
        return mendMaterielService.checkMaterialOrderState(mendOrderId, state);
    }

    /**
     * 根据mendOrderId查明细
     */
    @Override
    @ApiMethod
    public ServerResponse mendMaterialList(String mendOrderId){
        return mendMaterielService.mendMaterialList(mendOrderId);
    }

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    @Override
    @ApiMethod
    public ServerResponse materialOrderState(String houseId,Integer pageNum, Integer pageSize){
        return mendMaterielService.materialOrderState(houseId,pageNum,pageSize);
    }
}
