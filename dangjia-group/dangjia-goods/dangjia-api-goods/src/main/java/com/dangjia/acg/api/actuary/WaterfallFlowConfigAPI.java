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
 * @类 名： ActuarialTemplateController
 * @功能描述：
 * @作者信息： fzh
 * @创建时间： 2020-2-23 15:35:10
 */
@Api(description = "瀑布布")
@FeignClient("dangjia-service-goods")
public interface WaterfallFlowConfigAPI {

    @PostMapping("/actuary/waterfall/queryWaterfallFlowConfig")
    @ApiOperation(value = "查询瀑布流", notes = "精算模板列表")
    ServerResponse queryWaterfallFlowConfig(@RequestParam("cityId") String cityId);

    //新增或修改瀑布流
    @PostMapping("/actuary/actuary/editWaterfallFlowConfig")
    @ApiOperation(value = "新增或修改瀑布流", notes = "新增或修改瀑布流")
    ServerResponse editWaterfallFlowConfig(@RequestParam("waterfallConfigId") String waterfallConfigId,
                                           @RequestParam("userId") String userId,
                                           @RequestParam("cityId") String cityId,
                                           @RequestParam("name") String name,
                                           @RequestParam("sort") Integer sort,
                                           @RequestParam("sourceInfoList") String sourceInfoList);

    //修改精算模板
    @PostMapping("/actuary/actuary/queryWaterfallFlowConfigInfo")
    @ApiOperation(value = "查询瀑布流详情", notes = "查询瀑布流详情")
    ServerResponse queryWaterfallFlowConfigInfo(@RequestParam("waterfallConfigId") String waterfallConfigId);

    //删除精算模板
    @PostMapping("/actuary/actuary/deleteWaterfallFlowConfig")
    @ApiOperation(value = "删除瀑布流配置", notes = "删除瀑布流配置")
    ServerResponse deleteWaterfallFlowConfig(@RequestParam("waterfallConfigId") String waterfallConfigId);


}