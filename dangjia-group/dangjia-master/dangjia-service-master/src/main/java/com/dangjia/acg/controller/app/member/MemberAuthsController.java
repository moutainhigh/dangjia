package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberAuthsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberAuths;
import com.dangjia.acg.service.member.MemberAuthsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 当家用户第三方认证
 */
@RestController
public class MemberAuthsController implements MemberAuthsAPI {

    @Autowired
    private MemberAuthsService memberAuthsService;

    @Override
    @ApiMethod
    public ServerResponse authsLogin(HttpServletRequest request, Integer openType, String openid, Integer userRole) {
        return memberAuthsService.authsLogin(openType, openid, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse oldUserBinding(HttpServletRequest request, String phone, String password, MemberAuths memberAuths, Integer userRole) {
        return memberAuthsService.oldUserBinding(phone, password, memberAuths, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse newUserBinding(HttpServletRequest request, String phone, String password,
                                         int smscode, String invitationCode, MemberAuths memberAuths, Integer userRole) {
        return memberAuthsService.newUserBinding(phone, password, smscode, invitationCode, memberAuths, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse bindingThirdParties(HttpServletRequest request, String userToken, MemberAuths memberAuths) {
        return memberAuthsService.bindingThirdParties(userToken, memberAuths);
    }

    @Override
    @ApiMethod
    public ServerResponse cancelBindingThirdParties(HttpServletRequest request, String userToken, Integer openType, String openid) {
        return memberAuthsService.cancelBindingThirdParties(userToken, openType, openid);
    }
}

