package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.member.MemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ServerResponse getMemberInfo(String userToken, Integer userRole) {
        return memberService.getMemberInfo(userToken, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse login(String phone, String password,String loginMode, Integer userRole) {
        return memberService.login(phone, password,loginMode, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse registerCode(String phone,String codeType) {
        return memberService.registerCode(phone,codeType);
    }


    @Override
    @ApiMethod
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkRegister(HttpServletRequest request, String phone, String password, Integer smscode, String invitationCode, Integer userRole, String longitude, String latitude) {
        return memberService.checkRegister(request, phone, smscode, password, invitationCode, userRole, longitude, latitude);
    }

    /**
     * 注销账号
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public  ServerResponse cancellationAccountMember(String userToken){
        return memberService.cancellationAccountMember(userToken);
    }


    @Override
    @ApiMethod
    public ServerResponse updateWokerRegister(Member user, String userToken) {
        return memberService.updateWokerRegister(user, userToken);
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
    public ServerResponse checkForgotPasswordCode(String phone, Integer smscode) {
        return memberService.checkForgotPasswordCode(phone, smscode);
    }

    @Override
    @ApiMethod
    public ServerResponse updateForgotPassword(String phone, String password, String token) {
        return memberService.updateForgotPassword(phone, password, token);
    }
    @Override
    @ApiMethod
    public ServerResponse updateMethods(String workerId, Integer methods) {
        return memberService.updateMethods(workerId, methods);
    }
    @Override
    @ApiMethod
    public ServerResponse getMyInvitation(String userToken, Integer userRole) {
        return memberService.getMyInvitation(userToken, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse getMembers(String userToken, String memberId, String phone) {
        return memberService.getMembers(userToken, memberId, phone);
    }

    /**
     * 获取我的界面
     */
    @Override
    @ApiMethod
    public ServerResponse getMyHomePage(String userToken, Integer userRole) {
        return memberService.getMyHomePage(userToken, userRole);
    }
    /**
     * 获取我的徽章
     */
    @Override
    @ApiMethod
    public ServerResponse getMyInsigniaList(String userToken){
        return memberService.getMyInsigniaList(userToken);
    }
    /**
     * 获取我的徽章--徽章详情
     */
    @Override
    @ApiMethod
    public ServerResponse getMyInsigniaDetail(String userToken,String code){
        return memberService.getMyInsigniaDetail(userToken,code);
    }

    @Override
    @ApiMethod
    public ServerResponse  updateInsurances(Insurance insurance){
        return memberService.updateInsurances(insurance);
    }
    @Override
    @ApiMethod
    public ServerResponse  addInsurances(String userToken){
        return memberService.addInsurances(userToken);
    }
    @Override
    @ApiMethod
    public Object getMember(String userToken) {
        return constructionService.getMember(userToken);
    }


    @Override
    @ApiMethod
    public ServerResponse  myInsurances(HttpServletRequest request, String userToken, PageDTO pageDTO){
        return memberService.myInsurances(userToken, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse promotionList(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        return memberService.promotionList(userToken,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMember(HttpServletRequest request, String userToken,String houseId,String cityId) {
        return memberService.queryMember(userToken,houseId,cityId);
    }
}

