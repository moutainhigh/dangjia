package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @类 名： ActuarialTemplateController
 * @功能描述：
 * @作者信息： lxl
 * @创建时间： 2018-9-20上午13:35:10
 */
@Api(description = "精算模板接口")
@FeignClient("dangjia-service-goods")
public interface ActuarialTemplateAPI {

    /**
     * 查询所有精算模板
     *
     * @throws
     * @Title: queryActuarialTemplate
     * @Description: 查询功能，state_type传1表示查询所有启用的，0为所有停用的，2或者不传为查询所有
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     */
    @PostMapping("/actuary/actuary/queryActuarialTemplate")
    @ApiOperation(value = "精算模板列表", notes = "精算模板列表")
    public ServerResponse<PageInfo> queryActuarialTemplate(@RequestParam("request") HttpServletRequest request,@RequestParam("pageDTO") PageDTO pageDTO,
                                                           @RequestParam("workerTypeId")String workerTypeId,@RequestParam("stateType") String stateType,
                                                           @RequestParam("name") String name);

    //新增精算模板
    @PostMapping("/actuary/actuary/insertActuarialTemplate")
    @ApiOperation(value = "新增精算模板风格", notes = "新增精算模板风格")
    public ServerResponse insertActuarialTemplate(@RequestParam("request") HttpServletRequest request,@RequestParam("userId")String userId, @RequestParam("name") String name,@RequestParam("styleType") String styleType,
                                                  @RequestParam("applicableArea") String applicableArea,
                                                  @RequestParam("stateType")Integer stateType,@RequestParam("workerTypeName") String workerTypeName,
                                                  @RequestParam("workerTypeId")Integer workerTypeId);

    //修改精算模板
    @PostMapping("/actuary/actuary/updateActuarialTemplate")
    @ApiOperation(value = "修改精算模板风格", notes = "修改精算模板风格")
    public ServerResponse updateActuarialTemplate(@RequestParam("request") HttpServletRequest request,String id,String name,String styleType,String applicableArea,Integer stateType,String workingProcedure);

    //删除精算模板
    @PostMapping("/actuary/actuary/deleteActuarialTemplate")
    @ApiOperation(value = "删除精算模板", notes = "删除精算模板")
    public ServerResponse deleteActuarialTemplate(@RequestParam("request") HttpServletRequest request,String id);



}