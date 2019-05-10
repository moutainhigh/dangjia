package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.MendRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 14:01
 * 补退记录
 */
@RestController
public class MendRecordController implements MendRecordAPI {
    @Autowired
    private MendRecordService mendRecordService;

    /**
     * 补退明细
     */
    @Override
    @ApiMethod
    public ServerResponse mendOrderDetail(String userToken, String mendOrderId, Integer type){
        return mendRecordService.mendOrderDetail( userToken,mendOrderId,type);
    }
    /**
     * 供应商退明细
     */
    @Override
    @ApiMethod
    public ServerResponse mendDeliverDetail(String userToken, String mendDeliverId){
        return mendRecordService.mendDeliverDetail( userToken,mendDeliverId);
    }
    /**
     *  记录列表
     */
    @Override
    @ApiMethod
    public ServerResponse recordList(String houseId,Integer type){
        return mendRecordService.recordList(houseId,type);
    }

    /**
     * 要补退记录
     */
    @Override
    @ApiMethod
    public ServerResponse mendList(String userToken,String houseId, int roleType){
        return mendRecordService.mendList(userToken,houseId,roleType);
    }

    @Override
    @ApiMethod
    public ServerResponse backOrder(String mendOrderId,Integer type) {
        return mendRecordService.backOrder(mendOrderId,type);
    }
}
