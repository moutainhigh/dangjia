package com.dangjia.acg.api.home;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @description 新版首页接口
 * @Author: Ruking 18075121944
 * @Date: 2019/6/12 3:18 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版首页接口", description = "新版首页接口")
public interface HomeAPI {

    /**
     * showdoc
     *
     * @param userId 必选 string 操作人ID
     * @param name   必选 string 模版名称
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 添加首页模版
     * @description 添加首页模版
     * @method POST
     * @url master/home/addHomeTemplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 2:17 PM
     */
    @PostMapping("home/addHomeTemplate")
    @ApiOperation(value = "添加首页模版", notes = "添加首页模版")
    ServerResponse addHomeTemplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("name") String name);

    /**
     * showdoc
     *
     * @param pageNum  必选 int 页码
     * @param pageSize 必选 int 记录数
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/首页模块
     * @title 获取首页模版列表
     * @description 获取首页模版列表
     * @method POST
     * @url master/home/getHomeTemplateList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param userId string 操作人id
     * @return_param userName string 操作人姓名
     * @return_param userMobile string 操作人电话
     * @return_param name string 模版名称
     * @return_param enable string 是否启用：0:禁用，1:启用
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 2:18 PM
     */
    @PostMapping("home/getHomeTemplateList")
    @ApiOperation(value = "获取首页模版列表", notes = "获取首页模版列表")
    ServerResponse getHomeTemplateList(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param userId     必选 string 操作人ID
     * @param name       必选 string 模版名称
     * @param templateId 必选 string 对应的模版ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 修改首页模版
     * @description 修改首页模版
     * @method POST
     * @url master/home/upDataHomeTemplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 2:21 PM
     */
    @PostMapping("home/upDataHomeTemplate")
    @ApiOperation(value = "修改首页模版", notes = "修改首页模版")
    ServerResponse upDataHomeTemplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("name") String name,
            @RequestParam("templateId") String templateId);

    /**
     * showdoc
     *
     * @param userId     必选 string 操作人ID
     * @param templateId 必选 string 对应的模版ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 设置首页模版启用
     * @description 设置首页模版启用
     * @method POST
     * @url master/home/setHomeTemplateEnable
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 2:23 PM
     */
    @PostMapping("home/setHomeTemplateEnable")
    @ApiOperation(value = "设置首页模版启用", notes = "设置首页模版启用")
    ServerResponse setHomeTemplateEnable(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("templateId") String templateId);

    /**
     * showdoc
     *
     * @param userId     必选 string 操作人ID
     * @param templateId 必选 string 对应的模版ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 删除首页模版
     * @description 删除首页模版
     * @method POST
     * @url master/home/delHomeTemplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 2:24 PM
     */
    @PostMapping("home/delHomeTemplate")
    @ApiOperation(value = "删除首页模版", notes = "删除首页模版")
    ServerResponse delHomeTemplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("templateId") String templateId);

    /**
     * showdoc
     *
     * @param templateId 选填 string 对应的模版ID为空的时候为获取当前启用的模版
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title App获取首页配置
     * @description App获取首页配置
     * @method POST
     * @url master/home/getAppHomeCollocation
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param name string 名称
     * @return_param image string 图片
     * @return_param imageAddress string 图片全地址
     * @return_param url string H5对应的组件名
     * @return_param userId string 操作人id
     * @return_param userName string 操作人姓名
     * @return_param userMobile string 操作人电话
     * @return_param sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 2:38 PM
     */
    @PostMapping("home/getAppHomeCollocation")
    @ApiOperation(value = "App获取首页配置", notes = "App获取首页配置")
    ServerResponse getAppHomeCollocation(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("templateId") String templateId);

    /**
     * showdoc
     *
     * @param templateId     必选 string 对应的模版ID
     * @param userId         必选 string 操作人ID
     * @param masterpieceIds 必选 string 配置模块ID，以“,“分割
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 添加App首页模版并且排序
     * @description 添加App首页模版并且排序
     * @method POST
     * @url master/home/setAppHomeCollocation
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 2:45 PM
     */
    @PostMapping("home/setAppHomeCollocation")
    @ApiOperation(value = "添加App首页模版并且排序", notes = "添加App首页模版并且排序")
    ServerResponse setAppHomeCollocation(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("templateId") String templateId,
            @RequestParam("userId") String userId,
            @RequestParam("masterpieceIds") String masterpieceIds);

    /**
     * showdoc
     *
     * @param templateId 必选 string 对应的模版ID
     * @param pageNum    必选 int 页码
     * @param pageSize   必选 int 记录数
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/首页模块
     * @title App获取首页配置历史记录
     * @description App获取首页配置历史记录
     * @method POST
     * @url master/home/getAppHomeCollocationHistory
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param userId string 操作人id
     * @return_param userName string 操作人姓名
     * @return_param userMobile string 操作人电话
     * @return_param masterpieceIds string 配置模块ID，以“,“分割
     * @return_param masterplateList List 实际模块排版
     * @return_param masterplateList_id string id
     * @return_param masterplateList_createDate string 创建时间
     * @return_param masterplateList_modifyDate string 修改时间
     * @return_param masterplateList_dataStatus int 数据状态 0=正常，1=删除
     * @return_param masterplateList_name string 名称
     * @return_param masterplateList_image string 图片
     * @return_param masterplateList_imageAddress string 图片全地址
     * @return_param masterplateList_url string H5对应的组件名
     * @return_param masterplateList_userId string 操作人id
     * @return_param masterplateList_userName string 操作人姓名
     * @return_param masterplateList_userMobile string 操作人电话
     * @return_param masterplateList_sort int 优先顺序
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 8
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 3:07 PM
     */
    @PostMapping("home/getAppHomeCollocationHistory")
    @ApiOperation(value = "App获取首页配置历史记录", notes = "App获取首页配置历史记录")
    ServerResponse getAppHomeCollocationHistory(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("templateId") String templateId,
            @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param pageNum  必选 int 页码
     * @param pageSize 必选 int 记录数
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/首页模块
     * @title 获取所有首页模块
     * @description 获取所有首页模块
     * @method POST
     * @url master/home/getHomeMasterplateList
     * @return_param id string id
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态 0=正常，1=删除
     * @return_param name string 名称
     * @return_param image string 图片
     * @return_param imageAddress string 图片全地址
     * @return_param url string H5对应的组件名
     * @return_param userId string 操作人id
     * @return_param userName string 操作人姓名
     * @return_param userMobile string 操作人电话
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 9
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 3:11 PM
     */
    @PostMapping("home/getHomeMasterplateList")
    @ApiOperation(value = "获取所有首页模块", notes = "获取所有首页模块")
    ServerResponse getHomeMasterplateList(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param name   必选 string 名称
     * @param image  必选 string 图片
     * @param url    必选 string H5对应的组件名
     * @param userId 必选 string 操作人id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 添加首页模块
     * @description 添加首页模块
     * @method POST
     * @url master/home/addHomeMasterplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 10
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 3:13 PM
     */
    @PostMapping("home/addHomeMasterplate")
    @ApiOperation(value = "添加首页模块", notes = "添加首页模块")
    ServerResponse addHomeMasterplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("image") String image,
            @RequestParam("url") String url,
            @RequestParam("userId") String userId);

    /**
     * showdoc
     *
     * @param id     必选 string 首页模块ID
     * @param userId 必选 string 操作人id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 删除首页模块
     * @description 删除首页模块
     * @method POST
     * @url master/home/delHomeMasterplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 11
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 3:16 PM
     */
    @PostMapping("home/delHomeMasterplate")
    @ApiOperation(value = "删除首页模块", notes = "删除首页模块")
    ServerResponse delHomeMasterplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("id") String id,
            @RequestParam("userId") String userId);

    /**
     * showdoc
     *
     * @param id     必选 string 首页模块ID
     * @param name   必选 string 名称
     * @param image  必选 string 图片
     * @param url    必选 string H5对应的组件名
     * @param userId 必选 string 操作人id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 修改首页模块
     * @description 修改首页模块
     * @method POST
     * @url master/home/upDataHomeMasterplate
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 12
     * @Author: Ruking 18075121944
     * @Date: 2019/6/13 3:17 PM
     */
    @PostMapping("home/upDataHomeMasterplate")
    @ApiOperation(value = "修改首页模块", notes = "修改首页模块")
    ServerResponse upDataHomeMasterplate(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("image") String image,
            @RequestParam("url") String url,
            @RequestParam("userId") String userId);

}
