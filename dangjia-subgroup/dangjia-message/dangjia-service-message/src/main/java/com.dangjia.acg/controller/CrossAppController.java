package com.dangjia.acg.controller;

import com.dangjia.acg.api.CrossAppAPI;
import com.dangjia.acg.service.CrossAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 跨域应用维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class CrossAppController implements CrossAppAPI {
    @Autowired
    private CrossAppService crossAppService;

    /**
     * 跨应用管理群组成员
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param myAppType 管理用户所属的appType 必填（zx=当家装修，gj=当家工匠）
     * @param gid 群组id
     * @param addUsers 添加到群组的用户（任选）
     * @param delUsers 从群组删除的用户（任选）
     */
    public  void addOrRemoveMembersFromCrossGroup(String appType,String myAppType,long gid, String[] addUsers,String[] delUsers) {
        crossAppService.addOrRemoveMembersFromCrossGroup( appType, myAppType,gid,  addUsers, delUsers);
    }

    /**
     *  跨应用获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组ID
     * @return
     */
    public List<Map> getCrossGroupMembers(String appType, long gid) {
        return crossAppService.getCrossGroupMembers( appType, gid);
    }

    /**
     * 跨应用添加黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  添加的用户的数组
     * @param username 用户群组名
     */
    public  void addCrossBlacklist(String appType,String[] users,String username) {
        crossAppService.addCrossBlacklist( appType,  users, username);
    }
    /**
     * 跨应用移除黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  移除的用户的数组
     * @param username 用户群组名
     */
    public  void deleteCrossBlacklist(String appType,String[] users,String username) {
        crossAppService.deleteCrossBlacklist( appType, users,  username);
    }

    /**
     * 跨应用免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param single 单聊免打扰，支持add删除数组（任选）
     * @param group 群聊免打扰，支持add删除数组（任选）
     * @param username 用户群组名
     */
    public  void setCrossNoDisturb(String appType,String [] single,Long[] group,String username) {
        crossAppService.setCrossNoDisturb( appType, single,  group, username);
    }

    /**
     * 跨应用添加好友
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名username的json数组最多500个（必填）
     * @param username 用户群组名
     */
    public  void addCrossUsers(String appType,String[] usernames,String username) {
        crossAppService.addCrossUsers( appType, usernames, username);
    }

}
