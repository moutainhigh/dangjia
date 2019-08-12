package com.dangjia.acg.service.member;

import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HomePageBean;
import com.dangjia.acg.dto.member.MemberCustomerDTO;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.*;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.member.*;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.activity.RedPackPayService;
import com.dangjia.acg.service.clue.ClueService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.RKIDCardUtil;
import com.dangjia.acg.util.TokenUtil;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 */
@Service
public class MemberService {
    private Logger logger = LoggerFactory.getLogger(MemberService.class);
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private RedPackPayService redPackPayService;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IMemberCityMapper memberCityMapper;
    @Autowired
    private IMemberInfoMapper memberInfoMapper;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private ISmsMapper smsMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private CustomerRecordService customerRecordService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SupplierProductAPI supplierProductAPI;
    @Autowired
    private ClueService clueService;
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MessageAPI messageAPI;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;

    /**
     * 获取用户手机号
     *
     * @param id     来源ID
     * @param idType 1=房屋ID, 2=用户ID, 3=供应商ID, 4=系统用户, 5=验房分销
     */
    public ServerResponse getMemberMobile(HttpServletRequest request, String id, String idType) {
        String mobile = "";
        request.setAttribute("isShow", "true");
        String cityId = request.getParameter(Constants.CITY_ID);
        switch (idType) {
            case "1":
                House house = houseMapper.selectByPrimaryKey(id);
                if (house != null) {
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    mobile = member == null ? "" : member.getMobile();
                }
                break;
            case "3":
                Supplier supplier = supplierProductAPI.getSupplier(cityId, id);
                if (supplier != null) {
                    mobile = supplier.getTelephone();
                }
                break;
            case "5":
                HouseDistribution distribution = iHouseDistributionMapper.selectByPrimaryKey(id);
                if (distribution != null) {
                    mobile = distribution.getPhone();
                }
                break;
            default:
                Member member = memberMapper.selectByPrimaryKey(id);
                if (member != null) {
                    mobile = member.getMobile();
                } else {
                    MainUser mainUser = userMapper.selectByPrimaryKey(id);
                    if (mainUser != null) {
                        mobile = mainUser.getMobile();
                    }
                }
                break;
        }
        if (CommonUtil.isEmpty(mobile)) {
            mobile = "4001681231";
//			return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("OK", mobile);
    }

    /**
     * 获取用户详细资料
     */
    public ServerResponse getMemberInfo(String userToken, Integer userRole) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {//无效的token
            return ServerResponse.createbyUserTokenError();
        } else {
            Member user = memberMapper.selectByPrimaryKey(accessToken.getMemberId());
            if (user == null) {
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            if (userRole == 3) {//销售端放开登录权限
                if (!CommonUtil.isEmpty(accessToken.getUserId())) {
                    ServerResponse serverResponse = setSale(accessToken, accessToken.getUserId());
                    if (!serverResponse.isSuccess()) {
//                    return ServerResponse.createbyUserTokenError();
                        accessToken.setMemberType(6);
                    }
                }
            }
            updataMember(user, accessToken);
            return ServerResponse.createBySuccess("有效！", accessToken);
        }
    }

    // 登录 接口
    public ServerResponse login(String phone, String password, Integer userRole) {
        //指定角色查询用户
        Member user = new Member();
        user.setMobile(phone);
        user.setPassword(DigestUtils.md5Hex(password));
//		user.setUserRole(userRole);
        user = memberMapper.getUser(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("电话号码或者密码错误");
        } else {
            return getUser(user, userRole);
        }
    }

    ServerResponse getUser(Member user, Integer userRole) {
        if (userRole == 1) {
            Example example = new Example(Customer.class);
            example.createCriteria().andEqualTo(Customer.MEMBER_ID, user.getId());
            List<Customer> customers = iCustomerMapper.selectByExample(example);
            if (customers.size() <= 0) {
                clueService.sendUser(user, user.getMobile(), null, null);
            }
        }
        ServerResponse serverResponse = setAccessToken(user, userRole);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        updateOrInsertInfo(user.getId(), String.valueOf(userRole), user.getPassword());
        return ServerResponse.createBySuccess("登录成功，正在跳转", serverResponse.getResultObj());
    }

    private ServerResponse setSale(AccessToken accessToken, String userId) {
        if (accessToken != null)
            accessToken.setMemberType(-1);
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, userId)
                .andEqualTo(Store.DATA_STATUS, 0);
        int c = iStoreMapper.selectCountByExample(example);
        if (c > 0) {
            if (accessToken != null)
                accessToken.setMemberType(3);
        } else {
            example = new Example(StoreUser.class);
            example.createCriteria().andEqualTo(StoreUser.USER_ID, userId)
                    .andEqualTo(StoreUser.DATA_STATUS, 0);
            List<StoreUser> storeUsers = iStoreUserMapper.selectByExample(example);
            if (storeUsers.size() > 0) {
                StoreUser storeUser = storeUsers.get(0);
                if (storeUser.getType() == 0 || storeUser.getType() == 1) {
                    if (accessToken != null)
                        accessToken.setMemberType(storeUser.getType() == 0 ? 4 : 5);
                } else {
                    return ServerResponse.createByErrorMessage("当前用户暂无权限使用该终端，请联系管理员");
                }
            } else {
                return ServerResponse.createByErrorMessage("当前用户暂无权限使用该终端，请联系管理员");
            }
        }
        return ServerResponse.createBySuccess();
    }

    private ServerResponse setAccessToken(Member member, Integer userRole) {
        int memberType = -1;
        MainUser mainUser = userMapper.findUserByMobile(member.getMobile());
        if (mainUser != null && CommonUtil.isEmpty(mainUser.getMemberId())) {
            //插入MemberId
            userMapper.insertMemberId(member.getMobile());
        }
        if (userRole == 3) {//销售端放开登录权限
            if (mainUser == null) {
//                return ServerResponse.createByErrorMessage("当前用户暂无权限使用该终端，请联系管理员");
                memberType = 6;
            } else if (mainUser.getIsJob()) {
//                return ServerResponse.createByErrorMessage("当前用户已离职，请您联系管理员");
                memberType = 6;
            }
        }
        member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        AccessToken accessToken = TokenUtil.generateAccessToken(member, mainUser);
        if (userRole == 3) {//销售端放开登录权限
            if (memberType == -1) {
                ServerResponse serverResponse = setSale(accessToken, mainUser.getId());
                if (!serverResponse.isSuccess()) {
//                    return serverResponse;
                    memberType = 6;
                }
            }
            if (memberType != -1) {
                accessToken.setMemberType(memberType);
            }
        }
        if (!CommonUtil.isEmpty(member.getWorkerTypeId())) {
            WorkerType wt = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
            if (wt != null) {
                accessToken.setWorkerTypeName(wt.getName());
            }
        }
        redisClient.put(accessToken.getUserToken() + Constants.SESSIONUSERID, accessToken);
        String userRoleText = "role" + userRole + ":" + member.getId();
        String token = redisClient.getCache(userRoleText, String.class);
        //如果用户存在usertoken则清除原来的token数据
        if (!CommonUtil.isEmpty(token)) {
            redisClient.deleteCache(token + Constants.SESSIONUSERID);
        }
        redisClient.put(userRoleText, accessToken.getUserToken());
//        switch (userRole) {
//            case 1:
//                groupInfoService.registerJGUsers(AppType.ZHUANGXIU.getDesc(), new String[]{member.getId()}, new String[1]);
//                break;
//            case 2:
//                groupInfoService.registerJGUsers(AppType.GONGJIANG.getDesc(), new String[]{member.getId()}, new String[1]);
//                break;
//            case 3:
//                if (!CommonUtil.isEmpty(accessToken.getUserId()))
//                    groupInfoService.registerJGUsers(AppType.SALE.getDesc(), new String[]{accessToken.getUserId()}, new String[1]);
//                break;
//        }
        return ServerResponse.createBySuccess(accessToken);
    }

    /*
     * 接口注册获取验证码
     */
    public ServerResponse registerCode(String phone) {
        if (!Validator.isMobileNo(phone)) {
            return ServerResponse.createByErrorMessage("手机号不正确");
        }
        Member user = new Member();
        user.setMobile(phone);
        user = memberMapper.getUser(user);
        if (user != null) {
            return ServerResponse.createByErrorMessage("手机号已被注册");
        } else {
            Integer registerCode = redisClient.getCache(Constants.SMS_CODE + phone, Integer.class);
            if (registerCode == null || registerCode == 0) {
                registerCode = (int) (Math.random() * 9000 + 1000);
            }
            redisClient.put(Constants.SMS_CODE + phone, registerCode);
            JsmsUtil.SMS(registerCode, phone);
            //记录短信发送
            Sms sms = new Sms();
            sms.setCode(String.valueOf(registerCode));
            sms.setMobile(phone);
            smsMapper.insert(sms);
            return ServerResponse.createBySuccessMessage("验证码已发送");
        }
    }

    /*
     * 查询验证码
     */
    public ServerResponse getSmsCode(String phone) {
        if (!Validator.isMobileNo(phone)) {
            return ServerResponse.createBySuccessMessage("手机号不正确");
        }
        Example example = new Example(Sms.class);
        example.createCriteria().andEqualTo("mobile", phone);
        example.orderBy(Sms.CREATE_DATE).desc();
        List<Sms> sms = smsMapper.selectByExample(example);
        if (sms == null || sms.size() == 0) {
            return ServerResponse.createBySuccessMessage("无效验证码");
        }
        return ServerResponse.createBySuccess("验证码获取成功", sms.get(0).getCode());
    }

    /**
     * 校验验证码并保存密码
     */
    public ServerResponse checkRegister(HttpServletRequest request, String phone, int smscode, String
            password, String invitationCode, Integer userRole, String longitude, String latitude) {
        Integer registerCode = redisClient.getCache(Constants.SMS_CODE + phone, Integer.class);
        if (registerCode == null || smscode != registerCode) {
            return ServerResponse.createByErrorMessage("验证码错误");
        } else {
            if (CommonUtil.isEmpty(password) || password.length() < 6) {
                return ServerResponse.createByErrorMessage("密码不得小于六位数");
            }
            Member user = new Member();
            user.setMobile(phone);
            user.setPraiseRate(new BigDecimal(1));//好评率
            user.setEvaluationScore(new BigDecimal(60));//积分
            user.setCheckType(5);//未提交资料
            user.setWorkerPrice(new BigDecimal(0));
            user.setHaveMoney(new BigDecimal(0));
            user.setSurplusMoney(new BigDecimal(0));
            user.setRetentionMoney(new BigDecimal(0));
            user.setVisitState(0);
            user.setUserName(user.getMobile());
            user.setName("");
            user.setSmscode(0);
            user.setOthersInvitationCode(invitationCode);
            user.setInvitationCode(CommonUtil.randomString(6));
            user.setNickName("当家-" + CommonUtil.randomString(6));
            user.setInviteNum(0);
            user.setIsCrowned(0);
            user.setHead(Utils.getHead());
            updateOrInsertInfo(user.getId(), String.valueOf(userRole), user.getPassword());
            user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            ServerResponse serverResponse = setAccessToken(user, userRole);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            user.setPassword(DigestUtils.md5Hex(password));//验证码正确设置密码
            memberMapper.insertSelective(user);

            MemberCity userCity = new MemberCity();
            userCity.setMemberId(user.getId());
            userCity.setCityId(request.getParameter(Constants.CITY_ID));
            if (!CommonUtil.isEmpty(userCity.getCityId())) {
                City city = iCityMapper.selectByPrimaryKey(userCity.getCityId());
                userCity.setCityName(city.getName());
                memberCityMapper.insert(userCity);
            }
//            userRole", value = "app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
            if (userRole == 1) {
                clueService.sendUser(user, user.getMobile(), longitude, latitude);
            }
            redisClient.deleteCache(Constants.SMS_CODE + phone);
            if (userRole == 1) {
                try {
                    //检查是否有注册送优惠券活动，并给新注册的用户发放优惠券
                    redPackPayService.checkUpActivity(request, user.getMobile(), "1");
                    configMessageService.addConfigMessage(request, AppType.ZHUANGXIU, user.getId(), "0", "注册通知", "业主您好！等候多时啦，有任何装修问题，请联系我们，谢谢。", null);
                } catch (Exception e) {
                    logger.error("注册送优惠券活动异常-zhuce：原因：" + e.getMessage(), e);
                }
            }
            return ServerResponse.createBySuccess("注册成功", serverResponse.getResultObj());
        }
    }

    private void updateOrInsertInfo(String memberid, String policyId, String pwd) {
        try {
            //检测是否已有指定身份，无则初始化
            Example example = new Example(MemberInfo.class);
            example.createCriteria().andEqualTo(MemberInfo.MEMBER_ID, memberid).andEqualTo(MemberInfo.POLICY_ID, policyId);
            List<MemberInfo> infos = memberInfoMapper.selectByExample(example);
            if (!CommonUtil.isEmpty(memberid) && (infos == null || infos.size() == 0)) {
                MemberInfo memberInfo = new MemberInfo();
                memberInfo.setMemberId(memberid);
                memberInfo.setPolicyId(policyId);
                memberInfo.setPassword(pwd);
                memberInfo.setCheckStatus("0");
                memberInfoMapper.insertSelective(memberInfo);
            }
        } catch (Exception e) {
            logger.error("用户身份异常！", e);
        }
    }

    /**
     * 工匠提交详细资料
     */
    public ServerResponse updateWokerRegister(Member user, String userToken) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {//无效的token
            return ServerResponse.createbyUserTokenError();
        }
        user.setId(accessToken.getMember().getId());
        Member member = memberMapper.selectByPrimaryKey(user.getId());
        if (member.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
        }
        user.setCheckType(accessToken.getMember().getCheckType());//提交资料，审核中
        if (!CommonUtil.isEmpty(user.getIdnumber())) {
            String idCard = RKIDCardUtil.getIDCardValidate(user.getIdnumber());
            if (!"".equals(idCard)) {//验证身份证
                return ServerResponse.createByErrorMessage(idCard);
            }
        }
        if (user.getReferrals() != null && user.getReferrals().equals(user.getMobile())) {
            return ServerResponse.createByErrorMessage("自己不能作为推荐人");
        }
        if (user.getId() != null) {//存在注册信息
            WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
            if (wt != null) {
                user.setWorkerTypeId(wt.getId());
                user.setWorkerType(wt.getType());
            }
            user.setCreateDate(null);
            memberMapper.updateByPrimaryKeySelective(user);
            user = memberMapper.selectByPrimaryKey(user.getId());
            user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            MainUser mainUser = userMapper.findUserByMobile(user.getMobile());
            accessToken = TokenUtil.generateAccessToken(user, mainUser);
            accessToken.setUserToken(userToken);
            accessToken.setTimestamp(accessToken.getTimestamp());
            if (wt != null) {
                accessToken.setWorkerTypeName(wt.getName());
            }
            redisClient.put(accessToken.getUserToken() + Constants.SESSIONUSERID, accessToken);
            if (!CommonUtil.isEmpty(user.getReferrals())) {
                try {
                    //检查是否有推荐送优惠券活动，并给推荐的用户发放优惠券
                    redPackPayService.checkUpActivity(request, user.getReferrals(), "2");
                } catch (Exception e) {
                    logger.error("注册送优惠券活动异常 -tuijian：原因：" + e.getMessage(), e);
                }
            }
            return ServerResponse.createBySuccessMessage("提交资料成功！");
        } else {
            return ServerResponse.createByErrorMessage("不存在注册信息,请重新注册！");
        }
    }

    /**
     * 实名认证提交资料
     *
     * @param userToken token
     * @param name      真实姓名
     * @param idcaoda   身份证正面
     * @param idcaodb   身份证反面
     * @param idcaodall 半身照
     * @param idnumber  身份证号
     */
    public ServerResponse certification(String userToken, String name, String idcaoda, String idcaodb, String
            idcaodall, String idnumber) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        user = memberMapper.selectByPrimaryKey(user.getId());
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if (user.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
        }
        if (user.getRealNameState() == 1) {
            return ServerResponse.createByErrorMessage("您的资料正在审核中，请勿重复提交");
        }
        if (user.getRealNameState() == 3) {
            return ServerResponse.createByErrorMessage("您的资料已审核通过，请勿重复提交");
        }
        if (user.getRealNameState() == 0 &&
                (CommonUtil.isEmpty(name)
                        || (CommonUtil.isEmpty(user.getIdcaoda()) && CommonUtil.isEmpty(idcaoda))
                        || (CommonUtil.isEmpty(user.getIdcaodb()) && CommonUtil.isEmpty(idcaodb))
                        || (CommonUtil.isEmpty(user.getIdcaodall()) && CommonUtil.isEmpty(idcaodall))
                        || CommonUtil.isEmpty(idnumber))) {
            return ServerResponse.createByErrorMessage("请确认您的资料是否全部填写？");
        }
        if (!CommonUtil.isEmpty(idnumber)) {
            String idCard = RKIDCardUtil.getIDCardValidate(idnumber);
            if (!"".equals(idCard)) {//验证身份证
                return ServerResponse.createByErrorMessage(idCard);
            }
        }
        if (!CommonUtil.isEmpty(name))
            user.setName(name);
        if (!CommonUtil.isEmpty(idcaoda))
            user.setIdcaoda(idcaoda);
        if (!CommonUtil.isEmpty(idcaodb))
            user.setIdcaodb(idcaodb);
        if (!CommonUtil.isEmpty(idcaodall))
            user.setIdcaodall(idcaodall);
        if (!CommonUtil.isEmpty(idnumber))
            user.setIdnumber(idnumber);
        user.setRealNameState(1);
        memberMapper.updateByPrimaryKeySelective(user);
        updataMember(user, userToken);
        return ServerResponse.createBySuccessMessage("提交资料成功");
    }

    /**
     * 工种认证提交申请
     *
     * @param userToken    token
     * @param workerTypeId 工种类型的id
     */
    public ServerResponse certificationWorkerType(String userToken, String workerTypeId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        user = memberMapper.selectByPrimaryKey(user.getId());
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if (user.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
        }
        if (user.getCheckType() != 5 && user.getCheckType() != 1) {
            return ServerResponse.createByErrorMessage("您已提交过工种，不能再次提交");
        }
        user.setWorkerTypeId(workerTypeId);
        WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
        if (wt != null) {
            user.setWorkerType(wt.getType());
        }
        user.setCheckType(0);
        memberMapper.updateByPrimaryKeySelective(user);
        updataMember(user, userToken);
        return ServerResponse.createBySuccessMessage("提交工种认证成功");
    }

    /**
     * 找回密码 获取code
     */
    public ServerResponse forgotPasswordCode(String phone) {
        Member user = new Member();
        user.setMobile(phone);
        user = memberMapper.getUser(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("电话号码未注册！");
        } else {
            int registerCode = (int) (Math.random() * 9000 + 1000);
            user.setSmscode(registerCode);
            memberMapper.updateByPrimaryKeySelective(user);
            JsmsUtil.SMS(registerCode, phone);
            //记录短信发送
            Sms sms = new Sms();
            sms.setCode(String.valueOf(registerCode));
            sms.setMobile(phone);
            smsMapper.insert(sms);
            return ServerResponse.createBySuccessMessage("验证码已发送");
        }
    }

    /**
     * 找回密码校验验证码
     */
    public ServerResponse checkForgotPasswordCode(String phone, int smscode) {
        Member user = new Member();
        user.setMobile(phone);
        user.setSmscode(smscode);
        user = memberMapper.getUser(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("验证码错误！");
        } else {
            String token = TokenUtil.getRandom();
            redisClient.put(Constants.TEMP_TOKEN + phone, token);
            return ServerResponse.createBySuccess("验证码正确", token);
        }
    }

    /**
     * 找回密码更新密码
     */
    public ServerResponse updateForgotPassword(String phone, String password, String token) {
        if (CommonUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("身份认证错误,无认证参数！");
        }
        if (CommonUtil.isEmpty(password) || password.length() < 6) {
            return ServerResponse.createByErrorMessage("密码不得小于六位数");
        }
        Member user = new Member();
        user.setMobile(phone);
        user = memberMapper.getUser(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("电话号码未注册！");
        } else {
            if (user.getCheckType() == 4) {
                //冻结的帐户不能修改资料信息
                return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
            }
            String mytoken = redisClient.getCache(Constants.TEMP_TOKEN + phone, String.class);
            if (!token.equals(mytoken)) {
                return ServerResponse.createByErrorMessage("身份认证错误，身份不匹配！");
            }
            //认证通过，清除token认证
            redisClient.deleteCache(Constants.TEMP_TOKEN + phone);
            user.setPassword(DigestUtils.md5Hex(password));
            user.setSmscode(0);
            memberMapper.updateByPrimaryKeySelective(user);
            user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            MainUser mainUser = userMapper.findUserByMobile(user.getMobile());
            AccessToken accessToken = TokenUtil.generateAccessToken(user, mainUser);
            redisClient.put(accessToken.getUserToken() + Constants.SESSIONUSERID, accessToken);
            return ServerResponse.createBySuccessMessage("设置密码成功");
        }
    }

    /**
     * 业主列表
     */
    public ServerResponse getMemberList(PageDTO pageDTO, String cityId, String userKey, Integer stage, String userRole, String searchKey, String parentId, String childId, String orderBy, String type, String userId, String beginDate, String endDate) {
        try {
            userKey = iStoreUserMapper.getVisitUser(userKey);
            logger.info("权限返回结果 id:" + userKey);
            List<String> childsLabelIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(parentId)) {
                if (!StringUtils.isNotBlank(childId)) {//如果 子标签为null ，就是父标签的所有标签
                    List<MemberLabel> childsList = iMemberLabelMapper.getChildLabelByParentId(parentId);
                    for (MemberLabel aChildsList : childsList) childsLabelIdList.add(aChildsList.getId());
                } else {
                    childsLabelIdList.add(childId);
                }
            }
            String[] childsLabelIdArr = new String[childsLabelIdList.size()];
            childsLabelIdList.toArray(childsLabelIdArr);
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Member> list = memberMapper.getMemberListByName(cityId, searchKey, stage, userRole, childsLabelIdArr, orderBy, type, userKey, userId, beginDate, endDate);
            PageInfo pageResult = new PageInfo(list);
            List<MemberCustomerDTO> mcDTOList = new ArrayList<>();
            for (Member member : list) {
                logger.info("getMemberListByName id:" + member.getId());
                Customer customer = iCustomerMapper.getCustomerByMemberId(member.getId());
                //每个业主增加关联 客服跟进
                if (customer == null) {
                    customer = new Customer();
                    customer.setMemberId(member.getId());
                    customer.setStage(0);
                } else {
                    if (customer.getRemindRecordId() != null)//有提醒记录的 更新 为最新的更新沟通记录
                        customerRecordService.updateMaxNearRemind(customer);
                }
                MemberCustomerDTO mcDTO = new MemberCustomerDTO();
                mcDTO.setMcId(customer.getId());
                mcDTO.setPhaseStatus(customer.getPhaseStatus());
                mcDTO.setOrderDate(member.getModifyDate());
                mcDTO.setMemberId(member.getId());
                mcDTO.setMemberName(member.getName());
                mcDTO.setMemberNickName(member.getNickName());
                mcDTO.setMobile(member.getMobile());
                mcDTO.setReferrals(member.getReferrals());

                if (!CommonUtil.isEmpty(member.getReferrals())) {
                    Member referralsMember = memberMapper.selectByPrimaryKey(member.getReferrals());
                    if (referralsMember != null) {
                        mcDTO.setReferralsMobile(referralsMember.getMobile());
                    } else {
                        mcDTO.setReferralsMobile("");
                    }
                }
                mcDTO.setSource("来源未知");
                mcDTO.setStage(customer.getStage());
                mcDTO.setUserId(customer.getUserId());
                mcDTO.setCreateDate(member.getCreateDate());
                if (!CommonUtil.isEmpty(customer.getUserId())) {
                    MainUser mainUser = userMapper.selectByPrimaryKey(customer.getUserId());
                    if (null != mainUser) {
                        mcDTO.setUserName(mainUser.getUsername());
                    }
                }
                //找到提醒内容 ： 离当前时间最近的那一条
                if (customer.getCurrRecordId() != null) {
                    CustomerRecord remindCustomerRecord = iCustomerRecordMapper.selectByPrimaryKey(customer.getCurrRecordId());
                    mcDTO.setRemindContent(remindCustomerRecord.getDescribes());
                    mcDTO.setRemindTime(remindCustomerRecord.getRemindTime());
                    if (remindCustomerRecord.getRemindTime() != null) {
                        mcDTO.setRemindTimeOvertime(
                                Long.compare(remindCustomerRecord.getRemindTime().getTime(), new Date().getTime()));
                    } else {
                        mcDTO.setRemindTimeOvertime(-1);
                    }
                }
                if (customer.getCurrRecordId() != null) {
                    CustomerRecord currCustomerRecord = iCustomerRecordMapper.selectByPrimaryKey(customer.getCurrRecordId());
                    if (currCustomerRecord != null)
                        mcDTO.setLastRecord(currCustomerRecord.getCreateDate());
                }
                if (member.getRemarks() != null) {
                    mcDTO.setRemarks(member.getRemarks());//业主备注
                }
                List<MemberLabel> memberLabelList = new ArrayList<>();
                if (customer.getLabelIdArr() != null) {
                    String[] labelIdArr = customer.getLabelIdArr().split(",");
                    Example example = new Example(MemberLabel.class);
                    example.createCriteria().andIn(Member.ID, Arrays.asList(labelIdArr));
                    memberLabelList = iMemberLabelMapper.selectByExample(example);
                }
                mcDTO.setMemberLabelList(memberLabelList);

                Example example = new Example(MemberCity.class);
                example.createCriteria()
                        .andEqualTo(MemberCity.MEMBER_ID, member.getId())
                        .andEqualTo(MemberCity.CITY_ID, cityId);
                example.orderBy(MemberCity.CREATE_DATE);
                List<MemberCity> listcity = memberCityMapper.selectByExample(example);
                if (listcity.size() > 0) {
                    mcDTO.setMemberCityID(listcity.get(0).getCityId());
                    mcDTO.setMemberCityName(listcity.get(0).getCityName());
                }
                mcDTOList.add(mcDTO);
            }
//            logger.info(" mcDTOList getMemberNickName:" + mcDTOList.get(0).getMemberNickName());
//            logger.info("mcDTOList size:" + mcDTOList.size() +" mcDTOListOrderBy:"+ mcDTOListOrderBy.size() + " list:"+ list.size());
            pageResult.setList(mcDTOList);
            return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 业主列表
     */
    public ServerResponse setMember(Member member) {
        try {
            logger.info("setMember member: " + member);
            Member srcMember = memberMapper.selectByPrimaryKey(member.getId());
            if (srcMember == null)
                return ServerResponse.createByErrorMessage("该业主不存在");
            if (StringUtils.isNotBlank(member.getNickName()))
                srcMember.setNickName(member.getNickName());
            if (StringUtils.isNotBlank(member.getMobile()))
                srcMember.setMobile(member.getMobile());
            if (StringUtils.isNotBlank(member.getRemarks()))
                srcMember.setRemarks(member.getRemarks());
            if (srcMember.getCheckType() == 4) {
                //冻结的帐户不能修改资料信息
                return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
            }
            memberMapper.updateByPrimaryKeySelective(srcMember);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }


    /**
     * 我的邀请码
     */
    public ServerResponse getMyInvitation(String userToken, Integer userRole) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo(Member.OTHERS_INVITATION_CODE, member.getInvitationCode());
        Map<String, Object> memberMap = new HashMap<>();
        memberMap.put("invitationNum", memberMapper.selectCountByExample(example));
        memberMap.put("invitationCode", member.getInvitationCode());
        memberMap.put("id", member.getId());
        if (userRole == 3) {
            String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
            MainUser mainUser = userMapper.findUserByMobile(member.getMobile());
            memberMap.put("codeData", url + String.format("codeDetails?invitationCode=%s&memberId=%s&userId=%s&title=%s",
                    member.getInvitationCode(), member.getId(), mainUser == null ? "" : mainUser.getId(), "好工匠 在当家"));
        }
        return ServerResponse.createBySuccess("ok", memberMap);
    }

    /**
     * 查询用户实名认证列表
     *
     * @param searchKey     申请人或申请人手机好关键字
     * @param realNameState -1:全部，1:认证中，2:认证被驳回，3:认证通过 （默认-1）
     */
    public ServerResponse certificationList(PageDTO pageDTO, String searchKey, Integer realNameState) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(Member.class);
            Example.Criteria criteria = example.createCriteria();
            if (!CommonUtil.isEmpty(searchKey)) {
                criteria.andCondition("(name like '%" + searchKey
                        + "%' or nick_name like '%" + searchKey
                        + "%' or mobile like '%" + searchKey + "%')");
            }
            if (realNameState == null) realNameState = -1;
            switch (realNameState) {
                case -1:
                    criteria.andCondition("real_name_state != 0");
                    break;
                case 1:
                case 2:
                case 3:
                    criteria.andEqualTo(Member.REAL_NAME_STATE, realNameState);
                    break;
                default:
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                            , "查无数据");
            }
            List<Member> members = memberMapper.selectByExample(example);
            if (members.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(members);
            for (Member member : members) {
                member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                member.setPassword(null);
            }
            pageResult.setList(members);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询用户实名认证详情
     *
     * @param userId 用户id
     * @return
     */
    public ServerResponse certificationDetails(String userId) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        Member member = memberMapper.selectByPrimaryKey(userId);
        if (member == null || member.getRealNameState() == 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                    , "查无数据");
        }
        member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        member.setPassword(null);
        return ServerResponse.createBySuccess("查询成功", member);
    }

    /**
     * 实名认证审核提交
     *
     * @param userId           用户id
     * @param realNameState    2:认证驳回，3:认证通过
     * @param realNameDescribe 实名认证描述
     * @return
     */
    public ServerResponse certificationAuditing(String userId, Integer realNameState, String realNameDescribe) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        if (CommonUtil.isEmpty(realNameState)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        Member user = memberMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if (user.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
        }
        if (user.getRealNameState() == 0) {
            return ServerResponse.createByErrorMessage("请通知用户提交相关资料");
        }
        user.setRealNameState(realNameState);
        if (realNameState == 3) {
            user.setRealNameDescribe("审核通过");
        } else {
            user.setRealNameDescribe(CommonUtil.isEmpty(realNameDescribe) ? "审核不通过" : realNameDescribe);
        }
        memberMapper.updateByPrimaryKeySelective(user);
        updataAccessTokenMember(user);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 工匠审核
     *
     * @param workerId      工匠id
     * @param checkType     1审核未通过, 2审核已通过
     * @param checkDescribe 工匠审核描述
     * @return
     */
    public ServerResponse checkWorker(String workerId, Integer checkType, String checkDescribe) {
        if (CommonUtil.isEmpty(workerId)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        if (CommonUtil.isEmpty(checkType)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        Member user = memberMapper.selectByPrimaryKey(workerId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if (checkType != 4 && checkType != 2 && user.getCheckType() == 4) {
            //冻结的帐户不能修改资料信息
            return ServerResponse.createByErrorMessage("账户冻结，无法修改资料");
        }
        if (user.getRealNameState() != 3) {
            return ServerResponse.createByErrorMessage("请先审核'实名认证'");
        }
        if (user.getCheckType() == 5) {
            return ServerResponse.createByErrorMessage("请通知用户提交工种");
        }
        user.setCheckType(checkType);
        if (checkType == 2) {
            user.setCheckDescribe("审核通过");
        } else {
            user.setCheckDescribe(CommonUtil.isEmpty(checkDescribe) ? "审核不通过" : checkDescribe);
        }
        memberMapper.updateByPrimaryKeySelective(user);
        updataAccessTokenMember(user);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    private void updataAccessTokenMember(Member user) {
        String userRole = "role" + 1 + ":" + user.getId();
        String token = redisClient.getCache(userRole, String.class);
        updataMember(user, token);
        userRole = "role" + 2 + ":" + user.getId();
        token = redisClient.getCache(userRole, String.class);
        updataMember(user, token);
        userRole = "role" + 3 + ":" + user.getId();
        token = redisClient.getCache(userRole, String.class);
        updataMember(user, token);
    }

    private void updataMember(Member user, String token) {
        if (!CommonUtil.isEmpty(token)) {
            AccessToken accessToken = redisClient.getCache(token + Constants.SESSIONUSERID, AccessToken.class);
            updataMember(user, accessToken);
        }
    }

    private void updataMember(Member user, AccessToken accessToken) {
        if (accessToken != null) {
            user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            user.setPassword(null);
            accessToken.setMember(user);
            if (!CommonUtil.isEmpty(user.getWorkerTypeId())) {
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
                if (wt != null) {
                    accessToken.setWorkerTypeName(wt.getName());
                }
            }
            redisClient.put(accessToken.getUserToken() + Constants.SESSIONUSERID, accessToken);
        }
    }


    /**
     * 获取用户信息
     *
     * @param userToken
     * @param memberId
     * @param phone
     * @return
     */
    public ServerResponse getMembers(String userToken, String memberId, String phone) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = null;
        if (!CommonUtil.isEmpty(memberId)) {
            member = memberMapper.selectByPrimaryKey(memberId);
        } else if (!CommonUtil.isEmpty(phone)) {
            Member user = new Member();
            user.setMobile(phone);
            member = memberMapper.getUser(user);
        }
        if (member == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无该用户");
        }
        member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        List<Map<String, Object>> datas = new ArrayList<>();
        Example example = new Example(MemberInfo.class);
        example.createCriteria().andEqualTo(MemberInfo.MEMBER_ID, member.getId()).andEqualTo(MemberInfo.POLICY_ID, 1);
        List<MemberInfo> infos = memberInfoMapper.selectByExample(example);
        if (infos != null && infos.size() > 0) {//有业主
            Map<String, Object> map = new HashMap<>();
            map.put("memberType", 0);
            map.put("id", member.getId());
            map.put("nickName", member.getNickName());
            map.put("name", member.getNickName());
            map.put("mobile", member.getMobile());
            map.put("head", member.getHead());
            map.put("appKey", messageAPI.getAppKey(AppType.ZHUANGXIU.getDesc()));
            datas.add(map);
        }
        example = new Example(MemberInfo.class);
        example.createCriteria().andEqualTo(MemberInfo.MEMBER_ID, member.getId()).andEqualTo(MemberInfo.POLICY_ID, 2);
        infos = memberInfoMapper.selectByExample(example);
        if (infos != null && infos.size() > 0) {//有工匠
            Map<String, Object> map = new HashMap<>();
            map.put("memberType", 1);
            map.put("id", member.getId());
            map.put("nickName", member.getNickName());
            map.put("name", member.getNickName());
            map.put("mobile", member.getMobile());
            map.put("head", member.getHead());
            if (!CommonUtil.isEmpty(member.getWorkerTypeId())) {
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                map.put("workerTypeId", member.getWorkerTypeId());
                if (wt != null) {
                    map.put("workerName", wt.getName());
                }
            }
            map.put("appKey", messageAPI.getAppKey(AppType.GONGJIANG.getDesc()));
            datas.add(map);
        }
        MainUser mainUser = userMapper.findUserByMobile(member.getMobile());
        if (mainUser != null) {
            ServerResponse serverResponse = setSale(null, mainUser.getId());
            if (serverResponse.isSuccess()) {//有销售
                Map<String, Object> map = new HashMap<>();
                map.put("memberType", 2);
                map.put("id", mainUser.getId());
                map.put("nickName", member.getNickName());
                map.put("name", member.getNickName());
                map.put("mobile", member.getMobile());
                map.put("head", member.getHead());
                map.put("appKey", messageAPI.getAppKey(AppType.SALE.getDesc()));
                datas.add(map);
            }
        }
        if (datas.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无该用户");
        }
        return ServerResponse.createBySuccess("查询成功", datas);
    }


    /**
     * 获取我的界面
     *
     * @param userToken 用户登录信息
     * @return 我的页面
     */
    public ServerResponse getMyHomePage(String userToken, Integer userRole) {
        if (userRole == 1) {
            HomePageBean homePageBean = new HomePageBean();
            homePageBean.setList(getMyMenuList(userRole, null));
            return ServerResponse.createBySuccess("获取我的界面成功！", homePageBean);
        } else {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            worker = memberMapper.selectByPrimaryKey(worker.getId());
            if (worker == null) {
                return ServerResponse.createbyUserTokenError();
            }
            HomePageBean homePageBean = new HomePageBean();
            homePageBean.setWorkerId(worker.getId());
            homePageBean.setIoflow(CommonUtil.isEmpty(worker.getHead()) ? null : imageAddress + worker.getHead());
            homePageBean.setWorkerName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
            homePageBean.setEvaluation(worker.getEvaluationScore() == null ? new BigDecimal(60) : worker.getEvaluationScore());
            homePageBean.setFavorable(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
            StringBuilder stringBuffer = new StringBuilder();
            if (worker.getIsCrowned() == null || worker.getIsCrowned() != 1) {
                if (worker.getEvaluationScore() == null) {
                    stringBuffer.append("普通");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 90) {
                    stringBuffer.append("金牌");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 80) {
                    stringBuffer.append("银牌");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 70) {
                    stringBuffer.append("铜牌");
                } else {
                    stringBuffer.append("普通");
                }
            } else {
                stringBuffer.append("皇冠");
            }
            stringBuffer.append(worker.getWorkerType() != null && worker.getWorkerType() == 3 ? "大管家" : "工匠");
            homePageBean.setGradeName(stringBuffer.toString());
            homePageBean.setList(getMyMenuList(userRole, worker.getWorkerType()));
            return ServerResponse.createBySuccess("获取我的界面成功！", homePageBean);
        }
    }

    private List<HomePageBean.ListBean> getMyMenuList(Integer userRole, Integer workerType) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        Example example = new Example(MenuConfiguration.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(MenuConfiguration.DATA_STATUS, 0)
                .andEqualTo(MenuConfiguration.MENU_TYPE, 1);
        if (userRole == 1) {
            criteria.andEqualTo(MenuConfiguration.SHOW_PROPRIETOR, 1);
        } else if (workerType == null) {
            criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, 1);
        } else {
            switch (workerType) {
                case 1://设计师
                    criteria.andEqualTo(MenuConfiguration.SHOW_DESIGNER, 1);
                    break;
                case 2://精算师
                    criteria.andEqualTo(MenuConfiguration.SHOW_ACTUARIES, 1);
                    break;
                case 3://大管家
                    criteria.andEqualTo(MenuConfiguration.SHOW_HOUSEKEEPER, 1);
                    break;
                default://工匠
                    criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, 1);
                    break;
            }
        }
        example.orderBy(MenuConfiguration.SORT).asc();
        List<MenuConfiguration> menuConfigurations = iMenuConfigurationMapper.selectByExample(example);
        List<HomePageBean.ListBean> list = new ArrayList<>();
        for (MenuConfiguration configuration : menuConfigurations) {
            configuration.initPath(imageAddress, webAddress);
            HomePageBean.ListBean listBean = new HomePageBean.ListBean();
            listBean.setName(configuration.getName());
            listBean.setUrl(configuration.getUrl());
            listBean.setImageUrl(configuration.getImage());
            listBean.setType(configuration.getType());
            list.add(listBean);
        }
        return list;
    }

}
