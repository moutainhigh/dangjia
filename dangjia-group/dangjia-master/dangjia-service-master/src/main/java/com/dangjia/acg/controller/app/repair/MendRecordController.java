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
    public ServerResponse mendOrderDetail(String mendOrderId){
        return mendRecordService.mendOrderDetail(mendOrderId);
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
     * 补退记录
     *     //0:补材料;1:补人工;2:退材料;3:退人工
     */
    @Override
    @ApiMethod
    public ServerResponse mendList(String userToken, String houseId){
        return mendRecordService.mendList(userToken,houseId);
    }
}
