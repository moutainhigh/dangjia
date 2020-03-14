package com.dangjia.acg.controller.feedback;

import com.dangjia.acg.api.feedback.UserFeedbackAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.feedback.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ljl
 * @descrilbe 用户反馈控制层
 */
@RestController
public class UserFeedbackController implements UserFeedbackAPI {

    @Autowired
    private UserFeedbackService feedbackService;


    @Override
    @ApiMethod
    public ServerResponse addFeedbackInFo(HttpServletRequest request,
                                          String userToken, String userId,
                                          Integer appType, String image, String remark) {
        return feedbackService.addFeedbackInFo(userToken,userId,appType,image,remark);
    }

    @Override
    @ApiMethod
    public ServerResponse  queryFeedbackInFo(HttpServletRequest request,
                                             PageDTO pageDTO, Integer appType, Integer feedbackType,
                                             String beginDate, String endDate){
        return feedbackService.queryFeedbackInFo(pageDTO,appType,feedbackType,beginDate,endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse  queryFeedbackItemInFo(HttpServletRequest request,String id,String feedbackId){
        return feedbackService.queryFeedbackItemInFo(id,feedbackId);
    }

    @Override
    @ApiMethod
    public ServerResponse exportFeedbackInFo(HttpServletResponse request,
                                             Integer appType,
                                             Integer feedbackType,
                                              String beginDate, String endDate){
        return feedbackService.exportFeedbackInFo(request,appType,feedbackType,beginDate,endDate);
    }


}
