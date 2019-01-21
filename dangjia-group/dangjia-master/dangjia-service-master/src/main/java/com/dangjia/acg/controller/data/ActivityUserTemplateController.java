package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ActivityUserTemplateAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.ActivityUserTemplate;
import com.dangjia.acg.service.activity.ActivityUserTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxaing
 * Date: 2019/1/18 0031
 * Time: 20:07
 */
@RestController
public class ActivityUserTemplateController implements ActivityUserTemplateAPI {

    @Autowired
    private ActivityUserTemplateService activityUserTemplateService;

    /**
     * 获取所有推送模板
     * @param activityUserTemplate
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryActivityUserTemplate(HttpServletRequest request, PageDTO pageDTO, ActivityUserTemplate activityUserTemplate) {
        return activityUserTemplateService.queryActivityUserTemplate(request, pageDTO, activityUserTemplate);
    }




    /**
     * 新增
     * @param templateId 修改时，模板ID
     * @param activityUserTemplate
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addActivityUserTemplate(HttpServletRequest request, String templateId, ActivityUserTemplate activityUserTemplate) {
            return activityUserTemplateService.addActivityUserTemplate(request, templateId, activityUserTemplate);
    }
    /**
     * 可用优惠券数据
     * @param request
     * @param members 成员组
     * @param activityId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse sendActivityPadPack(HttpServletRequest request,String members,String activityId){
        ServerResponse serverResponse=activityUserTemplateService.sendActivityPadPack(request, members, activityId);
        return serverResponse;
    }
}
