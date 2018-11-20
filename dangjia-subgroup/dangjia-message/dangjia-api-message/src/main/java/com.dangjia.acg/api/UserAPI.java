package com.dangjia.acg.api;

import cn.jmessage.api.common.model.UserPayload;
import cn.jmessage.api.user.UserInfoResult;
import cn.jmessage.api.user.UserListResult;
import cn.jmessage.api.user.UserStateListResult;
import cn.jmessage.api.user.UserStateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "用户维护接口", description = "用户维护接口")
public interface UserAPI {


    /**
     *  用户注册
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名（必填）用户名
     *                      开头：字母或者数字
     *                      字母，数字，下划线
     *                      英文点，减号，@
     * @param passwords 密码（必填）用户密码。极光IM服务器会MD5加密保存。
     */
    @RequestMapping(value = "registerUsers", method = RequestMethod.POST)
    @ApiOperation(value = "用户注册", notes = "用户注册")
    void registerUsers(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="usernames",value = "（必填）用户名,开头：字母或者数字 字母，数字，下划线 英文点，减号，@")@RequestParam("usernames") String[] usernames,
            @ApiParam(name ="passwords",value = "（必填）用户密码。极光IM服务器会MD5加密保存。")@RequestParam("passwords") String[] passwords);


    /**
     * 获取用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    UserInfoResult getUserInfo(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username) ;
    /**
     * 用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "getUserState", method = RequestMethod.POST)
    @ApiOperation(value = "用户在线状态查询", notes = "用户在线状态查询")
    UserStateResult getUserState(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username) ;
    /**
     * 批量用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名数组
     * @return
     */
    @RequestMapping(value = "getUsersState", method = RequestMethod.POST)
    @ApiOperation(value = "批量用户在线状态查询", notes = "批量用户在线状态查询")
    UserStateListResult[]  getUsersState(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="usernames",value = "用户名数组")@RequestParam("usernames") String[] usernames) ;

    /**
     *  修改用户密码
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param password 新密码
     */
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    void updatePassword(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="password",value = "新密码")@RequestParam("password") String password) ;

    /**
     * 更新用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param payload 用户信息对象
     *                nickname （选填）用户昵称
     *                      不支持的字符：英文字符： \n \r\n
     *                avatar （选填）头像
     *                      需要填上从文件上传接口获得的media_id
     *                birthday （选填）生日 example: 1990-01-24
     *                      yyyy-MM-dd
     *                signature （选填）签名
     *                      支持的字符：全部，包括 Emoji
     *                gender （选填） 性别
     *                      0 - 未知， 1 - 男 ，2 - 女
     *                region （选填）地区
     *                      支持的字符：全部，包括 Emoji
     *                address （选填）地址
     *                      支持的字符：全部，包括 Emoji
     *                extras (选填) 用户自定义json对象
     *
     */
    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    void updateUserInfo(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="payload",value = "用户信息对象")@RequestParam("payload") UserPayload payload);
    /**
     *  获取用户列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    @RequestMapping(value = "getUsers", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    UserListResult getUsers(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="start",value = "起始序号从0开始")@RequestParam("start") int start,
            @ApiParam(name ="count",value = "查询条数，最多2000")@RequestParam("count") int count);
    /**
     *  删除用户
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     */
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    @ApiOperation(value = "删除用户", notes = "删除用户")
    void deleteUser(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("start") String username) ;
    
    /**
     * 获取应用管理员列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    @RequestMapping(value = "getAdminListByAppkey", method = RequestMethod.POST)
    @ApiOperation(value = "获取应用管理员列表", notes = "获取应用管理员列表")
    UserListResult getAdminListByAppkey(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="start",value = "起始序号从0开始")@RequestParam("start") int start,
            @ApiParam(name ="count",value = "查询条数，最多2000")@RequestParam("count") int count);


    /**
     *  黑名单列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "getBlackList", method = RequestMethod.POST)
    @ApiOperation(value = "黑名单列表", notes = "黑名单列表")
    UserInfoResult[] getBlackList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username);

    /**
     *  移除黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 移除的用户名数组
     */
    @RequestMapping(value = "removeBlacklist", method = RequestMethod.POST)
    @ApiOperation(value = "移除黑名单", notes = "移除黑名单")
    void removeBlacklist(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="usernames",value = "移除的用户名数组")@RequestParam("usernames") String[] usernames) ;

    /**
     *  添加黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 添加的用户名数组
     */
    @RequestMapping(value = "addBlackList", method = RequestMethod.POST)
    @ApiOperation(value = "添加黑名单", notes = "添加黑名单")
    void addBlackList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="usernames",value = "添加的用户名数组")@RequestParam("usernames") String[] usernames);

    /**
     *  免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param groupId 群聊免打扰，支持add remove数组（可选）
     * @param singleUsername 单聊免打扰，支持add remove数组 （可选）
     */
    @RequestMapping(value = "setNoDisturb", method = RequestMethod.POST)
    @ApiOperation(value = "免打扰设置", notes = "免打扰设置")
    void setNoDisturb(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="groupId",value = "群聊免打扰，支持add remove数组（可选）")@RequestParam("groupId") Long[] groupId,
            @ApiParam(name ="singleUsername",value = "单聊免打扰，支持add remove数组（可选）")@RequestParam("singleUsername") String[] singleUsername ) ;

    /**
     *  添加好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 添加的好友数组
     */
    @RequestMapping(value = "addFriends", method = RequestMethod.POST)
    @ApiOperation(value = "添加好友", notes = "添加好友")
    void addFriends(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "当前用户名")@RequestParam("username") String username,
            @ApiParam(name ="users",value = "添加的好友数组")@RequestParam("users") String[] users) ;

    /**
     * 删除好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 删除的好友数组
     */
    @RequestMapping(value = "deleteFriends", method = RequestMethod.POST)
    @ApiOperation(value = "删除好友", notes = "删除好友")
    void deleteFriends(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "当前用户名")@RequestParam("username") String username,
            @ApiParam(name ="users",value = "删除的好友数组")@RequestParam("users") String[] users) ;

    /**
     * 批量更新好友备注
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param noteNames 表示要添加的好友列表
     * @param others  其他备注信息
     * @param usernames 用户usernames数组 支持批量修改 最大限制500个
     */
    @RequestMapping(value = "updateFriendsNote", method = RequestMethod.POST)
    @ApiOperation(value = "批量更新好友备注", notes = "批量更新好友备注,支持批量修改 最大限制500个")
    void updateFriendsNote(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "当前用户名")@RequestParam("username") String username,
            @ApiParam(name ="noteNames",value = "表示要添加的好友列表")@RequestParam("noteNames") String[] noteNames,
            @ApiParam(name ="others",value = "其他备注信息")@RequestParam("others") String[] others,
            @ApiParam(name ="usernames",value = "用户usernames数组")@RequestParam("usernames") String[] usernames);

    /**
     *  获取好友列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    @RequestMapping(value = "getFriends", method = RequestMethod.POST)
    @ApiOperation(value = "获取好友列表", notes = "获取好友列表")
    UserInfoResult[] getFriends(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "当前用户名")@RequestParam("username") String username) ;

    /**
     * 禁用用户
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    @RequestMapping(value = "forbidUser", method = RequestMethod.POST)
    @ApiOperation(value = "禁用用户", notes = "禁用用户")
    void forbidUser(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "当前用户名")@RequestParam("username") String username);

}

