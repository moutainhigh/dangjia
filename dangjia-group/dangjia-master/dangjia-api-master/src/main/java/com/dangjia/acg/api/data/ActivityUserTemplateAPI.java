package com.dangjia.acg.api.data;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.ActivityUserTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxaing
 * Date: 2019/1/18 0031
 * Time: 20:07
 */
@FeignClient("dangjia-service-master")
@Api(value = "活动推送目标模板接口", description = "活动推送目标模板接口")
public interface ActivityUserTemplateAPI {

    /**
     * 获取所有推送模板
     * @param activityUserTemplate
     * @return
     */
    @PostMapping("/data/Activity/template/list")
    @ApiOperation(value = "获取所有推送模板", notes = "获取所有推送模板")
    ServerResponse queryActivityUserTemplate(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("pageDTO") PageDTO pageDTO,
                                             @RequestParam("activityUserTemplate") ActivityUserTemplate activityUserTemplate) ;




    /**
     * 新增
     * @param templateId 修改时，模板ID
     * @param activityUserTemplate
     * @return
     */
    @PostMapping("/data/Activity/template/add")
    @ApiOperation(value = "新增/修改推送模板", notes = "新增/修改推送模板")
    ServerResponse addActivityUserTemplate(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("templateId") String templateId,
                                           @RequestParam("activityUserTemplate") ActivityUserTemplate activityUserTemplate);
    /**
     * 推送用户推送活动优惠券
     * @param request
     * @param members 成员组
     * @param activityId
     * @return
     */
    @PostMapping("/data/Activity/template/send")
    @ApiOperation(value = "推送用户推送活动优惠券", notes = "推送用户推送活动优惠券")
     ServerResponse sendActivityPadPack(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("members")String members,
                                        @RequestParam("activityId")String activityId);
}
