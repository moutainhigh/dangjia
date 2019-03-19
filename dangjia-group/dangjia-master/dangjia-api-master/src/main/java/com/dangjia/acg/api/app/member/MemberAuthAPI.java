package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "当家用户第三方认证接口", description = "当家用户第三方认证接口")
public interface MemberAuthAPI {

    @RequestMapping(value = "memberAuth/authLogin", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户第三方认证登录", notes = "当家用户第三方认证登录")
    ServerResponse authLogin(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("openType") Integer openType,
                             @RequestParam("unionid") String unionid,
                             @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "memberAuth/oldUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家老用户", notes = "第三方认证登录绑定当家老用户")
    ServerResponse oldUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("memberAuth") MemberAuth memberAuth);

    @RequestMapping(value = "memberAuth/newUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家新用户", notes = "第三方认证登录绑定当家新用户")
    ServerResponse newUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("smscode") int smscode,
                                  @RequestParam("invitationCode") String invitationCode,
                                  @RequestParam("memberAuth") MemberAuth memberAuth);

    @RequestMapping(value = "memberAuth/bindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户绑定第三方认证", notes = "当家用户绑定第三方认证")
    ServerResponse bindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("memberAuth") MemberAuth memberAuth);

    @RequestMapping(value = "memberAuth/cancelBindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户取消绑定第三方认证", notes = "当家用户取消绑定第三方认证")
    ServerResponse cancelBindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("userToken") String userToken,
                                             @RequestParam("openType") Integer openType,
                                             @RequestParam("password") String password,
                                             @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "memberAuth/isBindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户判断是否绑定第三方认证", notes = "当家用户判断是否绑定第三方认证")
    ServerResponse isBindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("userToken") String userToken,
                                         @RequestParam("openType") Integer openType,
                                         @RequestParam("userRole") Integer userRole);


}

