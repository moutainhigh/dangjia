package com.dangjia.acg.common.util.nimserver.apply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import com.dangjia.acg.common.util.nimserver.dto.NimUserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2020/02/24
 */
public class NimUserService {

    private static Logger LOG = LoggerFactory.getLogger(NimUserService.class);

    /**
     *  用户注册
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param userInfoJsons
     * accid:	String	是	用户帐号，最大长度32字符，必须保证一个APP内唯一
     * name:	String	否	用户昵称，最大长度64字符，可设置为空字符串
     * icon:	String	否	用户头像，最大长度1024字节，可设置为空字符串
     * sign:	String	否	用户签名，最大长度256字符，可设置为空字符串
     * email:	String	否	用户email，最大长度64字符，可设置为空字符串
     * birth:	String	否	用户生日，最大长度16字符，可设置为空字符串
     * mobile:	String	否	用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码(如美国：+1-xxxxxxxxxx)或地区代码(如香港：+852-xxxxxxxx)，可设置为空字符串
     * gender:	int	否	用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     * ex:	String	否	用户名片扩展字段，最大长度1024字符，用户可自行扩展，建议封装成JSON字符串，也可以设置为空字符串
     */
    public static   void registerUsers(String appType,String userInfoJsons) {
        try {
            List resList=new ArrayList();
            JSONArray userInfoJson= JSON.parseArray(userInfoJsons);
            for (Object o : userInfoJson) {
                JSONObject jsonObject=(JSONObject)o;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("accid", jsonObject.getString("accid")));
                params.add(new BasicNameValuePair("name", jsonObject.getString("name")));
                params.add(new BasicNameValuePair("token", jsonObject.getString("token")));
                params.add(new BasicNameValuePair("icon",jsonObject.getString("head")));
                //UTF-8编码,解决中文问题
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

                String res = NIMPost.postNIMServer(NIMPost.CREATE, entity, NIMPost.APPKEY, NIMPost.SECRET);
                resList.add(res);
            }
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
        }
    }


    /**
     * 获取用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @return
     */
    public static  List<NimUserInfo> getUserInfo(String appType, String username) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONArray json = new JSONArray();
            json.addAll(Arrays.asList(username.split(",")));
            params.add(new BasicNameValuePair("accids", JSON.toJSONString(json)));
            //UTF-8编码,解决中文问题
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            String res = NIMPost.postNIMServer(NIMPost.GETUINFOS, entity, NIMPost.APPKEY, NIMPost.SECRET);
            JSONArray userInfoJson= JSON.parseArray(res);
            List<NimUserInfo> resultDTO=userInfoJson.toJavaList(NimUserInfo.class);
            return resultDTO;
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }


    /**
     *  修改用户密码
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param password 新密码
     */
    public static  void updatePassword(String appType,String username, String password) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("accids", username));
            params.add(new BasicNameValuePair("token", password));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            String res = NIMPost.postNIMServer(NIMPost.UPDATE, entity, NIMPost.APPKEY, NIMPost.SECRET);
            LOG.info("OK Message: " + res);
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param infoResultDTO
     *    String	accid	;//	用户帐号，最大长度32字符，必须保证一个APP内唯一
     *      String	name	;//	用户昵称，最大长度64字符，可设置为空字符串
     *      String	icon	;//	用户头像，最大长度1024字节，可设置为空字符串
     *      String	sign	;//	用户签名，最大长度256字符，可设置为空字符串
     *      String	email	;//	用户email，最大长度64字符，可设置为空字符串
     *      String	birth	;//	用户生日，最大长度16字符，可设置为空字符串
     *      String	mobile	;//	用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码(如美国：+1-xxxxxxxxxx)或地区代码(如香港：+852-xxxxxxxx)，可设置为空字符串
     *      Integer	gender	;//	用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     *      String	ex	;//	用户名片扩展字段，最大长度1024字符，用户可自行扩展，建议封装成JSON字符串，也可以设置为空字符串
     *
     */
    public static  void updateUserInfo(String appType, NimUserInfo infoResultDTO) {
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(NimUserInfo.ACCID, infoResultDTO.getAccid()));
            params.add(new BasicNameValuePair(NimUserInfo.NAME, infoResultDTO.getName()));
            params.add(new BasicNameValuePair(NimUserInfo.BIRTH, infoResultDTO.getBirth()));
            params.add(new BasicNameValuePair(NimUserInfo.EMAIL, infoResultDTO.getEmail()));
            params.add(new BasicNameValuePair(NimUserInfo.EX, infoResultDTO.getEx()));
            params.add(new BasicNameValuePair(NimUserInfo.GENDER, infoResultDTO.getGender()!=null?String.valueOf(infoResultDTO.getGender()):"0"));
            params.add(new BasicNameValuePair(NimUserInfo.ICON, infoResultDTO.getIcon()));
            params.add(new BasicNameValuePair(NimUserInfo.MOBILE, infoResultDTO.getMobile()));
            params.add(new BasicNameValuePair(NimUserInfo.SIGN, infoResultDTO.getSign()));

            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            String res = NIMPost.postNIMServer(NIMPost.UPDATEUINFO, entity, NIMPost.APPKEY, NIMPost.SECRET);
            LOG.info("OK Message: " + res);
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
        }
    }


}

