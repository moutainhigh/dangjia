package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationManualAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.matter.RenovationManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class RenovationManualController implements WebRenovationManualAPI {

    @Autowired
    private RenovationManualService renovationManualService;

    /**
     *根据工序id查询所有装修指南
     * @param workerTypeId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRenovationManual(PageDTO pageDTO,String workerTypeId){
        return renovationManualService.queryRenovationManual(pageDTO.getPageNum(), pageDTO.getPageSize(),workerTypeId);
    }

     /**
     *新增装修指南
     * @param name
     * @param workerTypeId
     * @param orderNumber
     * @param types
     * @param url
     * @param urlName
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRenovationManual(String name, String workerTypeId, Integer orderNumber, String types,
                                              String url, String urlName){
        return renovationManualService.addRenovationManual(name,workerTypeId,orderNumber,types,url,urlName);
    }

    /**
     * 修改装修指南
     * @param id
     * @param name
     * @param orderNumber
     * @param types
     * @param state
     * @param url
     * @param urlName
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateRenovationManual(String id,String name,Integer orderNumber,String types,Integer state,
                                                 String url,String urlName){
        return renovationManualService.updateRenovationManual(id,name,orderNumber,types,state,url,urlName);
    }

    /**
     * 删除装修指南
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteRenovationManual(String id){
        return renovationManualService.deleteRenovationManual(id);
    }

    /**
     * 根据id查询装修指南对象
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getRenovationManualById(String id){
        return renovationManualService.getRenovationManualById(id);
    }

}
