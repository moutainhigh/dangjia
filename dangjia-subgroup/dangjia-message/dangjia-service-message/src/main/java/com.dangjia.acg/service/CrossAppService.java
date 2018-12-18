package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.common.model.cross.CrossBlacklist;
import cn.jmessage.api.common.model.cross.CrossFriendPayload;
import cn.jmessage.api.common.model.cross.CrossGroup;
import cn.jmessage.api.common.model.cross.CrossNoDisturb;
import cn.jmessage.api.group.MemberListResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨域应用维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class CrossAppService  extends BaseService{

    /**
     * 跨应用管理群组成员
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组id
     * @param addUsers 添加到群组的用户（任选）
     * @param delUsers 从群组删除的用户（任选）
     */
    public  void addOrRemoveMembersFromCrossGroup(String appType,String myAppType,long gid, String[] addUsers,String[] delUsers) {

        JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
        try {
            List<CrossGroup> crossGroups = new ArrayList<CrossGroup>();
            CrossGroup crossGroup = new CrossGroup.Builder()
                    .setAppKey(getAppkey(myAppType))
                    .setAddUsers(addUsers)
                    .setRemoveUsers(delUsers)
                    .build();
            crossGroups.add(crossGroup);
            CrossGroup[] array = new CrossGroup[crossGroups.size()];
            ResponseWrapper response = client.addOrRemoveCrossGroupMember(gid, crossGroups.toArray(array));
            LOG.info("Got response " + response.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  跨应用获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组ID
     * @return
     */
    public  List<Map>  getCrossGroupMembers(String appType, long gid) {
        try {
            List<Map> resultMap=new ArrayList<Map>();
            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MemberListResult result = client.getCrossGroupMembers(gid);
            for (cn.jmessage.api.group.MemberResult memberResult:result.getMembers()) {
                Map map=new HashMap();
                map.put("nickname",memberResult.getNickname());
                map.put("username",memberResult.getUsername());
                resultMap.add(map);
            }
            return resultMap;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 跨应用添加黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  添加的用户的数组
     * @param username 用户群组名
     */
    public  void addCrossBlacklist(String appType,String[] users,String username) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            List<CrossBlacklist> crossBlacklists = new ArrayList<CrossBlacklist>();
            CrossBlacklist blacklist = new CrossBlacklist.Builder()
                    .setAppKey(getAppkey(appType))
                    .addUsers(users)
                    .build();

            crossBlacklists.add(blacklist);
            CrossBlacklist[] array = new CrossBlacklist[crossBlacklists.size()];
            ResponseWrapper response = client.addCrossBlacklist(username, crossBlacklists.toArray(array));
            LOG.info("Got response " + response.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
    /**
     * 跨应用移除黑名单
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param users  移除的用户的数组
     * @param username 用户群组名
     */
    public  void deleteCrossBlacklist(String appType,String[] users,String username) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            List<CrossBlacklist> crossBlacklists = new ArrayList<CrossBlacklist>();
            CrossBlacklist blacklist = new CrossBlacklist.Builder()
                    .setAppKey(getAppkey(appType))
                    .addUsers(users)
                    .build();

            crossBlacklists.add(blacklist);
            CrossBlacklist[] array = new CrossBlacklist[crossBlacklists.size()];
            ResponseWrapper response = client.deleteCrossBlacklist(username, crossBlacklists.toArray(array));
            LOG.info("Got response " + response.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 跨应用免打扰设置
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param single 单聊免打扰，支持add删除数组（任选）
     * @param group 群聊免打扰，支持add删除数组（任选）
     * @param username 用户群组名
     */
    public  void setCrossNoDisturb(String appType,String [] single,Long[] group,String username) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            List<CrossNoDisturb> list = new ArrayList<CrossNoDisturb>();
            CrossNoDisturb crossNoDisturb = new CrossNoDisturb.Builder()
                    .setAppKey(getAppkey(appType))
                    .setRemoveSingleUsers(single)
                    .setRemoveGroupIds(group)
                    .build();
            list.add(crossNoDisturb);
            CrossNoDisturb[] array = new CrossNoDisturb[list.size()];
            ResponseWrapper response = client.setCrossNoDisturb(username, list.toArray(array));
            LOG.info("Got response " + response.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  跨应用添加好友
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param usernames 用户名username的json数组最多500个（必填）
     * @param username 用户群组名
     */
    public  void addCrossUsers(String appType,String[] usernames,String username) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            CrossFriendPayload payload = new CrossFriendPayload.Builder()
                    .setAppKey(getAppkey(appType))
                    .setUsers(usernames)
                    .build();
            ResponseWrapper response = client.addCrossFriends(username, payload);
            LOG.info("Got response " + response.toString());
        } catch (APIConnectionException e) {
            e.printStackTrace();
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

}
