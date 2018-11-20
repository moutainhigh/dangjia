package com.dangjia.acg.controller;

import cn.jmessage.api.common.model.UserPayload;
import cn.jmessage.api.user.UserInfoResult;
import cn.jmessage.api.user.UserListResult;
import cn.jmessage.api.user.UserStateListResult;
import cn.jmessage.api.user.UserStateResult;
import com.dangjia.acg.api.UserAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class UserController implements UserAPI {

    @Autowired
    private UserService userService;

    /**
     *  用户注册
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名（必填）用户名
     *                      开头：字母或者数字
     *                      字母，数字，下划线
     *                      英文点，减号，@
     * @param passwords 密码（必填）用户密码。极光IM服务器会MD5加密保存。
     */
    @Override
    @ApiMethod
    public  void registerUsers(String appType,String[] usernames,String[] passwords) {
        userService.registerUsers( appType, usernames, passwords);
    }


    /**
     * 获取用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @Override
    @ApiMethod
    public  UserInfoResult getUserInfo(String appType,String username) {
        return  userService.getUserInfo( appType, username);
    }
    /**
     * 用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @Override
    @ApiMethod
    public  UserStateResult getUserState(String appType,String username) {
        return  userService.getUserState( appType, username);
    }
    /**
     * 批量用户在线状态查询
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名数组
     * @return
     */
    @Override
    @ApiMethod
    public  UserStateListResult[]  getUsersState(String appType,String[] usernames) {
        return  userService.getUsersState( appType, usernames);
    }

    /**
     *  修改用户密码
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param password 新密码
     */
    @Override
    @ApiMethod
    public  void updatePassword(String appType,String username, String password) {
        userService.updatePassword( appType, username,password);
    }

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
    @Override
    @ApiMethod
    public  void updateUserInfo(String appType, UserPayload payload) {
        userService.updateUserInfo( appType, payload);
    }
    /**
     *  获取用户列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    @Override
    @ApiMethod
    public  UserListResult getUsers(String appType,int start,int count) {
        return userService.getUsers( appType, start, count);
    }
    /**
     *  删除用户
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     */
    @Override
    @ApiMethod
    public  void deleteUser(String appType,String username) {
        userService.deleteUser( appType, username);
    }
    
    /**
     * 获取应用管理员列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 起始序号从0开始
     * @param count 查询条数，最多2000
     * @return
     */
    @Override
    @ApiMethod
    public  UserListResult getAdminListByAppkey(String appType,int start,int count) {
       return   userService.getAdminListByAppkey( appType, start, count);
    }


    /**
     *  黑名单列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    @Override
    @ApiMethod
    public  UserInfoResult[] getBlackList(String appType,String username) {
        return  userService.getBlackList( appType, username);
    }

    /**
     *  移除黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 移除的用户名数组
     */
    @Override
    @ApiMethod
    public  void removeBlacklist(String appType,String username,String[] usernames) {
        userService.removeBlacklist( appType, username,usernames);
    }

    /**
     *  添加黑名单
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param usernames 移除的用户名数组
     */
    @Override
    @ApiMethod
    public  void addBlackList(String appType,String username,String[] usernames) {
        userService.addBlackList( appType, username,usernames);
    }

    /**
     *  免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param groupId 群聊免打扰，支持add remove数组（可选）
     * @param singleUsername 单聊免打扰，支持add remove数组 （可选）
     */
    @Override
    @ApiMethod
    public  void setNoDisturb(String appType,String username,Long[] groupId,String[] singleUsername ) {
        userService.setNoDisturb( appType, username,groupId,singleUsername);
    }

    /**
     *  添加好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 添加的好友数组
     */
    @Override
    @ApiMethod
    public  void addFriends(String appType,String username, String[] users) {
        userService.addFriends( appType, username,users);
    }

    /**
     * 删除好友
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param users 删除的好友数组
     */
    @Override
    @ApiMethod
    public  void deleteFriends(String appType,String username, String[] users) {
        userService.deleteFriends( appType, username,users);
    }

    /**
     * 批量更新好友备注
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     * @param noteNames 表示要添加的好友列表
     * @param others  其他备注信息
     * @param usernames 用户usernames数组 支持批量修改 最大限制500个
     */
    @Override
    @ApiMethod
    public  void updateFriendsNote(String appType,String username,String[] noteNames,String[] others,String[] usernames) {
        userService.updateFriendsNote( appType, username,noteNames, others,usernames);
    }

    /**
     *  获取好友列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    @Override
    @ApiMethod
    public  UserInfoResult[] getFriends(String appType,String username) {
        return  userService.getFriends( appType, username);
    }

    /**
     * 禁用用户
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 当前用户名
     */
    @Override
    @ApiMethod
    public  void forbidUser(String appType,String username) {
         userService.forbidUser( appType, username);
    }

}

