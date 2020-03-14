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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    public ServerResponse queryActuarialTemplateConfig(HttpServletRequest request,String cityId,String serviceTypeId) {
        return djBasicsActuarialConfigurationServices.queryActuarialTemplateConfig(cityId,serviceTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryActuarialProductByConfigId(HttpServletRequest request, String actuarialTemplateId,String cityId) {
        return djBasicsActuarialConfigurationServices.queryActuarialProductByConfigId(actuarialTemplateId,cityId);
    }

    /**
     * 批量添加修改设计阶段的商品
     */
    @Override
    @ApiMethod
    public ServerResponse  editActuarialProduct(HttpServletRequest request,String actuarialProductStr,String actuarialTemplateId,String workTypeId,String userId,String cityId){
        try{
            return djBasicsActuarialConfigurationServices.editActuarialProduct(actuarialProductStr,actuarialTemplateId, workTypeId,userId,cityId);
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
    public ServerResponse getActuarialGoodsListByCategoryId(HttpServletRequest request,String categoryId,String cityId){
        return djBasicsActuarialConfigurationServices.getActuarialGoodsListByCategoryId(categoryId,cityId);
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
                                                 String configId, String configName,String configType,String cityId,String serviceTypeId){
        try{
            String userId = request.getParameter(Constants.USERID);
            return djBasicsActuarialConfigurationServices.editSimulateionTemplateConfig(userId,configId,configName,configType,configDetailArr,cityId, serviceTypeId);
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
    public ServerResponse querySimulateionTemplateConfig(HttpServletRequest request,String cityId,String serviceTypeId){
        return djBasicsActuarialConfigurationServices.querySimulateionTemplateConfig(cityId,serviceTypeId);
    }

    /**
     * 查询标题 详情信息
     * @param request
     * @param simulationTemplateId 标题 ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateionTemplateConfigById(HttpServletRequest request,String simulationTemplateId,String cityId){
        return djBasicsActuarialConfigurationServices.querySimulateionTemplateConfigById(simulationTemplateId,cityId);
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

    /**
     * excel模拟精算数据导入
     * @param request
     * @param name excel名称
     * @param fileName 上传的文件名
     * @param address 文件地址
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse importSimulateExcelBudgets(HttpServletRequest request,String name,String fileName,String address,String cityId,String serviceTypeId){

        try{
            return djBasicsActuarialConfigurationServices.importSimulateExcelBudgets(name,fileName,address,request.getParameter(Constants.USERID),cityId, serviceTypeId);
        } catch (Exception e) {
        logger.error("读取excel失败",e);
        return ServerResponse.createByErrorMessage("保存excel失败");
        }
    }

    /**
     * 查询excel列表
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateExcelList(HttpServletRequest request,String cityId,String serviceTypeId){
        return djBasicsActuarialConfigurationServices.querySimulateExcelList(cityId,serviceTypeId);
    }

    /**querySimulateAssemblyList
     * 根据excel表ID删除对应的excel数据
     * @param request
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteSimulateExcelById(HttpServletRequest request,String id){
        return djBasicsActuarialConfigurationServices.deleteSimulateExcelById(id);
    }

    /**
     * 组合精算，查询所有符合条件的组合列表
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateAssemblyList(HttpServletRequest request,String cityId,String serviceTypeId){
        return djBasicsActuarialConfigurationServices.querySimulateAssemblyList(cityId,serviceTypeId);
    }

    /**
     * 模拟花费，保存组合精算信息
     * @param request
     * @param assemblyInfoAttr 精算组合列表信息
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveSimulateAssemblyInfo(HttpServletRequest request,String  assemblyInfoAttr,String cityId,String serviceTypeId){
        try{
            return djBasicsActuarialConfigurationServices.saveSimulateAssemblyInfo(assemblyInfoAttr,request.getParameter(Constants.USERID),cityId,serviceTypeId);
        }catch (Exception e){
            logger.error("保存组合精算信息失败",e);
            return ServerResponse.createByErrorMessage("保存组合精算信息失败");
        }

    }

    /**
     * 查询精算组合关系表
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySimulateAssemblyRelateionList(HttpServletRequest request,String cityId,String serviceTypeId){
        return djBasicsActuarialConfigurationServices.querySimulateAssemblyRelateionList(cityId,serviceTypeId);
    }


}
