package com.dangjia.acg.api;

import cn.jmessage.api.group.GroupInfoResult;
import cn.jmessage.api.group.GroupListResult;
import cn.jmessage.api.group.MemberListResult;
import cn.jmessage.api.user.UserGroupsResult;
import com.dangjia.acg.dto.CreateGroupResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 群组维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "群组维护接口", description = "群组维护接口")
public interface GroupAPI {

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
    @RequestMapping(value = "createGroup", method = RequestMethod.POST)
    @ApiOperation(value = "创建群组", notes = "创建群组")
    CreateGroupResultDTO createGroup(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="owner_username",value = "（必填）群主用户名")@RequestParam("owner_username") String owner_username,
            @ApiParam(name ="name",value = "（必填）群组名字  支持的字符：全部，包括表情符号。")@RequestParam("name") String name,
            @ApiParam(name ="members_username",value = "成员用户名")@RequestParam("members_username") String[] members_username,
            @ApiParam(name ="avatar",value = "（选填）群组头像，上传接口所获得的media_id")@RequestParam("avatar") String avatar,
            @ApiParam(name ="desc",value = "（选填）群描述")@RequestParam("desc") String desc ,
            @ApiParam(name ="flag",value = "（选填）类型： 1 - 私有群（默认），2 - 公开群，不指定标志，默认为1 ")@RequestParam("flag") int flag);
    /**
     * 获取群组详情
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @RequestMapping(value = "getGroupInfo", method = RequestMethod.POST)
    @ApiOperation(value = "获取群组详情", notes = "获取群组详情")
    GroupInfoResult getGroupInfo(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="groupId",value = "群组ID。由创建群组时分配。")@RequestParam("groupId") int groupId) ;
    /**
     * 获取群组成员列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @RequestMapping(value = "getGroupMemberList", method = RequestMethod.POST)
    @ApiOperation(value = "获取群组成员列表", notes = "获取群组成员列表")
    MemberListResult getGroupMemberList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="groupId",value = "群组ID。由创建群组时分配。")@RequestParam("groupId") int groupId) ;

    /**
     *  获取当前应用的群组列表
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param start 开始的记录数。
     * @param count 计数本次读取的记录数量。最大值为500
     */
    @RequestMapping(value = "getGroupListByAppkey", method = RequestMethod.POST)
    @ApiOperation(value = "获取当前应用的群组列表", notes = "获取当前应用的群组列表")
    GroupListResult getGroupListByAppkey(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="start",value = "开始的记录数")@RequestParam("start") int start,
            @ApiParam(name ="count",value = "计数本次读取的记录数量。最大值为500")@RequestParam("count") int count);

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
    @RequestMapping(value = "manageGroup", method = RequestMethod.POST)
    @ApiOperation(value = "更新群组成员", notes = "更新群组成员， 两者至少要有一个")
    String manageGroup(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="groupId",value = "gid群组ID")@RequestParam("groupId") int groupId,
            @ApiParam(name ="addList",value = "dd json数组表示要添加到群组的用户（任选）")@RequestParam("addList") String[] addList,
            @ApiParam(name ="removeList",value = "remove json数组表示要从群组删除的用户（任选）")@RequestParam("removeList") String[] removeList) ;

    /**
     *  更新群组信息
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid gid群组ID
     * @param groupName 名称群名称
     * @param groupDesc 群描述
     * @param avatar 群组头像media_id
     */
    @RequestMapping(value = "updateGroupInfo", method = RequestMethod.POST)
    @ApiOperation(value = "更新群组信息", notes = "更新群组信息")
    void updateGroupInfo(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="gid",value = "群组id")@RequestParam("gid") long gid,
            @ApiParam(name ="groupName",value = "名称群名称")@RequestParam("groupName") String groupName,
            @ApiParam(name ="groupDesc",value = "群描述")@RequestParam("groupDesc") String groupDesc,
            @ApiParam(name ="avatar",value = "群组头像media_id")@RequestParam("avatar") String avatar);

    /**
     *   获取某用户的群组列表
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param userName 名称群名称
     * @return
     */
    @RequestMapping(value = "getGroupsByUser", method = RequestMethod.POST)
    @ApiOperation(value = "获取某用户的群组列表", notes = "获取某用户的群组列表")
    UserGroupsResult getGroupsByUser(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="userName",value = "名称群名称")@RequestParam("userName") String userName) ;
    /**
     * 删除群组
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId 群组ID。由创建群组时分配。
     * @return
     */
    @RequestMapping(value = "deleteGroup", method = RequestMethod.POST)
    @ApiOperation(value = "删除群组", notes = "删除群组")
    void deleteGroup(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="groupId",value = "群组ID。由创建群组时分配。")@RequestParam("groupId") int groupId);

}
