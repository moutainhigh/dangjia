package com.dangjia.acg.jmessage.api.reportv2;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.reportv2.UserStatResult;

public class UserStatListResult extends BaseResult {

    private cn.jmessage.api.reportv2.UserStatResult[] array;

    public static UserStatListResult fromResponse(ResponseWrapper responseWrapper) {
        UserStatListResult result = new UserStatListResult();
        if (responseWrapper.isServerResponse()) {
            result.array = _gson.fromJson(responseWrapper.responseContent, cn.jmessage.api.reportv2.UserStatResult[].class);
        }
        result.setResponseWrapper(responseWrapper);
        return result;
    }

    public UserStatResult[] getArray() {
        return array;
    }
}
