package com.dangjia.acg.service.member;


import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.member.IMemberAuthMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAuth;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public ServerResponse newUserBinding(HttpServletRequest request, String phone, String password,
                                         int smscode, String invitationCode,
                                         MemberAuth memberAuth) {
        if (memberAuth.getUserRole() == null || memberAuth.getUserRole() == 0
                || memberAuth.getOpenType() == null || memberAuth.getOpenType() == 0
                || memberAuth.getUnionid() == null) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        ServerResponse response = memberService.checkRegister(request, phone, smscode, password, invitationCode, memberAuth.getUserRole());
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
}
