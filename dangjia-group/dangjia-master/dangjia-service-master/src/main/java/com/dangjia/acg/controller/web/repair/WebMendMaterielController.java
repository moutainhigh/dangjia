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
     * 房子id查询业主退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse landlordState(String houseId,Integer pageNum, Integer pageSize,String beginDate, String endDate,String likeAddress){
        return mendMaterielService.landlordState(houseId,pageNum,pageSize,beginDate,endDate,likeAddress);
    }

    /**
     * 房子id查询退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(String houseId,Integer pageNum, Integer pageSize,String beginDate, String endDate,String likeAddress){
        return mendMaterielService.materialBackState(houseId,pageNum,pageSize,beginDate,endDate,likeAddress);
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
     */
    @Override
    @ApiMethod
    public ServerResponse materialOrderState(String houseId,Integer pageNum, Integer pageSize,String beginDate, String endDate,String likeAddress){
        return mendMaterielService.materialOrderState(houseId,pageNum,pageSize,beginDate,endDate,likeAddress);
    }
}
