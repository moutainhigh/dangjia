package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MemberController implements MemberAPI {

    @Autowired
    private MemberService memberService;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Override
    @ApiMethod
    public ServerResponse getMemberMobile(HttpServletRequest request, String id, String idType) {
        return memberService.getMemberMobile(request, id, idType);
    }

    @Override
    public ServerResponse getSmsCode(String phone) {
        return memberService.getSmsCode(phone);
    }

    @Override
    @ApiMethod
    public ServerResponse getMemberInfo(String userToken) {
        return memberService.getMemberInfo(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse login(String phone, String password, String userRole) {
        return memberService.login(phone, password, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse registerCode(String phone) {
        return memberService.registerCode(phone);
    }

    @Override
    @ApiMethod
    public ServerResponse checkRegister(HttpServletRequest request, String phone, String password, int smscode, String invitationCode, Integer userRole) {
        return memberService.checkRegister(request, phone, smscode, password, invitationCode, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse updateWokerRegister(Member user, String userToken, String userRole) {
        return memberService.updateWokerRegister(user, userToken, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse certification(String userToken, String name, String idcaoda, String idcaodb, String idcaodall, String idnumber) {
        return memberService.certification(userToken, name, idcaoda, idcaodb, idcaodall, idnumber);
    }

    @Override
    @ApiMethod
    public ServerResponse certificationWorkerType(String userToken, String workerTypeId) {
        return memberService.certificationWorkerType(userToken, workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse forgotPasswordCode(String phone) {
        return memberService.forgotPasswordCode(phone);
    }

    @Override
    @ApiMethod
    public ServerResponse checkForgotPasswordCode(String phone, int smscode) {
        return memberService.checkForgotPasswordCode(phone, smscode);
    }

    @Override
    @ApiMethod
    public ServerResponse updateForgotPassword(String phone, String password, String token) {
        return memberService.updateForgotPassword(phone, password, token);
    }

    @Override
    @ApiMethod
    public ServerResponse getMyInvitation(String userToken) {
        return memberService.getMyInvitation(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse getMembers(String userToken, String memberId, String phone) {
        return memberService.getMembers(userToken, memberId, phone);
    }

    @Override
    @ApiMethod
    public Object getMember(String userToken) {
        return constructionService.getMember(userToken);
    }
}

