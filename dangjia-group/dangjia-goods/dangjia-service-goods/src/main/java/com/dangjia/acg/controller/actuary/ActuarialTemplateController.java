package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuarialTemplateAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuarialTemplateService;
import com.dangjia.acg.service.basics.TechnologyService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @类 名： ActuarialTemplateController
 * @功能描述：
 * @作者信息： lxl
 * @创建时间： 2018-9-20上午13:35:10
 */
@RestController
public class ActuarialTemplateController  implements ActuarialTemplateAPI {
    /**
     *service
     */
    @Autowired
    private ActuarialTemplateService actuarialTemplateService;
    @Autowired
    private TechnologyService technologyService;
    
    /**
     * 查询所有精算模板
     * @Title: queryActuarialTemplate
     * @Description: 查询功能，state_type传1表示查询所有启用的，0为所有停用的，2或者不传为查询所有
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryActuarialTemplate(HttpServletRequest request,PageDTO pageDTO ,String workerTypeId, String stateType,String name){
        try {
            return actuarialTemplateService.queryActuarialTemplate(pageDTO.getPageNum(),pageDTO.getPageSize(),workerTypeId,stateType,name);
        } catch (Exception e) {
            throw new BaseException(ServerCode.WRONG_PARAM, "查询精算模版列表失败");
        }
    }

    /**
     * 新增精算模板
     * @Title: insertActuarialTemplate
     * @Description: TODO
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse<String> insertActuarialTemplate(HttpServletRequest request,String userId, String name, String styleType, String applicableArea,
                                                          Integer stateType, String workerTypeName,Integer workerTypeId){
        try {
            return actuarialTemplateService.insertActuarialTemplate(userId,name,styleType,applicableArea,
                    stateType,workerTypeName,workerTypeId);
        }
        catch (Exception e) {
            return ServerResponse.createByErrorMessage("新增精算模版失败");
        }

    }
    /**
     * 修改精算模板
     * @Title: updateActuarialTemplate
     * @Description: 根据精算模版ID修改
     * @param: @param id
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse<String> updateActuarialTemplate(HttpServletRequest request,String id,String name,String styleType,String applicableArea,Integer stateType,String workingProcedure) {
        try {
            return actuarialTemplateService.updateActuarialTemplate(id, name, styleType, applicableArea, stateType, workingProcedure);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("修改精算模版失败");
        }
    }
    /**
     * 删除精算模板
     * @Title: deleteActuarialTemplate
     * @Description: 根据精算模版ID删除
     * @param: @param id
     * @param: @return
     * @return: JsonResult
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse<String> deleteActuarialTemplate(HttpServletRequest request,String id)
    {
        try {
            return actuarialTemplateService.deleteActuarialTemplate(id);
        }
        catch (Exception e) {
            return ServerResponse.createByErrorMessage("删除精算模版失败");
        }
    }

}

