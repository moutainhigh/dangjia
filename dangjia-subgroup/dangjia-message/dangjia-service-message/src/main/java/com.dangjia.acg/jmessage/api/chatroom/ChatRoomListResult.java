package com.dangjia.acg.jmessage.api.chatroom;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.chatroom.ChatRoomResult;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListResult extends BaseResult {

    @Expose private List<cn.jmessage.api.chatroom.ChatRoomResult> rooms = new ArrayList<cn.jmessage.api.chatroom.ChatRoomResult>();
    @Expose private Integer total;
    @Expose private cn.jmessage.api.chatroom.ChatRoomResult[] roomsArray;
    @Expose private Integer start;
    @Expose private Integer count;

    public static ChatRoomListResult fromResponse(ResponseWrapper responseWrapper) {
        ChatRoomListResult result = new ChatRoomListResult();
        if (responseWrapper.isServerResponse()) {
            result.roomsArray = _gson.fromJson(responseWrapper.responseContent, cn.jmessage.api.chatroom.ChatRoomResult[].class);
        }
        result.setResponseWrapper(responseWrapper);
        return result;
    }

    public cn.jmessage.api.chatroom.ChatRoomResult[] getRooms() {
        return this.roomsArray;
    }

    public List<ChatRoomResult> getList() {
        return this.rooms;
    }

    public Integer getTotal() {
        return this.total;
    }

    public Integer getStart() {
        return this.start;
    }

    public Integer getCount() {
        return this.count;
    }
}
