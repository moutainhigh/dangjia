package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.DjBasicsActuarialConfigurationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.DjBasicsActuarialConfigurationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/10/15
 * Time: 16:44
 */
@RestController
public class DjBasicsActuarialConfigurationController implements DjBasicsActuarialConfigurationAPI {
    private static Logger logger = LoggerFactory.getLogger(DjBasicsActuarialConfigurationController.class);
    @Autowired
    private DjBasicsActuarialConfigurationServices djBasicsActuarialConfigurationServices;

    @Override
    @ApiMethod
    public ServerResponse queryActuarialTemplateConfig(HttpServletRequest request) {
        return djBasicsActuarialConfigurationServices.queryActuarialTemplateConfig();
    }

    @Override
    @ApiMethod
    public ServerResponse queryActuarialProductByConfigId(HttpServletRequest request, String actuarialTemplateId) {
        return djBasicsActuarialConfigurationServices.queryActuarialProductByConfigId(actuarialTemplateId);
    }

    /**
     * 批量添加修改设计阶段的商品
     */
    @Override
    @ApiMethod
    public ServerResponse  editActuarialProduct(HttpServletRequest request,String actuarialProductStr,String userId){
        try{
            return djBasicsActuarialConfigurationServices.editActuarialProduct(actuarialProductStr,userId);
        }catch (Exception e){
            logger.error("批量编辑异常：",e);
            return ServerResponse.createByErrorMessage("保存失败！");
        }

    }

    /**
     * 删除对应的设计精算商品
     * @param request
     * @param id 设计精算商品表ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteActuarialProduct(HttpServletRequest request,String id){
        return djBasicsActuarialConfigurationServices.deleteActuarialProduct(id);
    }

    /**
     * 查询设计精算的货品列表
     * @param request
     * @param configType （设计类型1设计，2精算）
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuarialGoodsList(HttpServletRequest request,String configType){
        return djBasicsActuarialConfigurationServices.getActuarialGoodsList(configType);
    }
}
