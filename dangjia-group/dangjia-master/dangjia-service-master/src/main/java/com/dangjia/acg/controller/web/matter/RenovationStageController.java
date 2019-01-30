package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationStageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.matter.RenovationStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class RenovationStageController implements WebRenovationStageAPI {

    @Autowired
    private RenovationStageService renovationStageService;

    /**
     *查询所有装修指南阶段配置
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRenovationStage(){
        return renovationStageService.queryRenovationStage();
    }

     /**
     *新增装修指南阶段配置
     * @param name
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRenovationStage(String name){
        return renovationStageService.addRenovationStage(name);
    }

    /**
     * 修改装修指南阶段配置
     * @param id
     * @param name
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateRenovationStage(String id,String name){
        return renovationStageService.updateRenovationStage(id,name);
    }

    /**
     * 删除装修指南阶段配置
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteRenovationStage(String id){
        return renovationStageService.deleteRenovationStage(id);
    }


}
