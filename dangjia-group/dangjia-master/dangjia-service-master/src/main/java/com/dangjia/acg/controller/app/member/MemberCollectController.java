package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberCollectAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.service.member.MemberCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 当家用户第三方认证
 */
@RestController
public class MemberCollectController implements MemberCollectAPI {

    @Autowired
    private MemberCollectService memberCollectService;


    @Override
    @ApiMethod
    public ServerResponse queryCollectHouse(HttpServletRequest request, String userToken, PageDTO pageDTO){
        return memberCollectService.queryCollectHouse(request, userToken, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCollectGood(HttpServletRequest request, String userToken, PageDTO pageDTO){
        return memberCollectService.queryCollectGood(request, userToken, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addMemberCollect(HttpServletRequest request,String userToken,String collectId,String collectType){
        return memberCollectService.addMemberCollect(userToken, collectId,collectType);
    }

    @Override
    @ApiMethod
    public ServerResponse isMemberCollect(HttpServletRequest request, String userToken, String collectId,String collectType){
        return memberCollectService.isMemberCollect(request, userToken, collectId,collectType);
    }

    @Override
    @ApiMethod
    public ServerResponse delMemberCollect(HttpServletRequest request, String id){
        return memberCollectService.delMemberCollect(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRelated(HttpServletRequest request, String userToken) {
        return memberCollectService.queryRelated(userToken);
    }
}

