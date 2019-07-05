package com.dangjia.acg.api.user;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.user.UserDTO;
import com.dangjia.acg.dto.user.UserSearchDTO;
import com.dangjia.acg.modle.user.MainUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "系统用户管理接口", description = "系统用户管理接口")
public interface MainUserAPI {

    @RequestMapping(value = "/user/sysSwitching", method = RequestMethod.POST)
    @ApiOperation(value = "系统来源切换", notes = "系统来源切换")
    ServerResponse sysSwitching(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("source") Integer source);


    /**
     * 分页查询用户列表
     * @param request
     * @param pageDTO
     * @param userSearch
     * @param isJob 1：查询未离职的用户 2：查询未离职售前客服、工程部、工程部经理三个角色的用户  "" ：不传则展示所有
     * @return
     */
    @RequestMapping(value = "/user/getUsers", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询用户列表", notes = "分页查询用户列表")
    ServerResponse getUsers(@RequestParam("request") HttpServletRequest request,
                            @RequestParam("pageDTO") PageDTO pageDTO,
                            @RequestParam("userSearch") UserSearchDTO userSearch,
                            @RequestParam("isJob") Integer isJob);

    /**
     * 设置用户是否离职
     *
     * @return ok/fail
     */
    @RequestMapping(value = "/user/setJobUser", method = RequestMethod.POST)
    @ApiOperation(value = "设置用户是否离职", notes = "设置用户是否离职")
    ServerResponse setJobUser(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("id") String id,
                              @RequestParam("isJob") boolean isJob);

    /**
     * 指定某个用户为坐席
     *
     * @return ok/fail
     */
    @RequestMapping(value = "/user/setReceiveUser", method = RequestMethod.POST)
    @ApiOperation(value = "指定某个用户为坐席", notes = "指定某个用户为坐席")
    ServerResponse setReceiveUser(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("id") String id,
                                  @RequestParam("type") Integer type);

    /**
     * 设置用户[更新]
     *
     * @return ok/fail
     */
    @RequestMapping(value = "/user/setUser", method = RequestMethod.POST)
    @ApiOperation(value = "设置用户[新增或更新]", notes = "设置用户[新增或更新]")
    ServerResponse setUser(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("roleIds") String roleIds,
                           @RequestParam("member") MainUser user);

    /**
     * 设置用户[新增]
     *
     * @return ok/fail
     */
    @RequestMapping(value = "/user/addUser", method = RequestMethod.POST)
    @ApiOperation(value = "设置用户[新增或更新]", notes = "设置用户[新增或更新]")
    ServerResponse addUser(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("roleIds") String roleIds,
                           @RequestParam("member") MainUser user);

    /**
     * 删除用户
     *
     * @return ok/fail
     */
    @RequestMapping(value = "/user/delUser", method = RequestMethod.POST)
    @ApiOperation(value = "删除用户", notes = "删除用户")
    ServerResponse delUser(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("id") String id);

    /**
     * 恢复用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/user/recoverUser", method = RequestMethod.POST)
    @ApiOperation(value = "恢复用户", notes = "恢复用户")
    ServerResponse recoverUser(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id);

    /**
     * 查询用户数据
     *
     * @return map
     */
    @RequestMapping(value = "/user/getUserAndRoles", method = RequestMethod.POST)
    @ApiOperation(value = "查询用户数据", notes = "查询用户数据")
    ServerResponse getUserAndRoles(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id);

    /**
     * 登录【使用shiro中自带的HashedCredentialsMatcher结合ehcache（记录输错次数）配置进行密码输错次数限制】
     * </br>缺陷是，无法友好的在后台提供解锁用户的功能，当然，可以直接提供一种解锁操作，清除ehcache缓存即可，不记录在用户表中；
     * </br>
     *
     * @param user
     * @param rememberMe
     * @return
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录", notes = "登录")
    ServerResponse login(@RequestParam("request") HttpServletRequest request,
                         @RequestParam("member") UserDTO user,
                         @RequestParam(value = "rememberMe", required = false) boolean rememberMe);


    @RequestMapping(value = "/user/checkAuth", method = RequestMethod.POST)
    @ApiOperation(value = "檢查指定权限code，当前登录用户是否有权限", notes = "檢查指定权限code，当前登录用户是否有权限\"")
    ServerResponse checkAuth(@RequestParam("request") HttpServletRequest request, @RequestParam("rcode") String rcode);

    /**
     * 修改密码之确认手机号
     *
     * @param mobile
     * @param picCode
     * @return
     */
    @RequestMapping(value = "/user/updatePwd", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码之确认手机号", notes = "修改密码之确认手机号")
    ServerResponse updatePwd(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("mobile") String mobile,
                             @RequestParam("picCode") String picCode,
                             @RequestParam("mobileCode") String mobileCode);

    /**
     * 修改密码
     *
     * @param pwd
     * @param isPwd
     * @return
     */
    @RequestMapping(value = "/user/setPwd", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码", notes = "修改密码")
    ServerResponse setPwd(@RequestParam("request") HttpServletRequest request,
                          @RequestParam("pwd") String pwd,
                          @RequestParam("isPwd") String isPwd);

    @RequestMapping(value = "/user/findUserByMobile", method = RequestMethod.POST)
    @ApiOperation(value = "根据手机号获取用户信息", notes = "根据手机号获取用户信息")
    ServerResponse findUserByMobile(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("mobile") String mobile);

}
