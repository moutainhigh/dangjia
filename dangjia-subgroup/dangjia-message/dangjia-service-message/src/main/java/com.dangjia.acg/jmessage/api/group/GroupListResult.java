package com.dangjia.acg.jmessage.api.group;

import cn.jiguang.common.resp.BaseResult;
import cn.jmessage.api.group.GroupInfoResult;
import com.google.gson.annotations.Expose;

import java.util.List;

public class GroupListResult extends BaseResult {

    @Expose Integer total;
    @Expose Integer start;
    @Expose Integer count;
    @Expose List<cn.jmessage.api.group.GroupInfoResult> groups;

    public Integer getTotal() {
        return total;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getCount() {
        return count;
    }

    public List<GroupInfoResult> getGroups() {
        return groups;
    }
}
