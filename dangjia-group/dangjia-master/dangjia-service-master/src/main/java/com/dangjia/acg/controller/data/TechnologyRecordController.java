package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.service.matter.TechnologyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/2 0002
 * Time: 17:25
 */
@RestController
public class TechnologyRecordController implements TechnologyRecordAPI {

    @Autowired
    private TechnologyRecordService technologyRecordService;


    /**
     * 获取上传图片列表
     */
    @Override
    @ApiMethod
    public ServerResponse uploadingImageList(String userToken,String nodeArr){
        return technologyRecordService.uploadingImageList(nodeArr);
    }
    /**
     * 根据houseFlowId查询验收节点
     * 供管家选择验收
     */
    @Override
    @ApiMethod
    public ServerResponse technologyRecordList(String userToken,String houseFlowId){
        return technologyRecordService.technologyRecordList(houseFlowId);
    }

    /**
     * 已进场未完工
     */
    @Override
    @ApiMethod
    public List<HouseFlow> unfinishedFlow(String houseId){
        return technologyRecordService.unfinishedFlow(houseId);
    }

    /**
     * 所有购买材料
     */
    @Override
    @ApiMethod
    public List<Warehouse> warehouseList(String houseId){
        return technologyRecordService.warehouseList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getByProductId(String productId,String houseId){
        return technologyRecordService.getByProductId(productId,houseId);
    }


}
