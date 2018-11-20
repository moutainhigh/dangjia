package com.dangjia.acg.jmessage.api.message;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.message.MessageResult;
import com.google.gson.annotations.Expose;

public class MessageListResult extends BaseResult {

    @Expose Integer total;
    @Expose String cursor;
    @Expose Integer count;
    @Expose cn.jmessage.api.message.MessageResult[] messages;

    public static MessageListResult fromResponse(ResponseWrapper responseWrapper) {
        MessageListResult result = new MessageListResult();
        if (responseWrapper.isServerResponse()) {
            result.messages = _gson.fromJson(responseWrapper.responseContent, cn.jmessage.api.message.MessageResult[].class);
        }
        result.setResponseWrapper(responseWrapper);
        return result;
    }


    public Integer getTotal() {
        return total;
    }

    public String getCursor() {
        return cursor;
    }

    public Integer getCount() {
        return count;
    }

    public MessageResult[] getMessages() {
        return messages;
    }

}
