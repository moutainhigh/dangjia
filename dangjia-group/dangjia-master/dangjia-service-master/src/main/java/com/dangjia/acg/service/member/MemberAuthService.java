package com.dangjia.acg.service.member;


import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.net.MininProgramUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.member.IMemberAuthMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAuth;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当家用户第三方认证
 */
@Service
public class MemberAuthService {
    @Autowired
    private IMemberAuthMapper memberAuthMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private RedisClient redisClient;

    /**
     * 当家用户第三方认证登录
     *
     * @param openType 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param unionid  第三方认证ID
     * @param userRole app应用角色  1为业主角色，2为工匠角色
     * @return
     */
    public ServerResponse authLogin(Integer openType, String unionid, Integer userRole) {
        if (userRole == null || userRole == 0
                || openType == null || openType == 0
                || unionid == null) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        Example example = new Example(MemberAuth.class);
        example.createCriteria()
                .andEqualTo(MemberAuth.OPEN_TYPE, openType)
                .andEqualTo(MemberAuth.UNIONID, unionid)
                .andEqualTo(MemberAuth.DATA_STATUS, 0);
        List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
        if (memberAuthList == null || memberAuthList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                    , "没有绑定账号");
        }
        MemberAuth memberAuth = memberAuthList.get(0);
        Member user = memberMapper.selectByPrimaryKey(memberAuth.getMemberId());
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                    , "没有绑定账号");
        } else {
            return memberService.getUser(user, userRole);
        }
    }

    /**
     * 第三方认证登录绑定当家老用户
     *
     * @param phone      手机号
     * @param password   密码
     * @param memberAuth 当家用户第三方认证表
     * @return
     */
    public ServerResponse oldUserBinding(String phone, String password, MemberAuth memberAuth) {
        if (memberAuth.getUserRole() == null || memberAuth.getUserRole() == 0
                || memberAuth.getOpenType() == null || memberAuth.getOpenType() == 0
                || memberAuth.getUnionid() == null) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        //指定角色查询用户
        Member user = new Member();
        user.setMobile(phone);
        user.setPassword(DigestUtils.md5Hex(password));
        user = memberMapper.getUser(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("电话号码或者密码错误");
        } else {
            Example example = new Example(MemberAuth.class);
            example.createCriteria()
                    .andEqualTo(MemberAuth.OPEN_TYPE, memberAuth.getOpenType())
                    .andEqualTo(MemberAuth.MEMBER_ID, user.getId())
                    .andEqualTo(MemberAuth.DATA_STATUS, 0);
            List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
            if (memberAuthList != null && memberAuthList.size() > 0) {
                return ServerResponse.createByErrorMessage("此账号已绑定过,请勿重复绑定");
            }
            memberAuth.setMemberId(user.getId());
            memberAuthMapper.insertSelective(memberAuth);
            return memberService.getUser(user, memberAuth.getUserRole());
        }
    }

    /**
     * 第三方认证登录绑定当家新用户
     *
     * @param phone          手机号
     * @param password       密码
     * @param smscode        验证码Code
     * @param invitationCode 邀请码
     * @param memberAuth     当家用户第三方认证表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse newUserBinding(HttpServletRequest request, String phone, String password,
                                         int smscode, String invitationCode,
                                         MemberAuth memberAuth, String longitude, String latitude) {
        if (memberAuth.getUserRole() == null || memberAuth.getUserRole() == 0
                || memberAuth.getOpenType() == null || memberAuth.getOpenType() == 0
                || memberAuth.getUnionid() == null) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        ServerResponse response = memberService.checkRegister(request, phone, smscode, password, invitationCode, memberAuth.getUserRole(), longitude, latitude);
        if (!response.isSuccess()) {
            return response;
        }
        return oldUserBinding(phone, password, memberAuth);
    }

    /**
     * 当家用户绑定第三方认证
     *
     * @param userToken  userToken
     * @param memberAuth 当家用户第三方认证表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse bindingThirdParties(String userToken, MemberAuth memberAuth) {
        if (memberAuth.getUserRole() == null || memberAuth.getUserRole() == 0
                || memberAuth.getOpenType() == null || memberAuth.getOpenType() == 0
                || memberAuth.getUnionid() == null) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        Example example = new Example(MemberAuth.class);
        example.createCriteria()
                .andEqualTo(MemberAuth.OPEN_TYPE, memberAuth.getOpenType())
                .andEqualTo(MemberAuth.UNIONID, memberAuth.getUnionid())
                .andEqualTo(MemberAuth.DATA_STATUS, 0);
        List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
        if (memberAuthList != null && memberAuthList.size() > 0) {
            return ServerResponse.createByErrorMessage("此账户已绑定过其他账号,请勿重复绑定");
        }
        memberAuth.setMemberId(user.getId());
        memberAuthMapper.insertSelective(memberAuth);
        return ServerResponse.createBySuccessMessage("绑定成功");
    }

    /**
     * 当家用户取消绑定第三方认证
     *
     * @param userToken userToken
     * @param openType  认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param userRole  app应用角色  1为业主角色，2为工匠角色
     * @return
     */
    public ServerResponse cancelBindingThirdParties(String userToken, Integer openType, String password, Integer userRole) {
        if (userRole == null || userRole == 0
                || openType == null || openType == 0) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        if (CommonUtil.isEmpty(password)) {
            return ServerResponse.createByErrorMessage("请输入密码");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        user = memberMapper.selectByPrimaryKey(user.getId());
        if (!user.getPassword().equals(DigestUtils.md5Hex(password))) {
            return ServerResponse.createByErrorMessage("密码不正确");
        }
        Example example = new Example(MemberAuth.class);
        example.createCriteria()
                .andEqualTo(MemberAuth.OPEN_TYPE, openType)
                .andEqualTo(MemberAuth.MEMBER_ID, user.getId())
                .andEqualTo(MemberAuth.DATA_STATUS, 0);
        List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
        if (memberAuthList == null || memberAuthList.size() <= 0) {
            return ServerResponse.createByErrorMessage("此账号未绑定，无法取消绑定");
        }
        MemberAuth memberAuth = memberAuthList.get(0);
        memberAuth.setDataStatus(1);
        memberAuthMapper.updateByPrimaryKeySelective(memberAuth);
        return ServerResponse.createBySuccessMessage("取消绑定成功");
    }


    /**
     * 当家用户判断是否绑定第三方认证
     *
     * @param userToken userToken
     * @param openType  认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param userRole  app应用角色  1为业主角色，2为工匠角色
     * @return
     */
    public ServerResponse isBindingThirdParties(String userToken, Integer openType, Integer userRole) {
        if (userRole == null || userRole == 0
                || openType == null || openType == 0) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        Example example = new Example(MemberAuth.class);
        example.createCriteria()
                .andEqualTo(MemberAuth.OPEN_TYPE, openType)
                .andEqualTo(MemberAuth.MEMBER_ID, user.getId())
                .andEqualTo(MemberAuth.DATA_STATUS, 0);
        List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
        if (memberAuthList == null || memberAuthList.size() <= 0) {
            return ServerResponse.createBySuccess("查询成功", 0);
        }
        return ServerResponse.createBySuccess("查询成功", 1);
    }

    /**
     * 微信小程序登录
     *
     * @param code 登录时获取的 code
     * @return 登录信息
     */
    public ServerResponse miniProgramLogin(String code) {
        Object jscode2session = jscode2session(code);
        if (jscode2session instanceof ServerResponse) {
            return (ServerResponse) jscode2session;
        }
        JSONObject object = (JSONObject) jscode2session;
        String openid = object.getString("openid");
        String sessionKey = object.getString("session_key");
        String unionid = object.getString("unionid");
        ServerResponse serverResponse = authLogin(1, unionid, 1);
        if (serverResponse.isSuccess()) {
            Map map = BeanUtils.beanToMap(serverResponse.getResultObj());
            map.put("openid", openid);
            map.put("sessionKey", sessionKey);
            map.put("unionid", unionid);
            map.put("loginType", 0);
            return ServerResponse.createBySuccess("登录成功，正在跳转", map);
        } else {
            if (serverResponse.getResultCode() == ServerCode.NO_DATA.getCode()) {
                Map map = new HashMap();
                map.put("openid", openid);
                map.put("sessionKey", sessionKey);
                map.put("unionid", unionid);
                map.put("loginType", 1);
                return ServerResponse.createBySuccess("登录失败", map);
            } else {
                return serverResponse;
            }
        }
    }

    /**
     * 小程序获取手机号
     */
    public ServerResponse decodeWxAppPhone(HttpServletRequest request, String encrypted, String iv, String sessionKey) {
        request.setAttribute("isShow", "true");
        String phone = MininProgramUtil.getPhone(encrypted, iv, sessionKey);
        if (phone == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "未找到手机号");
        } else {
            return ServerResponse.createBySuccess("获取成功", phone);
        }
    }

    /**
     * 小程序通过Code注册登录账号
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse miniProgramCodeRegister(HttpServletRequest request, String encrypted, String iv,
                                                  String sessionKey, String openid, String unionid,
                                                  String name, String iconurl) {
        String phone = MininProgramUtil.getPhone(encrypted, iv, sessionKey);
        if (phone == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "未找到手机号");
        } else {
            return miniProgramRegister(request, phone, openid, unionid, name, iconurl);
        }
    }

    /**
     * 小程序通过手机号注册登录账号
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse miniProgramPhoneRegister(HttpServletRequest request, String phone, String smscode, String openid, String unionid,
                                                   String name, String iconurl) {
        Integer registerCode = redisClient.getCache(Constants.SMS_CODE + phone, Integer.class);
        Integer smscodeInt;
        try {
            smscodeInt = Integer.valueOf(smscode);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("验证码错误");
        }
        if (!smscodeInt.equals(registerCode)) {
            return ServerResponse.createByErrorMessage("验证码错误");
        } else {
            return miniProgramRegister(request, phone, openid, unionid, name, iconurl);
        }
    }

    private ServerResponse miniProgramRegister(HttpServletRequest request, String phone, String openid, String unionid, String name, String iconurl) {
        if (CommonUtil.isEmpty(phone) || phone.length() != 11) {
            return ServerResponse.createByErrorMessage("手机号不正确");
        }
        Member member = memberMapper.getByPhone(phone);
        if (member == null) {
            ServerResponse response = memberService.register(request, phone, null,
                    null, 1, null, null);
            if (!response.isSuccess()) {
                return response;
            }
            member = memberMapper.getByPhone(phone);
        }
        Example example = new Example(MemberAuth.class);
        example.createCriteria()
                .andEqualTo(MemberAuth.OPEN_TYPE, 1)
                .andEqualTo(MemberAuth.MEMBER_ID, member.getId())
                .andEqualTo(MemberAuth.DATA_STATUS, 0);
        List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
        if (memberAuthList != null && memberAuthList.size() > 0) {
            return ServerResponse.createByErrorMessage("此账号已绑定过,请勿重复绑定");
        }
        MemberAuth memberAuth = new MemberAuth();
        memberAuth.setIconurl(iconurl);
        memberAuth.setOpenType(1);
        memberAuth.setOpenid(openid);
        memberAuth.setAccessToken("");
        memberAuth.setUnionid(unionid);
        memberAuth.setName(name);
        memberAuth.setUserRole(1);
        memberAuth.setMemberId(member.getId());
        memberAuthMapper.insertSelective(memberAuth);
        return memberService.getUser(member, 1);
    }

    private Object jscode2session(String code) {
        try {
            JSONObject object = MininProgramUtil.jscode2session(code);
            Integer errcode = object.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                switch (errcode) {
                    case 45011:
                        return ServerResponse.createByErrorMessage("频率限制，每个用户每分钟100次");
                    case -1:
                        return ServerResponse.createByErrorMessage("系统繁忙,请稍候再试");
                    default:
                        return ServerResponse.createByErrorMessage("code 无效");
                }
            } else {
                return object;
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统繁忙,请稍候再试");
        }
    }
}
