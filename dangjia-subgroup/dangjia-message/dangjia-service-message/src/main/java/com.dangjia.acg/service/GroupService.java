package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.group.CreateGroupResult;
import cn.jmessage.api.group.GroupInfoResult;
import cn.jmessage.api.group.GroupListResult;
import cn.jmessage.api.group.MemberListResult;
import cn.jmessage.api.user.UserGroupsResult;
import org.springframework.stereotype.Service;
/**
 * 群组维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class GroupService  extends BaseService{

    /**
     * 创建群组
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param owner_username（必填）群主用户名
     * @param name（必填）群组名字
     * 支持的字符：全部，包括表情符号。
     * @param members_username 成员用户名
     * @param avatar（选填）群组头像，上传接口所获得的media_id
     * @param desc（选填）群描述
     *              支持的字符：全部，包括表情符号。
     * @param flag（选填）类型
     *              1 - 私有群（默认）
     *              2 - 公开群
     *              不指定标志，默认为1
     */
    public  CreateGroupResult createGroup(String appType,String owner_username,String name,String[] members_username,String avatar,String desc ,int flag) {
      
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            CreateGroupResult res = client.createGroup(owner_username, name, desc, avatar, flag, members_username);
            LOG.info(res.getName());
            return res;
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
     * 获取群组详情
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    public  GroupInfoResult getGroupInfo(String appType,int groupId) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            GroupInfoResult res = client.getGroupInfo(groupId);
            LOG.info(res.getOriginalContent());
            return res;
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
     * 获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    public  MemberListResult getGroupMemberList(String appType,int groupId) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MemberListResult res = client.getGroupMembers(groupId);
            LOG.info(res.getOriginalContent());
            return res;
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
     *  获取当前应用的群组列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 开始的记录数。
     * @param count 计数本次读取的记录数量。最大值为500
     */
    public  GroupListResult getGroupListByAppkey(String appType,int start,int count) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            GroupListResult res = client.getGroupListByAppkey(start, count);
            LOG.info(res.getOriginalContent());
            return res;
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
     *  更新群组成员
     *      批量增加与删除某gid群组的成员。
     *      群组成员将收到增加与删除成员的通知。
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId gid群组ID
     * @param addList add json数组表示要添加到群组的用户（任选）
     * @param removeList remove json数组表示要从群组删除的用户（任选）
     *        addList和removeList  两者至少要有一个
     */
    public  void manageGroup(String appType,int groupId, String[] addList,String[] removeList) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            client.addOrRemoveMembers(groupId, addList, removeList );
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  更新群组信息
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid gid群组ID
     * @param groupName 名称群名称
     * @param groupDesc 群描述
     * @param avatar 群组头像media_id
     */
    public  void updateGroupInfo(String appType,long gid, String groupName, String groupDesc, String avatar) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            client.updateGroupInfo(gid, groupName, groupDesc, avatar);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *   获取某用户的群组列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param userName 名称群名称
     * @return
     */
    public  UserGroupsResult getGroupsByUser(String appType,String userName) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserGroupsResult res = client.getGroupListByUser(userName);
            LOG.info(res.getOriginalContent());
            return res;
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
     * 删除群组
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    public  void deleteGroup(String appType,int groupId) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            client.deleteGroup(groupId);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

}
