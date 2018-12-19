package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class MemberController implements MemberAPI {

    @Autowired
    private MemberService memberService;

    /**
     * 获取用户详细资料
     * @param userToken  token
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getMemberInfo(String userToken){
        return memberService.getMemberInfo(userToken);
    }

    /**
     * 登录 接口
     * @param phone 手机号
     * @param password 密码
     * @param userRole app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse login(String phone, String password, String userRole) {
       return memberService.login( phone,  password,  userRole);
    }

    /**
     * 接口注册获取验证码
     * @param phone 手机号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse registerCode(String phone) {
        return memberService.registerCode( phone);
    }

    /**
     * 校验验证码并保存密码
     * @param phone 手机号
     * @param password 密码
     * @param smscode 验证码Code
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse checkRegister(String phone, String password,int smscode,String invitationCode,Integer userRole) {
        return memberService.checkRegister( phone,smscode,password,invitationCode,userRole);
    }

    /**
     * 工匠提交详细资料
     * @param user 用户详细信息
     * @param userToken  token
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateWokerRegister(Member user, String userToken, String userRole){
        return memberService.updateWokerRegister(user,userToken,userRole);
    }

    /**
     * 找回密码 获取code
     * @param phone 手机号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse forgotPasswordCode(String phone) {
        return memberService.forgotPasswordCode(phone);
    }

    /**
     * 找回密码校验验证码
     * @param phone 手机号
     * @param smscode 验证码Code
     * @return
     * @throws Exception
     */
    @Override
    @ApiMethod
    public ServerResponse checkForgotPasswordCode(String phone, int smscode) throws Exception {
        return memberService.checkForgotPasswordCode(phone,smscode);
    }

    /**
     * 找回密码更新密码
     * @param phone 手机号
     * @param password 密码
     * @return
     * @throws Exception
     */
    @Override
    @ApiMethod
    public ServerResponse updateForgotPassword(String phone, String password,String token){
        return memberService.updateForgotPassword(phone,password, token);
    }

    /**
     * 我的邀请码
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getMyInvitation(String userToken){
        return memberService.getMyInvitation(userToken);
    }
}

