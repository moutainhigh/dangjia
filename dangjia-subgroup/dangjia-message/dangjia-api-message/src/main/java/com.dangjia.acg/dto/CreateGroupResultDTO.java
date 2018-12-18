package com.dangjia.acg.dto;

import cn.jiguang.common.resp.BaseResult;
import lombok.Data;

@Data
public class CreateGroupResultDTO extends BaseResult {

    private Long gid;
    private String owner_username;
    private String name;
    private String desc;
    private String ctime;
    private String mtime;
    private String appkey;
    private String avatar;
    private Integer MaxMemberCount;

}
