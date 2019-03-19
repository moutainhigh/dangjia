package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAuths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "当家用户第三方认证接口", description = "当家用户第三方认证接口")
public interface MemberAuthsAPI {

    @RequestMapping(value = "memberauths/authsLogin", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户第三方认证登录", notes = "当家用户第三方认证登录")
    ServerResponse authsLogin(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("openType") Integer openType,
                              @RequestParam("openid") String openid,
                              @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "memberauths/oldUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家老用户", notes = "第三方认证登录绑定当家老用户")
    ServerResponse oldUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("memberAuths") MemberAuths memberAuths,
                                  @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "memberauths/newUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家新用户", notes = "第三方认证登录绑定当家新用户")
    ServerResponse newUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("smscode") int smscode,
                                  @RequestParam("invitationCode") String invitationCode,
                                  @RequestParam("memberAuths") MemberAuths memberAuths,
                                  @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "memberauths/bindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户绑定第三方认证", notes = "当家用户绑定第三方认证")
    ServerResponse bindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("memberAuths") MemberAuths memberAuths);

    @RequestMapping(value = "memberauths/cancelBindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户取消绑定第三方认证", notes = "当家用户取消绑定第三方认证")
    ServerResponse cancelBindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("userToken") String userToken,
                                             @RequestParam("openType") Integer openType,
                                             @RequestParam("openid") String openid);


}

