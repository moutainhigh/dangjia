package com.dangjia.acg.api;

import cn.jmessage.api.group.MemberListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 跨域应用维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "跨域应用维护接口", description = "跨域应用维护接口")
public interface CrossAppAPI {

    /**
     * 跨应用管理群组成员
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组id
     * @param addUsers 添加到群组的用户（任选）
     * @param delUsers 从群组删除的用户（任选）
     */
    @RequestMapping(value = "addOrRemoveMembersFromCrossGroup", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用管理群组成员", notes = "跨应用管理群组成员")
    void addOrRemoveMembersFromCrossGroup(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="gid",value = "群组id")@RequestParam("gid") long gid,
            @ApiParam(name ="addUsers",value = "（任选）添加到群组的用户")@RequestParam("addUsers") String[] addUsers,
            @ApiParam(name ="delUsers",value = "（任选）从群组删除的用户")@RequestParam("delUsers") String[] delUsers);

    /**
     *  跨应用获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组ID
     * @return
     */
    @RequestMapping(value = "getCrossGroupMembers", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用获取群组成员列表", notes = "跨应用获取群组成员列表")
    MemberListResult getCrossGroupMembers(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="gid",value = "群组ID")@RequestParam("gid") long gid);
    /**
     * 跨应用添加黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  添加的用户的数组
     * @param username 用户群组名
     */
    @RequestMapping(value = "addCrossBlacklist", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用添加黑名单", notes = "跨应用添加黑名单")
    void addCrossBlacklist(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="users",value = "添加的用户的数组")@RequestParam("users") String[] users,
            @ApiParam(name ="username",value = "用户群组名")@RequestParam("username") String username);
    /**
     * 跨应用移除黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  移除的用户的数组
     * @param username 用户群组名
     */
    @RequestMapping(value = "deleteCrossBlacklist", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用移除黑名单", notes = "跨应用移除黑名单")
    void deleteCrossBlacklist(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="users",value = "移除的用户的数组")@RequestParam("users") String[] users,
            @ApiParam(name ="username",value = "用户群组名")@RequestParam("username") String username);

    /**
     * 跨应用免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param single 单聊免打扰，支持add删除数组（任选）
     * @param group 群聊免打扰，支持add删除数组（任选）
     * @param username 用户群组名
     */
    @RequestMapping(value = "setCrossNoDisturb", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用免打扰设置", notes = "跨应用免打扰设置")
    void setCrossNoDisturb(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="single",value = "单聊免打扰，支持add删除数组（任选）")@RequestParam("single") String [] single,
            @ApiParam(name ="group",value = "群聊免打扰，支持add删除数组（任选）")@RequestParam("group") Long[] group,
            @ApiParam(name ="username",value = "用户群组名")@RequestParam("username") String username) ;

    /**
     * 跨应用添加好友
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名username的json数组最多500个（必填）
     * @param username 用户群组名
     */
    @RequestMapping(value = "addCrossUsers", method = RequestMethod.POST)
    @ApiOperation(value = "跨应用添加好友", notes = "跨应用添加好友")
    void addCrossUsers(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="usernames",value = "用户名username的json数组最多500个（必填）")@RequestParam("usernames") String[] usernames,
            @ApiParam(name ="username",value = "用户群组名")@RequestParam("username") String username) ;

}
