package com.dangjia.acg.jmessage.api.user;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.user.UserInfoResult;
import com.google.gson.annotations.Expose;

public class UserListResult extends BaseResult {

    @Expose Integer total;
    @Expose Integer start;
    @Expose Integer count;
    @Expose cn.jmessage.api.user.UserInfoResult[] users;

    public static UserListResult fromResponse(ResponseWrapper responseWrapper) {
        UserListResult result = new UserListResult();
        if (responseWrapper.isServerResponse()) {
            result.users = _gson.fromJson(responseWrapper.responseContent, cn.jmessage.api.user.UserInfoResult[].class);
        }
        result.setResponseWrapper(responseWrapper);
        return result;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getCount() {
        return count;
    }

    public UserInfoResult[] getUsers() {
        return users;
    }
}
