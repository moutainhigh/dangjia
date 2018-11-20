package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.chatroom.ChatRoomClient;
import cn.jmessage.api.chatroom.ChatRoomListResult;
import cn.jmessage.api.chatroom.ChatRoomMemberList;
import cn.jmessage.api.chatroom.CreateChatRoomResult;
import cn.jmessage.api.common.model.Members;
import cn.jmessage.api.common.model.chatroom.ChatRoomPayload;
import org.springframework.stereotype.Service;

/**
 * 聊天室维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class ChatRoomService  extends BaseService{

  /**
   *  创建聊天室
   * @param appType 应用类型（zx=当家装修，gj=当家工匠）
   * @param name （必填）聊天室名称
   * @param desc （选填）描述
   * @param owner（必填）聊天室拥有者
   * @param usernames（选填）成员用户名
   * @return
   */
  public CreateChatRoomResult createChatRoom(String appType,String name,String desc,String owner,String[] usernames) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ChatRoomPayload payload = ChatRoomPayload.newBuilder()
              .setName(name)
              .setDesc(desc)
              .setOwnerUsername(owner)
              .setMembers(Members.newBuilder().addMember(usernames).build())
              .build();
      CreateChatRoomResult result = mClient.createChatRoom(payload);
      LOG.info("Got result, room id:" + result.getChatroom_id());
      return result;
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
   * 获取聊天室详情
   * @param appType 应用类型（zx=当家装修，gj=当家工匠）
   * @param roomIds (必填)聊天室ID数组
   */
  public ChatRoomListResult getBatchChatRoomInfo(String appType,long... roomIds) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ChatRoomListResult result = mClient.getBatchChatRoomInfo(roomIds);
      return result;
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
   * GET获取用户聊天室列表
   * @param appType 应用类型（zx=当家装修，gj=当家工匠）
   * @param username （必填）用户名
   * @return
   */
  public ChatRoomListResult getUserChatRoomInfo(String appType,String username) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ChatRoomListResult result = mClient.getUserChatRoomInfo(username);
      return result;
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
   * GET获取应用下聊天室列表
   * @param appType  应用类型（zx=当家装修，gj=当家工匠）
   * @param start 开始数
   * @param count 总数
   * @return
   */
  public ChatRoomListResult getAppChatRoomInfo(String appType,int start, int count) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ChatRoomListResult result = mClient.getAppChatRoomInfo(start, count);
      LOG.info("Got result " + result.getList().toString());
      return result;
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
   * 更新聊天室信息
   * @param appType  应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId 聊天室ID
   * @param ownerUsername 聊天室拥有者用户名
   * @param name 聊天室名称
   * @param desc 聊天室描述
   */
  public void updateChatRoomInfo(String appType,long roomId, String ownerUsername, String name, String desc) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ResponseWrapper result = mClient.updateChatRoomInfo(roomId, ownerUsername, name, desc);
      LOG.info("Got result " + result.toString());
    } catch (APIConnectionException e) {
      LOG.error("Connection error. Should retry later. ", e);
    } catch (APIRequestException e) {
      LOG.error("Error response from JPush server. Should review and fix it. ", e);
      LOG.info("HTTP Status: " + e.getStatus());
      LOG.info("Error Message: " + e.getMessage());
    }
  }

  /**
   * 删除聊天室
   * @param appType   应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId 聊天室ID
   */
  public void deleteChatRoom(String appType,long roomId) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ResponseWrapper result = mClient.deleteChatRoom(roomId);
      LOG.info("Got result " + result.toString());
    } catch (APIConnectionException e) {
      LOG.error("Connection error. Should retry later. ", e);
    } catch (APIRequestException e) {
      LOG.error("Error response from JPush server. Should review and fix it. ", e);
      LOG.info("HTTP Status: " + e.getStatus());
      LOG.info("Error Message: " + e.getMessage());
    }
  }


  /**
   * 修改用户禁言状态
   * @param appType 应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId  聊天室ID
   * @param username 用户名
   * @param flag 禁言状态:0表示不禁言1表示开启禁言必填
   */
  public void updateUserSpeakStatus(String appType,long roomId, String username, int flag) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ResponseWrapper result = mClient.updateUserSpeakStatus(roomId, username, flag);
      LOG.info("Got result " + result.toString());
    } catch (APIConnectionException e) {
      LOG.error("Connection error. Should retry later. ", e);
    } catch (APIRequestException e) {
      LOG.error("Error response from JPush server. Should review and fix it. ", e);
      LOG.info("HTTP Status: " + e.getStatus());
      LOG.info("Error Message: " + e.getMessage());
    }
  }

  /**
   * 获取聊天室成员列表
   * @param appType  应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId 聊天室ID。
   * @param start 开始数
   * @param count 总数
   * @return
   */
  public ChatRoomMemberList getChatRoomMembers(String appType,long roomId, int start, int count) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ChatRoomMemberList result = mClient.getChatRoomMembers(roomId, start, count);
      return result;
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
   * 添加聊天室成员
   * @param appType 应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId 聊天室ID。
   * @param members 需要添加的成员组 数组最多支持3000个
   */
  public void addChatRoomMember(String appType,long roomId, String[] members) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ResponseWrapper result = mClient.addChatRoomMember(roomId, members);
      LOG.info("Got result: " + result);
    } catch (APIConnectionException e) {
      LOG.error("Connection error. Should retry later. ", e);
    } catch (APIRequestException e) {
      LOG.error("Error response from JPush server. Should review and fix it. ", e);
      LOG.info("HTTP Status: " + e.getStatus());
      LOG.info("Error Message: " + e.getMessage());
    }
  }

  /**
   * 移除聊天室成员
   * @param appType  应用类型（zx=当家装修，gj=当家工匠）
   * @param roomId 聊天室ID。
   * @param members 需要删除的成员组 数组最多支持3000个
   */
  public void deleteChatRoomMember(String appType,long roomId, String[] members) {
    try {

      ChatRoomClient mClient = new ChatRoomClient(getAppkey(appType), getMasterSecret(appType));
      ResponseWrapper result = mClient.removeChatRoomMembers(roomId, members);
      LOG.info("Got result: " + result);
    } catch (APIConnectionException e) {
      LOG.error("Connection error. Should retry later. ", e);
    } catch (APIRequestException e) {
      LOG.error("Error response from JPush server. Should review and fix it. ", e);
      LOG.info("HTTP Status: " + e.getStatus());
      LOG.info("Error Message: " + e.getMessage());
    }
  }

}
