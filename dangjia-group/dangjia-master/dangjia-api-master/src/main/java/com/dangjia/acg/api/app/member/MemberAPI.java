package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 用户
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-master")
@Api(value = "用户接口", description = "用户接口")
public interface MemberAPI {


    /**
     * 获取用户详细资料
     * @param userToken  token
     * @return
     */
    @RequestMapping(value = "member/info", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户详细资料", notes = "获取用户详细资料")
    ServerResponse getMemberInfo(String userToken);
    /**
     * 登录 接口
     * @param phone 手机号
     * @param password 密码
     * @param userRole app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return
     */
    
    
    @RequestMapping(value = "member/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录", notes = "用户登录")
    ServerResponse login(
            @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone,
            @ApiParam(name = "password", value = "密码") @RequestParam("password") String password,
            @ApiParam(name = "userRole", value = "app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色") @RequestParam("userRole") String userRole) ;

    /**
     * 接口注册获取验证码
     * @param phone 手机号
     * @return
     */

    @RequestMapping(value = "member/registerCode", method = RequestMethod.POST)
    @ApiOperation(value = "接口注册获取验证码", notes = "接口注册获取验证码")
    ServerResponse registerCode( @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone) ;

    /**
     * 校验验证码并保存密码
     * @param phone 手机号
     * @param password 密码
     * @param smscode 验证码Code
     * @return
     */

    @RequestMapping(value = "member/checkRegister", method = RequestMethod.POST)
    @ApiOperation(value = "校验验证码并保存密码", notes = "校验验证码并保存密码")
    ServerResponse checkRegister(
            @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone,
            @ApiParam(name = "password", value = "密码") @RequestParam("password")String password,
            @ApiParam(name = "smscode", value = "验证码Code") @RequestParam("smscode") int smscode,
            @ApiParam(name = "invitationCode", value = "邀请码") @RequestParam("invitationCode") String invitationCode,
            @ApiParam(name = "userRole", value = "app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色") @RequestParam("userRole")  Integer userRole);

    /**
     * 工匠提交详细资料
     * @param user 用户详细信息
     * @param userToken  token
     * @return
     */

    @RequestMapping(value = "member/updateWokerRegister", method = RequestMethod.POST)
    @ApiOperation(value = "工匠提交详细资料", notes = "工匠提交详细资料")
    ServerResponse updateWokerRegister(@RequestParam("member") Member user, @RequestParam("userToken") String userToken,@RequestParam("userRole")  String userRole);
    
    /**
     * 找回密码 获取code
     * @param phone 手机号
     * @return
     */

    @RequestMapping(value = "member/forgotPasswordCode", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码", notes = "找回密码 获取code")
    ServerResponse forgotPasswordCode( @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone);

    /**
     * 找回密码校验验证码
     * @param phone 手机号
     * @param smscode 验证码Code
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "member/checkForgotPasswordCode", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码校验验证码", notes = "找回密码校验验证码")
    ServerResponse checkForgotPasswordCode(
            @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone,
            @ApiParam(name = "smscode", value = "验证码Code") @RequestParam("smscode") int smscode
    ) throws Exception;

    /**
     * 找回密码更新密码
     * @param phone 手机号
     * @param password 密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "member/updateForgotPassword", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码更新密码", notes = "找回密码更新密码")
    ServerResponse updateForgotPassword(
            @ApiParam(name = "phone", value = "手机号") @RequestParam("phone") String phone,
            @ApiParam(name = "password", value = "密码") @RequestParam("password")String password,
            @ApiParam(name = "token", value = "checkForgotPasswordCode返回的临时token") @RequestParam("token") String token
    );

     @RequestMapping(value = "member/getMyWallet", method = RequestMethod.POST)
     @ApiOperation(value = "统计我的钱包", notes = "统计我的钱包")
    ServerResponse getMyWallet(@RequestParam("userToken")String userToken);

     @RequestMapping(value = "member/getMyBillDetail", method = RequestMethod.POST)
     @ApiOperation(value = "我的钱包-收入-支出", notes = "我的钱包-收入-支出")
    ServerResponse getMyBillDetail(@RequestParam("userToken")String userToken,@RequestParam("type") Integer type,@RequestParam("pageDTO") PageDTO pageDTO);

    @RequestMapping(value = "member/getMyInvitation", method = RequestMethod.POST)
    @ApiOperation(value = "我的邀请码", notes = "我的邀请码")
    ServerResponse getMyInvitation(String userToken);
}

