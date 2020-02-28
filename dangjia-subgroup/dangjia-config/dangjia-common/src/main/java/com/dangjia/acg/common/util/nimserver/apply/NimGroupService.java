package com.dangjia.acg.common.util.nimserver.apply;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import com.dangjia.acg.common.util.nimserver.dto.NimGroup;
import com.dangjia.acg.common.util.nimserver.dto.NimUserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2020/02/24
 */
public class NimGroupService {

    private static Logger LOG = LoggerFactory.getLogger(NimGroupService.class);

    /**
     * 创建群组
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param ownerUsername（必填）群主用户名
     * @param name（必填）群组名字
     * 支持的字符：全部，包括表情符号。
     * @param membersUsername 成员用户名
     * @param avatar（选填）群组头像，上传接口所获得的media_id
     * @param desc（选填）群描述
     *              支持的字符：全部，包括表情符号。
     */
    public static String createGroup(String appType, String ownerUsername, String name, String[] membersUsername, String avatar, String desc) {

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tname", name));
            params.add(new BasicNameValuePair("owner", ownerUsername));
            params.add(new BasicNameValuePair("members", Arrays.toString(membersUsername)));
            params.add(new BasicNameValuePair("intro", desc));
            params.add(new BasicNameValuePair("icon", avatar));
            params.add(new BasicNameValuePair("joinmode", "0"));
            params.add(new BasicNameValuePair("beinvitemode", "1"));
            params.add(new BasicNameValuePair("invitemode", "1"));
            params.add(new BasicNameValuePair("msg", name+"邀请您入群"));
            //UTF-8编码,解决中文问题
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            String res = NIMPost.postNIMServer(NIMPost.TEAM_CREATE, entity, NIMPost.APPKEY, NIMPost.SECRET);
            JSONObject json =JSON.parseObject(res);
            return json.getString("tid");
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     *  更新群组成员
     *      批量增加与删除某gid群组的成员。
     *      群组成员将收到增加与删除成员的通知。
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param groupId gid群组ID
     * @param ownerUsername 群主用户帐号
     * @param addList add json数组表示要添加到群组的用户（任选）
     * @param removeList remove json数组表示要从群组删除的用户（任选）
     *        addList和removeList  两者至少要有一个
     */
    public static void manageGroup(String appType,String groupId, String ownerUsername,String[] addList,String[] removeList) {

        try {
            if(addList!=null&&addList.length>0) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tid", groupId));
                params.add(new BasicNameValuePair("owner", ownerUsername));
                params.add(new BasicNameValuePair("members", Arrays.toString(addList)));
                params.add(new BasicNameValuePair("magree", "0"));
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                NIMPost.postNIMServer(NIMPost.TEAM_ADD, entity, NIMPost.APPKEY, NIMPost.SECRET);
            }
            if(removeList!=null&&removeList.length>0) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tid", groupId));
                params.add(new BasicNameValuePair("owner", ownerUsername));
                params.add(new BasicNameValuePair("members", Arrays.toString(removeList)));
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                NIMPost.postNIMServer(NIMPost.TEAM_KICK, entity, NIMPost.APPKEY, NIMPost.SECRET);
            }
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  查询指定群的详细信息（群信息+成员详细信息）
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param gid 群组ID
     * @return
     */
    public static List<NimUserInfo>  getGroupInfoMembers(String appType, String gid) {
        try {
            List<NimUserInfo> resultMap=new ArrayList<>();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tid", gid));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
           String res= NIMPost.postNIMServer(NIMPost.TEAM_DETAIL, entity, NIMPost.APPKEY, NIMPost.SECRET);
            JSONObject json =JSON.parseObject(res);
            if(json!=null) {
                StringBuffer accids=new StringBuffer();
                JSONObject jsonms =json.getJSONObject("tinfo");
                JSONObject owner =jsonms.getJSONObject("owner");
                if(owner!=null) {
                    accids.append(owner.getString("accid"));
                }

                JSONArray members = jsonms.getJSONArray("members");
                if(members!=null&&members.size()>0) {
                    for (Object member : members) {
                        JSONObject m = (JSONObject) member;
                        accids.append(","+m.getString("accid"));
                    }
                }


                if(!CommonUtil.isEmpty(accids)) {
                    resultMap = NimUserService.getUserInfo(appType, accids.toString());
                }
            }
            return resultMap;
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
}

