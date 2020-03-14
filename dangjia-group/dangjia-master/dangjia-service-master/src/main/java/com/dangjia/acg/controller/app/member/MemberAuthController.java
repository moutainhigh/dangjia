package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberAuthAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberAuth;
import com.dangjia.acg.service.member.MemberAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 当家用户第三方认证
 */
@RestController
public class MemberAuthController implements MemberAuthAPI {

    @Autowired
    private MemberAuthService memberAuthService;

    @Override
    @ApiMethod
    public ServerResponse authLogin(HttpServletRequest request, Integer openType, String unionid, Integer userRole) {
        return memberAuthService.authLogin(openType, unionid, userRole);
    }

    @Override
    @ApiMethod
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse oldUserBinding(HttpServletRequest request, String phone, String password, MemberAuth memberAuth) {
        return memberAuthService.oldUserBinding(phone, password, memberAuth);
    }

    @Override
    @ApiMethod
    public ServerResponse newUserBinding(HttpServletRequest request, String phone, String password,
                                         Integer smscode, String invitationCode, MemberAuth memberAuth, String longitude, String latitude) {
        return memberAuthService.newUserBinding(request, phone, password, smscode, invitationCode, memberAuth, longitude, latitude);
    }

    @Override
    @ApiMethod
    public ServerResponse bindingThirdParties(HttpServletRequest request, String userToken, MemberAuth memberAuth) {
        return memberAuthService.bindingThirdParties(userToken, memberAuth);
    }

    @Override
    @ApiMethod
    public ServerResponse cancelBindingThirdParties(HttpServletRequest request, String userToken, Integer openType, String password, Integer userRole) {
        return memberAuthService.cancelBindingThirdParties(userToken, openType, password, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse isBindingThirdParties(HttpServletRequest request, String userToken, Integer openType, Integer userRole) {
        return memberAuthService.isBindingThirdParties(userToken, openType, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse miniProgramLogin(HttpServletRequest request, String code) {
        return memberAuthService.miniProgramLogin(code);
    }

    @Override
    @ApiMethod
    public ServerResponse decodeWxAppPhone(HttpServletRequest request, String encrypted, String iv, String sessionKey) {
        return memberAuthService.decodeWxAppPhone(request, encrypted, iv, sessionKey);
    }

    @Override
    @ApiMethod
    public ServerResponse miniProgramCodeRegister(HttpServletRequest request, String encrypted, String iv, String sessionKey, String openid, String unionid, String name, String iconurl) {
        return memberAuthService.miniProgramCodeRegister(request, encrypted, iv, sessionKey, openid, unionid, name, iconurl);
    }

    @Override
    @ApiMethod
    public ServerResponse miniProgramPhoneRegister(HttpServletRequest request, String phone, String smscode, String openid, String unionid, String name, String iconurl) {
        return memberAuthService.miniProgramPhoneRegister(request, phone, smscode, openid, unionid, name, iconurl);
    }
}

