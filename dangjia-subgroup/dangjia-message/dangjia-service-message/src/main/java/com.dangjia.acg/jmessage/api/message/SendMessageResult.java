package com.dangjia.acg.jmessage.api.message;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class SendMessageResult extends BaseResult{

    @Expose Long msg_id;
    @Expose Long msg_ctime;

    public Long getMsg_id() {
        return msg_id;
    }

    public Long getMsgCtime() {
        return msg_ctime;
    }
}
