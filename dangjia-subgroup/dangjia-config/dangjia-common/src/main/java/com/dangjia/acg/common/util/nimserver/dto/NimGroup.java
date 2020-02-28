package com.dangjia.acg.common.util.nimserver.dto;

import cn.jiguang.common.resp.BaseResult;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(prefix = "")
public class NimGroup  {

    private String	tname	;//		群名称，最大长度64字符
    private	 String	owner	;//		群主用户帐号，最大长度32字符
    private	 String	members	;//		邀请的群成员列表。["aaa","bbb"](JSONArray对应的accid，如果解析出错会报414)，members与owner总和上限为200。members中无需再加owner自己的账号。
    private	 String	announcement	;//		群公告，最大长度1024字符
    private	 String	intro	;//		群描述，最大长度512字符
    private	 String	msg	;//		邀请发送的文字，最大长度150字符
    private	 Integer magree	;//		管理后台建群时，0不需要被邀请人同意加入群，1需要被邀请人同意才可以加入群。其它会返回414
    private	 Integer joinmode	;//		群建好后，sdk操作时，0不用验证，1需要验证,2不允许任何人加入。其它返回414
    private	 String	custom	;//		自定义高级群扩展属性，第三方可以跟据此属性自定义扩展自己的群属性。（建议为json）,最大长度1024字符
    private	 String	icon;//				群头像，最大长度1024字符
    private	 Integer beinvitemode;//				被邀请人同意方式，0-需要同意(默认),1-不需要同意。其它返回414
    private	 Integer invitemode;//				谁可以邀请他人入群，0-管理员(默认),1-所有人。其它返回414
    private	 Integer uptinfomode;//				谁可以修改群资料，0-管理员(默认),1-所有人。其它返回414
    private	 Integer upcustommode;//				谁可以更新群自定义属性，0-管理员(默认),1-所有人。其它返回414
    private	 Integer teamMemberLimit	;//			该群最大人数(包含群主)，范围：2至应用定义的最大群人数(默认:200)。其它返回414


}