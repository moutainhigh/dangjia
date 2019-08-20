package com.dangjia.acg.util;


import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenUtil {

    private static ConcurrentHashMap<String, AccessToken> map = new ConcurrentHashMap<>();

    /**
     * 生成加密Token
     *
     * @param member
     * @return
     */
    public static AccessToken generateAccessToken(Member member, MainUser user) {
        AccessToken accessToken = new AccessToken();
        //用户id
        accessToken.setMemberId(member.getId());
        accessToken.setPhone(member.getMobile());
        if (user != null)
            accessToken.setUserId(user.getId());
        accessToken.setMemberType(0);
        //设置系统token
        accessToken.setUserToken(getRandom());
        // 设置时间戳
        accessToken.setTimestamp(getTimeStamp());
        member.setPassword(null);
        accessToken.setMember(member);
        //设置工种类型名称
//			accessToken.setUserTypeName(userTypeName);
        return accessToken;
    }


    /**
     * 设置token
     *
     * @param username
     * @param accessToken
     */
    public static void putToken(String username, AccessToken accessToken) {
        map.put(username, accessToken);
    }


    /**
     * 判定是否已经登录
     *
     * @param signature
     * @return
     */
    public static boolean hasLogin(String signature) {
        return map.containsKey(signature);
    }

    /**
     * 清空token
     *
     * @param signature
     * @return
     */
    public static void removeToken(String signature) {
        map.remove(signature);
    }

    /**
     * 获取accesstoken对象
     *
     * @param signature
     * @return
     */
    public static AccessToken getAccessToken(String signature) {
        return map.get(signature);


    }

    /**
     * 生成时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        return Calendar.getInstance().getTimeInMillis() + "";
    }


    /**
     * 生成随机数
     *
     * @return
     */
    public static String getRandom() {
        return UUID.randomUUID().toString();
    }

    /**
     * 验证token的失效性
     *
     * @param timestampStr 时间戳
     * @return
     * @throws Exception
     */
    public static boolean verifyAccessToken(String timestampStr)
            throws Exception {
        // 验证是否存在此用户登录的Token
        if (timestampStr != null) {
            // 判定时间戳是否过期
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long timestamp = Long.valueOf(timestampStr);
            // Token有效时间为60分钟
            long verifyTime = 360 * 60 * 60 * 1000;
            if (currentTime - timestamp > verifyTime) {
                return true;
            }
            return false;
        }
        return true;
    }
}
