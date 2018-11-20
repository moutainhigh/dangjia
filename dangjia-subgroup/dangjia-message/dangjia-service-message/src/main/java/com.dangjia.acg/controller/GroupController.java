package com.dangjia.acg.controller;

import cn.jmessage.api.group.CreateGroupResult;
import cn.jmessage.api.group.GroupInfoResult;
import cn.jmessage.api.group.GroupListResult;
import cn.jmessage.api.group.MemberListResult;
import cn.jmessage.api.user.UserGroupsResult;
import com.dangjia.acg.api.GroupAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 群组维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class GroupController implements GroupAPI {

    @Autowired
    private GroupService groupService;
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
    @Override
    @ApiMethod
    public  CreateGroupResult createGroup(String appType,String owner_username,String name,String[] members_username,String avatar,String desc ,int flag) {
      return groupService.createGroup( appType, owner_username, name, members_username, avatar, desc , flag);
    }

    /**
     * 获取群组详情
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @Override
    @ApiMethod
    public  GroupInfoResult getGroupInfo(String appType,int groupId) {
        return groupService.getGroupInfo( appType, groupId);
    }
    /**
     * 获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @Override
    @ApiMethod
    public  MemberListResult getGroupMemberList(String appType,int groupId) {
        return groupService.getGroupMemberList( appType, groupId);
    }

    /**
     *  获取当前应用的群组列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 开始的记录数。
     * @param count 计数本次读取的记录数量。最大值为500
     */
    @Override
    @ApiMethod
    public  GroupListResult getGroupListByAppkey(String appType,int start,int count) {
        return groupService.getGroupListByAppkey( appType, start, count);
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
    @Override
    @ApiMethod
    public  void manageGroup(String appType,int groupId, String[] addList,String[] removeList) {
         groupService.manageGroup( appType, groupId, addList, removeList);
    }

    /**
     *  更新群组信息
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid gid群组ID
     * @param groupName 名称群名称
     * @param groupDesc 群描述
     * @param avatar 群组头像media_id
     */
    @Override
    @ApiMethod
    public  void updateGroupInfo(String appType,long gid, String groupName, String groupDesc, String avatar) {
        groupService.updateGroupInfo( appType, gid, groupName, groupDesc ,avatar);
    }

    /**
     *   获取某用户的群组列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param userName 名称群名称
     * @return
     */
    @Override
    @ApiMethod
    public  UserGroupsResult getGroupsByUser(String appType,String userName) {
        return  groupService.getGroupsByUser( appType, userName);
    }
    /**
     * 删除群组
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @Override
    @ApiMethod
    public  void deleteGroup(String appType,int groupId) {
        groupService.deleteGroup( appType, groupId);
    }

}
