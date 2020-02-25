package com.dangjia.acg.common.util.nimserver.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(prefix = "")
public class NimUserInfo {

    private String	accid	;//	用户帐号，最大长度32字符，必须保证一个APP内唯一
    private String	name	;//	用户昵称，最大长度64字符，可设置为空字符串
    private String	icon	;//	用户头像，最大长度1024字节，可设置为空字符串
    private String	sign	;//	用户签名，最大长度256字符，可设置为空字符串
    private String	email	;//	用户email，最大长度64字符，可设置为空字符串
    private String	birth	;//	用户生日，最大长度16字符，可设置为空字符串
    private String	mobile	;//	用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码(如美国：+1-xxxxxxxxxx)或地区代码(如香港：+852-xxxxxxxx)，可设置为空字符串
    private Integer gender	;//	用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
    private String	ex	;//	用户名片扩展字段，最大长度1024字符，用户可自行扩展，建议封装成JSON字符串，也可以设置为空字符串

}
