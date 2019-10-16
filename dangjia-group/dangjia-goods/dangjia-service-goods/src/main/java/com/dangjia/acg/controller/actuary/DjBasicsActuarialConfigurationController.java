package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.DjBasicsActuarialConfigurationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
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
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuarialGoodsList(HttpServletRequest request){
        return djBasicsActuarialConfigurationServices.getActuarialGoodsList();
    }

    /**
     * 查询货品下的商品列表
     * @param request
     * @param goodsId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuarialProductListByGoodsId(HttpServletRequest request,
                                                    String goodsId){
        return djBasicsActuarialConfigurationServices.getActuarialProductListByGoodsId(goodsId);
    }
    /**
     *
     * @param request
     * @param configDetailArr(问题下的选项值列表）
     * @param configId  模拟配置问题 ID
     * @param configName 模拟配置问题 名称
     * @param configType 模拟配置问题模板类型（A图片和文字，B仅图片，C仅文字）
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editSimulateionTemplateConfig(HttpServletRequest request,String configDetailArr,
                                                 String configId, String configName,String configType){
        try{
            String userId = request.getParameter(Constants.USERID);
            return djBasicsActuarialConfigurationServices.editSimulateionTemplateConfig(userId,configId,configName,configType,configDetailArr);
        }catch (Exception e){
            logger.error("editSimulateionTemplateConfig批量编辑异常：",e);
            return ServerResponse.createByErrorMessage("保存失败！");
        }
    }

    /**
     * 查询标题 列表信息
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateionTemplateConfig(HttpServletRequest request){
        return djBasicsActuarialConfigurationServices.querySimulateionTemplateConfig();
    }

    /**
     * 查询标题 详情信息
     * @param request
     * @param simulationTemplateId 标题 ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateionTemplateConfigById(HttpServletRequest request,String simulationTemplateId){
        return djBasicsActuarialConfigurationServices.querySimulateionTemplateConfigById(simulationTemplateId);
    }

    /**
     * 查询标题详情具体信息（根据详情ID）
     * @param request
     * @param simulationDetailId 详情ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateionDetailInfoById(HttpServletRequest request,String simulationDetailId){
        return djBasicsActuarialConfigurationServices.querySimulateionDetailInfoById(simulationDetailId);
    }

    /**
     * 根据标题 ID删除对应的标题信息
     * @param request
     * @param simulationTemplateId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteSimulateDetailInfoById(HttpServletRequest  request,String simulationTemplateId){
        return djBasicsActuarialConfigurationServices.deleteSimulateDetailInfoById(simulationTemplateId);
    }
}
