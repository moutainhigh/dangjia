package com.dangjia.acg.api;

import cn.jmessage.api.chatroom.ChatRoomListResult;
import cn.jmessage.api.chatroom.ChatRoomMemberList;
import cn.jmessage.api.chatroom.CreateChatRoomResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 聊天室维护
 *
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "极光聊天室管理接口", description = "极光聊天室管理接口")
public interface ChatRoomAPI {


    /**
     * 创建聊天室
     *
     * @param appType            应用类型（zx=当家装修，gj=当家工匠）
     * @param name               （必填）聊天室名称
     * @param desc               （选填）描述
     * @param owner（必填）聊天室拥有者
     * @param usernames（选填）成员用户名
     * @return
     */
    @RequestMapping(value = "createChatRoom", method = RequestMethod.POST)
    @ApiOperation(value = "创建聊天室", notes = "创建聊天室")
    CreateChatRoomResult createChatRoom(
            @ApiParam(name = "appType", value = "应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "name", value = "（必填）聊天室名称") @RequestParam("name") String name,
            @ApiParam(name = "desc", value = "（选填）描述") @RequestParam("desc") String desc,
            @ApiParam(name = "owner", value = "（必填）聊天室拥有者") @RequestParam("owner") String owner,
            @ApiParam(name = "usernames", value = "（选填）成员用户名") @RequestParam("usernames") String[] usernames);

    /**
     * 获取聊天室详情
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param roomIds (必填)聊天室ID数组
     */
    @RequestMapping(value = "getBatchChatRoomInfo", method = RequestMethod.POST)
    @ApiOperation(value = "获取聊天室详情", notes = "获取聊天室详情")
    ChatRoomListResult getBatchChatRoomInfo(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomIds", value = "(必填)聊天室ID数组") @RequestParam("roomIds") long... roomIds);

    /**
     * GET获取用户聊天室列表
     *
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username （必填）用户名
     * @return
     */
    @RequestMapping(value = "getBatchChatRoomInfo", method = RequestMethod.GET)
    @ApiOperation(value = "GET获取用户聊天室列表", notes = "GET获取用户聊天室列表")
    ChatRoomListResult getUserChatRoomInfo(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "username", value = " （必填）用户名") @RequestParam("username") String username);

    /**
     * GET获取应用下聊天室列表
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param start   开始数
     * @param count   总数
     * @return
     */
    @RequestMapping(value = "getAppChatRoomInfo", method = RequestMethod.GET)
    @ApiOperation(value = "GET获取用户聊天室列表", notes = "GET获取用户聊天室列表")
    ChatRoomListResult getAppChatRoomInfo(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "start", value = " 开始数") @RequestParam("start") int start,
            @ApiParam(name = "count", value = " 总数") @RequestParam("count") int count);

    /**
     * 更新聊天室信息
     *
     * @param appType       应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId        聊天室ID
     * @param ownerUsername 聊天室拥有者用户名
     * @param name          聊天室名称
     * @param desc          聊天室描述
     */
    @RequestMapping(value = "updateChatRoomInfo", method = RequestMethod.POST)
    @ApiOperation(value = "更新聊天室信息", notes = "更新聊天室信息")
    void updateChatRoomInfo(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId,
            @ApiParam(name = "ownerUsername", value = " 聊天室拥有者用户名") @RequestParam("ownerUsername") String ownerUsername,
            @ApiParam(name = "name", value = " 聊天室名称") @RequestParam("name") String name,
            @ApiParam(name = "desc", value = " 聊天室描述") @RequestParam("desc") String desc);

    /**
     * 删除聊天室
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId  聊天室ID
     */
    @RequestMapping(value = "deleteChatRoom", method = RequestMethod.POST)
    @ApiOperation(value = "删除聊天室", notes = "删除聊天室")
    void deleteChatRoom(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId);


    /**
     * 修改用户禁言状态
     *
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId   聊天室ID
     * @param username 用户名
     * @param flag     禁言状态:0表示不禁言1表示开启禁言必填
     */
    @RequestMapping(value = "updateUserSpeakStatus", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户禁言状态", notes = "修改用户禁言状态")
    void updateUserSpeakStatus(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId,
            @ApiParam(name = "username", value = " 用户名") @RequestParam("username") String username,
            @ApiParam(name = "flag", value = " 禁言状态:0表示不禁言1表示开启禁言必填") @RequestParam("flag") int flag);

    /**
     * 获取聊天室成员列表
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId  聊天室ID。
     * @param start   开始数
     * @param count   总数
     * @return
     */
    @RequestMapping(value = "getChatRoomMembers", method = RequestMethod.POST)
    @ApiOperation(value = "获取聊天室成员列表", notes = "获取聊天室成员列表")
    ChatRoomMemberList getChatRoomMembers(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId,
            @ApiParam(name = "start", value = " 开始数") @RequestParam("start") int start,
            @ApiParam(name = "count", value = " 总数") @RequestParam("count") int count);

    /**
     * 添加聊天室成员
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId  聊天室ID。
     * @param members 需要添加的成员组 数组最多支持3000个
     */
    @RequestMapping(value = "addChatRoomMember", method = RequestMethod.POST)
    @ApiOperation(value = "添加聊天室成员", notes = "添加聊天室成员")
    void addChatRoomMember(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId,
            @ApiParam(name = "members", value = " 需要添加的成员组 数组最多支持3000个") @RequestParam("members") String[] members);

    /**
     * 移除聊天室成员
     *
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param roomId  聊天室ID。
     * @param members 需要删除的成员组 数组最多支持3000个
     */
    @RequestMapping(value = "deleteChatRoomMember", method = RequestMethod.POST)
    @ApiOperation(value = "移除聊天室成员", notes = "移除聊天室成员")
    void deleteChatRoomMember(
            @ApiParam(name = "appType", value = " 应用类型（zx=当家装修，gj=当家工匠）") @RequestParam("appType") String appType,
            @ApiParam(name = "roomId", value = " 聊天室ID") @RequestParam("roomId") long roomId,
            @ApiParam(name = "members", value = " 需要删除的成员组 数组最多支持3000个") @RequestParam("members") String[] members);

}
