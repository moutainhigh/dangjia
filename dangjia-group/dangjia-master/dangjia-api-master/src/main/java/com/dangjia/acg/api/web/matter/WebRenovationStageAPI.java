package com.dangjia.acg.api.web.matter;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 后台装修指南阶段配置接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台装修指南阶段配置接口", description = "后台装修指南阶段配置接口")
public interface WebRenovationStageAPI {
    /**
     * showdoc
     *
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 获取所有装修指南阶段配置
     * @description 获取所有装修指南阶段配置
     * @method POST
     * @url master/web/renovationStage/queryRenovationStage
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param name string 阶段名称
     * @return_param image string 图标
     * @return_param imageUrl string 图标地址
     * @return_param workerTypeId string 工序ID
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 3:21 PM
     */
    @PostMapping("web/renovationStage/queryRenovationStage")
    @ApiOperation(value = "获取所有装修指南阶段配置", notes = "获取所有装修指南阶段配置")
    ServerResponse queryRenovationStage(@RequestParam("request") HttpServletRequest request);

    /**
     * showdoc
     *
     * @param name         必选 string 阶段名称
     * @param image        必选 string 图标
     * @param workerTypeId 可选 string 工序ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 新增装修指南阶段配置
     * @description 新增装修指南阶段配置
     * @method POST
     * @url master/web/renovationStage/addRenovationStage
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 3:25 PM
     */
    @PostMapping("web/renovationStage/addRenovationStage")
    @ApiOperation(value = "新增装修指南阶段配置", notes = "新增装修指南阶段配置")
    ServerResponse addRenovationStage(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("name") String name,
                                      @RequestParam("image") String image,
                                      @RequestParam("workerTypeId") String workerTypeId);

    /**
     * showdoc
     *
     * @param id           必选 string id
     * @param name         可选 string 阶段名称
     * @param image        可选 string 图标
     * @param workerTypeId 可选 string 工序ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 修改装修指南阶段配置
     * @description 修改装修指南阶段配置
     * @method POST
     * @url master/web/renovationStage/updateRenovationStage
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 3:26 PM
     */
    @PostMapping("web/renovationStage/updateRenovationStage")
    @ApiOperation(value = "修改装修指南阶段配置", notes = "修改装修指南阶段配置")
    ServerResponse updateRenovationStage(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("id") String id,
                                         @RequestParam("name") String name,
                                         @RequestParam("image") String image,
                                         @RequestParam("workerTypeId") String workerTypeId);

    /**
     * showdoc
     *
     * @param id 必选 string id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/装修攻略
     * @title 删除装修指南阶段配置
     * @description 删除装修指南阶段配置
     * @method POST
     * @url master/web/renovationStage/deleteRenovationStage
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/6/17 3:28 PM
     */
    @PostMapping("web/renovationStage/deleteRenovationStage")
    @ApiOperation(value = "删除装修指南阶段配置", notes = "删除装修指南阶段配置")
    ServerResponse deleteRenovationStage(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("id") String id);

}
