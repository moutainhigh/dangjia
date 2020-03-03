package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.WaterfallFlowConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.WaterfallFlowConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * @类 名： ActuarialTemplateController
 * @功能描述：
 * @作者信息： fzh
 */
@RestController
public class WaterfallFlowConfigController implements WaterfallFlowConfigAPI {
    private static Logger logger = LoggerFactory.getLogger(WaterfallFlowConfigController.class);
    /**
     * service
     */
    @Autowired
    private WaterfallFlowConfigService waterfallFlowConfigService;

    @Override
    @ApiMethod
    public ServerResponse queryWaterfallFlowConfig(String cityId){
        return waterfallFlowConfigService.queryWaterfallFlowConfig(cityId);
    }

    //新增或修改瀑布流
    @Override
    @ApiMethod
    public ServerResponse editWaterfallFlowConfig(String waterfallConfigId,String userId,String cityId,
                                           String name,Integer sort,String sourceInfoList){
        try{
            return waterfallFlowConfigService.editWaterfallFlowConfig(waterfallConfigId,userId,cityId,name,sort,sourceInfoList);
        }catch (Exception e){
            logger.error("保存失败",e);
            return ServerResponse.createByErrorMessage("保存失败");
        }

    }

    //修改精算模板
    @Override
    @ApiMethod
    public ServerResponse queryWaterfallFlowConfigInfo(String waterfallConfigId){
        return waterfallFlowConfigService.queryWaterfallFlowConfigInfo(waterfallConfigId);
    }

    //删除精算模板
    @Override
    @ApiMethod
    public ServerResponse deleteWaterfallFlowConfig(String waterfallConfigId){
        try{
            return waterfallFlowConfigService.deleteWaterfallFlowConfig(waterfallConfigId);
        }catch (Exception e){
            logger.error("删除失败",e);
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }



}

