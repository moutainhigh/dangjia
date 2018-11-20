package com.dangjia.acg.jmessage.api.group;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.group.MemberResult;

public class MemberListResult extends BaseResult {

    private cn.jmessage.api.group.MemberResult[] members;

    public static MemberListResult fromResponse(ResponseWrapper responseWrapper) {
        MemberListResult  result = new MemberListResult();
        if (responseWrapper.isServerResponse()) {
            result.members = _gson.fromJson(responseWrapper.responseContent, cn.jmessage.api.group.MemberResult[].class);
        } else {
            // nothing
        }
        result.setResponseWrapper(responseWrapper);
        return result;
    }

    public MemberResult[] getMembers() {
        return members;
    }
}
