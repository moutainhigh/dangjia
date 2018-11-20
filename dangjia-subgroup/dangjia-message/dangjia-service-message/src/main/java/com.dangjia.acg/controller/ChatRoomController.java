package com.dangjia.acg.controller;

import cn.jmessage.api.chatroom.ChatRoomListResult;
import cn.jmessage.api.chatroom.ChatRoomMemberList;
import cn.jmessage.api.chatroom.CreateChatRoomResult;
import com.dangjia.acg.api.ChatRoomAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天室维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class ChatRoomController implements ChatRoomAPI {

  @Autowired
  private ChatRoomService chatRoomService;


  @Override
  @ApiMethod
  public CreateChatRoomResult createChatRoom(String appType,String name,String desc,String owner,String[] usernames) {
    return chatRoomService.createChatRoom( appType, name, desc, owner,usernames);
  }

  @Override
  @ApiMethod
  public ChatRoomListResult getBatchChatRoomInfo(String appType,long... roomIds) {
    return chatRoomService.getBatchChatRoomInfo( appType, roomIds);
  }
  @Override
  @ApiMethod
  public ChatRoomListResult getUserChatRoomInfo(String appType,String username) {
    return chatRoomService.getUserChatRoomInfo( appType, username);
  }
  @Override
  @ApiMethod
  public ChatRoomListResult getAppChatRoomInfo(String appType,int start, int count) {
    return chatRoomService.getAppChatRoomInfo( appType, start,count);
  }
  @Override
  @ApiMethod
  public void updateChatRoomInfo(String appType,long roomId, String ownerUsername, String name, String desc) {
     chatRoomService.updateChatRoomInfo( appType, roomId,ownerUsername,name,desc);
  }
  @Override
  @ApiMethod
  public void deleteChatRoom(String appType,long roomId) {
    chatRoomService.deleteChatRoom( appType, roomId);
  }
  @Override
  @ApiMethod
  public void updateUserSpeakStatus(String appType,long roomId, String username, int flag) {
    chatRoomService.updateUserSpeakStatus( appType, roomId,username,flag);
  }
  @Override
  @ApiMethod
  public ChatRoomMemberList getChatRoomMembers(String appType,long roomId, int start, int count) {
    return chatRoomService.getChatRoomMembers( appType, roomId,  start,  count);
  }
  @Override
  @ApiMethod
  public void addChatRoomMember(String appType,long roomId, String[] members) {
     chatRoomService.addChatRoomMember( appType, roomId,  members);
  }
  @Override
  @ApiMethod
  public void deleteChatRoomMember(String appType,long roomId, String[] members) {
    chatRoomService.deleteChatRoomMember( appType, roomId,  members);
  }

}
