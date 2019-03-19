package com.dangjia.acg.service.member;


import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.member.IMemberAuthsMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.member.MemberAuths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 当家用户第三方认证
 */
@Service
public class MemberAuthsService {
    @Autowired
    private IMemberAuthsMapper memberAuthsMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MemberService memberService;

    /**
     * 当家用户第三方认证登录
     *
     * @param openType 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param openid   第三方认证ID
     * @param userRole app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return
     */
    public ServerResponse authsLogin(Integer openType, String openid, Integer userRole) {
        return null;
    }

    /**
     * 第三方认证登录绑定当家老用户
     *
     * @param phone       手机号
     * @param password    密码
     * @param memberAuths 当家用户第三方认证表
     * @param userRole    app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return
     */
    public ServerResponse oldUserBinding(String phone, String password, MemberAuths memberAuths, Integer userRole) {
        return null;
    }

    /**
     * 第三方认证登录绑定当家新用户
     *
     * @param phone          手机号
     * @param password       密码
     * @param smscode        验证码Code
     * @param invitationCode 邀请码
     * @param memberAuths    当家用户第三方认证表
     * @param userRole       app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return
     */
    public ServerResponse newUserBinding(String phone, String password, int smscode, String invitationCode, MemberAuths memberAuths, Integer userRole) {
        return null;
    }

    /**
     * 当家用户绑定第三方认证
     *
     * @param userToken   userToken
     * @param memberAuths 当家用户第三方认证表
     * @return
     */
    public ServerResponse bindingThirdParties(String userToken, MemberAuths memberAuths) {
        return null;
    }

    /**
     * 当家用户取消绑定第三方认证
     *
     * @param userToken userToken
     * @param openType  认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param openid    第三方认证ID
     * @return
     */
    public ServerResponse cancelBindingThirdParties(String userToken, Integer openType, String openid) {
        return null;
    }
}
