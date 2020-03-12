package com.dangjia.acg.service.member;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.*;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HomePageBean;
import com.dangjia.acg.dto.member.MemberCustomerDTO;
import com.dangjia.acg.dto.member.MemberDTO;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.dto.shell.HomeShellProductSpecDTO;
import com.dangjia.acg.dto.worker.WorkerComprehensiveDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.engineer.DjSkillCertificationMapper;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.*;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductSpecMapper;
import com.dangjia.acg.mapper.shell.IMasterOrderNodeMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjSkillCertification;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.member.*;
import com.dangjia.acg.modle.order.OrderNode;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.service.activity.RedPackPayService;
import com.dangjia.acg.service.clue.ClueService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.configRule.ConfigRuleUtilService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.RKIDCardUtil;
import com.dangjia.acg.util.StringTool;
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
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
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
    private IInsuranceMapper iInsuranceMapper;

    @Autowired
    private DjSkillCertificationMapper djSkillCertificationMapper;
    @Autowired
    private IMasterOrderNodeMapper masterOrderNodeMapper;

    @Autowired
    private IOrderMapper iOrderMapper;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;


    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private ConfigRuleUtilService configRuleUtilService;


    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;
    @Autowired
    private IWorkerBankCardMapper iWorkerBankCardMapper;
    @Autowired
    private IActivityRedPackRecordMapper activityRedPackRecordMapper;
    @Autowired
    private IHomeShellProductMapper homeShellProductMapper;
    @Autowired
    private IHomeShellProductSpecMapper productSpecMapper;

    /**
     * 获取用户手机号
     *
     * @param id     来源ID
     * @param idType 1=房屋ID, 2=用户ID, 3=供应商ID, 4=系统用户, 5=验房分销, 6=用户地址
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
            case "4":
                Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(id);
                if (storefront != null) {
                    mobile = storefront.getMobile();
                }
                break;
            case "5":
                HouseDistribution distribution = iHouseDistributionMapper.selectByPrimaryKey(id);
                if (distribution != null) {
                    mobile = distribution.getPhone();
                }
                break;
            case "6":
                MemberAddress memberAddress = iMasterMemberAddressMapper.selectByPrimaryKey(id);
                if (memberAddress != null) {
                    mobile = memberAddress.getMobile();
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
            if (!CommonUtil.isEmpty(userRole) && userRole == 3) {//销售端放开登录权限
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
    public ServerResponse login(String phone, String password, String loginMode, Integer userRole) {
        Member user;
        //验证码登陆
        if ("2".equals(loginMode)) {
            Integer registerCode = redisClient.getCache(Constants.SMS_LOGIN_CODE + phone, Integer.class);
            if (!password.equals(registerCode + "")) {
                return ServerResponse.createByErrorMessage("验证码错误");
            }
            user = memberMapper.getByPhone(phone);
        } else {
            //指定角色查询用户
            user = new Member();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex(password));
            user = memberMapper.getUser(user);
        }
        if (user == null) {
            String msg = "手机号或密码错误";
            if ("2".equals(loginMode)) {
                msg = "手机号或验证码错误";
            }
            return ServerResponse.createByErrorMessage(msg);
        }
        return getUser(user, userRole);

    }

    ServerResponse getUser(Member user, Integer userRole) {

        if (userRole == 1) {
            Example example = new Example(Customer.class);
            example.createCriteria().andEqualTo(Customer.MEMBER_ID, user.getId());
            List<Customer> customers = iCustomerMapper.selectByExample(example);
            if (customers.size() <= 0) {
                clueService.sendUser(user.getId(), user.getMobile(), null, null);
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
        if (mainUser != null) {
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
        switch (userRole) {
            case 1:
                groupInfoService.registerJGUsers(AppType.ZHUANGXIU.getDesc(), new String[]{member.getId()}, new String[1]);
                break;
            case 2:
                groupInfoService.registerJGUsers(AppType.GONGJIANG.getDesc(), new String[]{member.getId()}, new String[1]);
                break;
            case 3:
                if (!CommonUtil.isEmpty(accessToken.getUserId()))
                    groupInfoService.registerJGUsers(AppType.SALE.getDesc(), new String[]{accessToken.getUserId()}, new String[1]);
                break;
        }
        return ServerResponse.createBySuccess(accessToken);
    }

    /**
     * 接口注册获取验证码
     *
     * @param phone    手机号
     * @param codeType 验证码类型，1=登陆   2=注册
     * @return
     */
    public ServerResponse registerCode(String phone, String codeType) {
        if (!Validator.isMobileNo(phone)) {
            return ServerResponse.createByErrorMessage("手机号不正确");
        }
        Member user = new Member();
        user.setMobile(phone);
        user = memberMapper.getUser(user);
        if ("1".equals(codeType) && user == null) {
            return ServerResponse.createByErrorMessage("手机号没有注册");
        } else if ("2".equals(codeType) && user != null) {
            return ServerResponse.createByErrorMessage("手机号已被注册");
        } else {
//            Integer registerCode = redisClient.getCache(Constants.SMS_CODE + phone, Integer.class);
            Integer registerCode = (int) (Math.random() * 9000 + 1000);
            if ("1".equals(codeType)) {
                redisClient.put(Constants.SMS_LOGIN_CODE + phone, registerCode);
            } else {
                redisClient.put(Constants.SMS_CODE + phone, registerCode);
            }
            String result = JsmsUtil.SMS(registerCode, phone);
            //记录短信发送
            Sms sms = new Sms();
            sms.setCode(String.valueOf(registerCode));
            sms.setMobile(phone);
            sms.setContent(result);
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

    public ServerResponse register(HttpServletRequest request, String phone, String password,
                                   String invitationCode, Integer userRole,
                                   String longitude, String latitude) {
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
        if (!CommonUtil.isEmpty(password))
            user.setPassword(DigestUtils.md5Hex(password));//验证码正确设置密码
        user.setCityId(CommonUtil.isEmpty(request.getParameter(Constants.CITY_ID)) ? "402881882ba8753a012ba93101120116" : request.getParameter(Constants.CITY_ID));
        user.setPolicyId(String.valueOf(userRole));
        if (!CommonUtil.isEmpty(user.getCityId())) {
            City city = iCityMapper.selectByPrimaryKey(user.getCityId());
            user.setCityName(city.getName());
        }

        if (!CommonUtil.isEmpty(request.getParameter(Member.WORKER_TYPE_ID))) {
            WorkerType wt = workerTypeMapper.selectByPrimaryKey(request.getParameter(Member.WORKER_TYPE_ID));
            if (wt != null) {
                user.setWorkerTypeId(wt.getId());
                user.setWorkerType(wt.getType());
                user.setCheckType(0);
            }
        }
        memberMapper.insertSelective(user);
        updateOrInsertInfo(user.getId(), String.valueOf(userRole), user.getPassword());
        ServerResponse serverResponse = setAccessToken(user, userRole);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        MemberCity userCity = new MemberCity();
        userCity.setMemberId(user.getId());
        if (!CommonUtil.isEmpty(user.getCityId())) {
            userCity.setCityId(user.getCityId());
            userCity.setCityName(user.getName());
            memberCityMapper.insert(userCity);
        }
//            userRole", value = "app应用角色  1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
        if (userRole == 1) {
            clueService.sendUser(user.getId(), user.getMobile(), longitude, latitude);
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
            return register(request, phone, password, invitationCode, userRole, longitude, latitude);
        }
    }

    /**
     * 注销账号
     *
     * @param userToken
     * @return
     */
    public ServerResponse cancellationAccountMember(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Map map = new HashMap();
            map.put("state", 0);
            //1.判断用户名下是否有未完工的房子
            Example example = new Example(House.class);
            example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId()).andEqualTo(House.VISIT_STATE, 1);
            List<House> houseList = houseMapper.selectByExample(example);
            if (houseList != null && houseList.size() > 0) {
                map.put("stateName", "用户名下有未完工的房子不能注销");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //2.判断是否有未支付完成的订单
            Integer count = iOrderMapper.selectCountOrderByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在未支付完成的订单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断是否有未要货的订单
            count = iOrderMapper.selectOrderItemByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在已支付未要货的订单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断是否有待发货的单
            count = iOrderMapper.selectOrderSplitItemByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在已要货待处理的订单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断是否有待收货的单
            count = iOrderMapper.selectOrderSplitDeliverByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在待收货的订单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断是否有待退款的订单
            count = iOrderMapper.selectMendOrderByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在已申请退款待处理的订单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断是否有未处理完的退款单
            count = iOrderMapper.selectMendDeliverByMemberId(member.getId());
            if (count != null && count > 0) {
                map.put("stateName", "用户名下存在正在处理中的退款单");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            //判断账户是否有余额未提现
            Member newMember = memberMapper.selectByPrimaryKey(member.getId());
            if (newMember.getSurplusMoney() != null && newMember.getSurplusMoney().doubleValue() > 0) {
                map.put("stateName", "存在未提现的余额");
                return ServerResponse.createBySuccess("注销失败 ", map);
            }
            newMember.setDataStatus(1);
            newMember.setMobile("---delete----" + newMember.getMobile());
            newMember.setName("--delet--" + member.getName());
            newMember.setId("--delete--" + member.getId());
            memberMapper.updateByPrimaryKey(newMember);
            map.put("state", 1);
            map.put("stateName", "注销成功");
            return ServerResponse.createBySuccess("注销成功 ", map);
        } catch (Exception e) {
            logger.error("注销失败", e);
            return ServerResponse.createByErrorMessage("注销失败");
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
        Member user = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (user == null) {
            return ServerResponse.createbyUserTokenError();
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
//                        || (CommonUtil.isEmpty(user.getIdcaodall()) && CommonUtil.isEmpty(idcaodall))
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
        user.setRealNameTime(new Date());
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
        Member user = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (user == null) {
            return ServerResponse.createbyUserTokenError();
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
            String result = JsmsUtil.SMS(registerCode, phone);
            //记录短信发送
            Sms sms = new Sms();
            sms.setCode(String.valueOf(registerCode));
            sms.setMobile(phone);
            sms.setContent(result);
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
     * 更新工匠持单量
     */
    public ServerResponse updateMethods(String workerId, Integer methods) {
        Member user = memberMapper.selectByPrimaryKey(workerId);
        user.setMethods(methods);
        memberMapper.updateByPrimaryKeySelective(user);
        return ServerResponse.createBySuccessMessage("持单量设置成功");
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
                Customer customer = iCustomerMapper.getCustomerBy(member.getId());
                //每个业主增加关联 客服跟进
                if (customer == null) {
                    customer = new Customer();
                    customer.setMemberId(member.getId());
                    customer.setStage(1);
                    customer.setTurnStatus(0);
                    customer.setPhaseStatus(1);
                    customer.setClueType(0);
                    customer.setDataStatus(0);
                    customer.setTips("0");
                    customer.setCityId(member.getCityId());
                    customer.setPhaseStatus(1);
                    iCustomerMapper.insert(customer);
                } else {
                    if (customer.getRemindRecordId() != null)//有提醒记录的 更新 为最新的更新沟通记录
                    {
                        customerRecordService.updateMaxNearRemind(customer);
                    }
                }
                MemberCustomerDTO mcDTO = new MemberCustomerDTO();
                mcDTO.setMcId(customer.getId());
                mcDTO.setPhaseStatus(customer.getPhaseStatus());
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
                mcDTO.setMemberCityID(member.getCityId());
                mcDTO.setMemberCityName(member.getCityName());


                Date house = houseMapper.getHouseDateByMemberId(member.getId());
                mcDTO.setOrderDate(house);
                mcDTOList.add(mcDTO);
            }
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
    public ServerResponse setMember(String userToken, Member member) {
        try {
            if (CommonUtil.isEmpty(userToken)) {
                userToken = member.getId();
            }
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member srcMember = (Member) object;

            if (srcMember == null)
                return ServerResponse.createByErrorMessage("该业主不存在");
            if (StringUtils.isNotBlank(member.getNickName()))
                srcMember.setNickName(member.getNickName());
            if (StringUtils.isNotBlank(member.getMobile()))
                srcMember.setMobile(member.getMobile());
            if (StringUtils.isNotBlank(member.getRemarks()))
                srcMember.setRemarks(member.getRemarks());
            if (StringUtils.isNotBlank(member.getAutoOrder()))
                srcMember.setAutoOrder(member.getAutoOrder());
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
        if (userRole != null && userRole == 3) {
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
    public ServerResponse certificationList(PageDTO pageDTO, String searchKey, String cityId, String policyId, Integer realNameState) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(Member.class);
            Example.Criteria criteria = example.createCriteria();
            if (!CommonUtil.isEmpty(searchKey)) {
                criteria.andCondition("(name like '%" + searchKey
                        + "%' or nick_name like '%" + searchKey
                        + "%' or mobile like '%" + searchKey + "%')");
            }
            if (!CommonUtil.isEmpty(cityId)) {
                criteria.andEqualTo(Member.CITY_ID, cityId);
            }
            if (!CommonUtil.isEmpty(policyId)) {
                criteria.andEqualTo(Member.POLICY_ID, policyId);
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

        user.setRealNameTime(new Date());
        user.setRealNameCheckTime(new Date());
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
            map.put("appKey", NIMPost.APPKEY);
            datas.add(map);
        }
        //网易云信只需要一个身份用户
//        example = new Example(MemberInfo.class);
//        example.createCriteria().andEqualTo(MemberInfo.MEMBER_ID, member.getId()).andEqualTo(MemberInfo.POLICY_ID, 2);
//        infos = memberInfoMapper.selectByExample(example);
//        if (infos != null && infos.size() > 0) {//有工匠
//            Map<String, Object> map = new HashMap<>();
//            map.put("memberType", 1);
//            map.put("id", member.getId());
//            map.put("nickName", member.getNickName());
//            map.put("name", member.getNickName());
//            map.put("mobile", member.getMobile());
//            map.put("head", member.getHead());
//            if (!CommonUtil.isEmpty(member.getWorkerTypeId())) {
//                WorkerType wt = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
//                map.put("workerTypeId", member.getWorkerTypeId());
//                if (wt != null) {
//                    map.put("workerName", wt.getName());
//                }
//            }
//            map.put("appKey", NIMPost.APPKEY);
//            datas.add(map);
//        }
//        MainUser mainUser = userMapper.findUserByMobile(member.getMobile());
//        if (mainUser != null) {
//            ServerResponse serverResponse = setSale(null, mainUser.getId());
//            if (serverResponse.isSuccess()) {//有销售
//                Map<String, Object> map = new HashMap<>();
//                map.put("memberType", 2);
//                map.put("id", mainUser.getId());
//                map.put("nickName", member.getNickName());
//                map.put("name", member.getNickName());
//                map.put("mobile", member.getMobile());
//                map.put("head", member.getHead());
//                map.put("appKey", NIMPost.APPKEY);
//                datas.add(map);
//            }
//        }
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

        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        if (userRole == 1) {
            HomePageBean homePageBean = new HomePageBean();
//            homePageBean.setList(getMyMenuList(userRole, null));
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
            WorkerComprehensiveDTO workerComprehensive = workIntegralMapper.getComprehensiveWorker(worker.getId());
            HomePageBean homePageBean = new HomePageBean();
            homePageBean.setWorkerId(worker.getId());
            homePageBean.setIoflow(CommonUtil.isEmpty(worker.getHead()) ? null : imageAddress + worker.getHead());
            homePageBean.setWorkerName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
            homePageBean.setEvaluation(worker.getEvaluationScore() == null ? new BigDecimal(60) : worker.getEvaluationScore());
            homePageBean.setOverall(workerComprehensive.getOverall());
            homePageBean.setFavorable(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(configRuleUtilService.getMemberRank(worker.getId()));
            if (worker.getWorkerType() > 3) {
                stringBuffer.append("工匠");
            } else {
                stringBuffer.append(workerTypeMapper.getName(worker.getWorkerType()));
            }

            homePageBean.setGradeName(stringBuffer.toString());
            Example example1 = new Example(HouseWorkerOrder.class);
            example1.createCriteria().andEqualTo(HouseWorkerOrder.WORKER_ID, worker.getId());
            Integer orderTakingNum = houseWorkerOrderMapper.selectCountByExample(example1);
            homePageBean.setOrderTakingNum(orderTakingNum);

            //查询保险徽章
            Example example = new Example(Insurance.class);
            example.createCriteria().andEqualTo(Insurance.WORKER_ID, worker.getId())
                    .andEqualTo(Insurance.DATA_STATUS, 0);
            example.orderBy(Insurance.CREATE_DATE).desc();
            List<Insurance> insurance = iInsuranceMapper.selectByExample(example);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            if (insurance != null && insurance.size() > 0) {
                if (new Date().getTime() <= insurance.get(0).getEndDate().getTime()) {
                    map.put("type", 0);//保险期内
                } else {
                    map.put("type", 1);//保险期外
                }
                map.put("name", "保险详情");
                map.put("head", address + "iconWork/shqd_icon_bx@3x.png");
                map.put("id", insurance.get(0).getId());
                list.add(map);
            }

            //查询技能徽章
            example = new Example(DjSkillCertification.class);
            example.createCriteria().andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, worker.getId())
                    .andEqualTo(DjSkillCertification.DATA_STATUS, 0);
            List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
            if (djSkillCertifications != null && djSkillCertifications.size() > 0) {
                map = new HashMap<>();
                map.put("name", "技能培训");
                map.put("head", address + "iconWork/shqd_icon_jn@3x.png");
                map.put("id", worker.getId());
                list.add(map);
            }
            //他的徽章
            homePageBean.setList(list);
            //查询当这贝商品
            HomeShellProductDTO homeShellProductDTO = homeShellProductMapper.serachShellProductInfo();
            if (homeShellProductDTO != null) {
                homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(), address));
                HomeShellProductSpecDTO productSpecDTO = productSpecMapper.selectProductSpecInfo(homeShellProductDTO.getShellProductId());
                if (productSpecDTO != null) {
                    homeShellProductDTO.setProductSpecId(productSpecDTO.getProductSpecId());
                    homeShellProductDTO.setIntegral(productSpecDTO.getIntegral());
                    homeShellProductDTO.setMoney(productSpecDTO.getMoney());
                }
            }
            homePageBean.setHomeShellProduct(homeShellProductDTO);

            return ServerResponse.createBySuccess("获取我的界面成功！", homePageBean);
        }
    }

    /**
     * 获取我的徽章
     */
    public ServerResponse getMyInsigniaList(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Example example = new Example(OrderNode.class);
            example.createCriteria().andEqualTo(OrderNode.TYPE, "MY_INSIGNIA");
            example.orderBy(OrderNode.SORT);
            List<OrderNode> nodeList = masterOrderNodeMapper.selectByExample(example);
            if (nodeList == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map;
            for (OrderNode orderNode : nodeList) {
                map = new HashMap<>();
                map.put("head", address + orderNode.getNodeDescribe());
                map.put("id", orderNode.getCode());
                map.put("name", orderNode.getName());
                map.put("code", orderNode.getCode());
                map.put("type", 0);//未获取
                if ("H001".equals(orderNode.getCode())) {
                    //判断当前人员是否有保险
                    //查询保险徽章
                    example = new Example(Insurance.class);
                    example.createCriteria().andEqualTo(Insurance.WORKER_ID, worker.getId())
                            .andEqualTo(Insurance.DATA_STATUS, 0)
                            .andGreaterThanOrEqualTo(Insurance.END_DATE, new Date());
                    example.orderBy(Insurance.CREATE_DATE).desc();
                    List<Insurance> insurance = iInsuranceMapper.selectByExample(example);
                    if (insurance != null && insurance.size() > 0) {
                        map.put("head", address + "iconWork/shqd_icon_bx@3x.png");
                        map.put("id", insurance.get(0).getId());
                        map.put("type", 1);//已获取
                    }
                }
                if ("H002".equals(orderNode.getCode())) {//技能详情
                    example = new Example(DjSkillCertification.class);
                    example.createCriteria().andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, worker.getId())
                            .andEqualTo(DjSkillCertification.DATA_STATUS, 0);
                    List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
                    if (djSkillCertifications != null && djSkillCertifications.size() > 0) {
                        map.put("head", address + "iconWork/shqd_icon_jn@3x.png");
                        map.put("id", worker.getId());
                        map.put("type", 1);//已获取
                    }
                }

                list.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 获取我的徽章--徽章详情
     */
    public ServerResponse getMyInsigniaDetail(String userToken, String code) {
        try {
            if (code == null) {
                return ServerResponse.createByErrorMessage("请输入查询徽章的编码");
            }
            Map<String, Object> map = new HashMap<>();
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Member worker = (Member) object;
            Example example = new Example(OrderNode.class);
            example.createCriteria().andEqualTo(OrderNode.TYPE, "MY_INSIGNIA")
                    .andEqualTo(OrderNode.CODE, code);
            List<OrderNode> nodeList = masterOrderNodeMapper.selectByExample(example);
            if (nodeList != null) {
                OrderNode orderNode = nodeList.get(0);
                map.put("head", address + orderNode.getNodeDescribe());
                map.put("id", orderNode.getCode());
                map.put("name", orderNode.getName());
                map.put("code", orderNode.getCode());
                map.put("type", 0);//未获取
            }
            if ("H001".equals(code)) {
                //判断当前人员是否有保险
                //查询保险徽章
                example = new Example(Insurance.class);
                example.createCriteria().andEqualTo(Insurance.WORKER_ID, worker.getId())
                        .andEqualTo(Insurance.DATA_STATUS, 0)
                        .andGreaterThanOrEqualTo(Insurance.END_DATE, new Date());
                example.orderBy(Insurance.CREATE_DATE).desc();
                List<Insurance> insurance = iInsuranceMapper.selectByExample(example);
                if (insurance != null && insurance.size() > 0) {
                    Insurance rance = insurance.get(0);
                    map.put("head", address + "iconWork/shqd_icon_bx@3x.png");
                    map.put("id", insurance.get(0).getId());
                    map.put("startDate", rance.getStartDate());//保险开始时间
                    map.put("ednDate", rance.getEndDate());//保险结束时间
                    map.put("type", 1);//已获取
                } else {
                    map.put("showButton", "去购买");
                }
                //判断已有多少人获取
                example = new Example(Insurance.class);
                example.createCriteria().andEqualTo(Insurance.DATA_STATUS, 0)
                        .andGreaterThanOrEqualTo(Insurance.END_DATE, new Date());
                Integer count = iInsuranceMapper.selectCountByExample(example);
                map.put("remark", "有了保险才能开工哦，安全第一");
                map.put("countRemark", "已有<font color='#F57341'>" + count + "</font>人获得");

            }
            if ("H002".equals(code)) {//技能详情
                example = new Example(DjSkillCertification.class);
                example.createCriteria().andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, worker.getId())
                        .andEqualTo(DjSkillCertification.DATA_STATUS, 0);
                List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
                if (djSkillCertifications != null && djSkillCertifications.size() > 0) {
                    map.put("head", address + "iconWork/shqd_icon_jn@3x.png");
                    map.put("id", worker.getId());
                    map.put("type", 1);//已获取
                }
                example = new Example(DjSkillCertification.class);
                example.createCriteria().andEqualTo(DjSkillCertification.DATA_STATUS, 0);
                Integer count = iInsuranceMapper.selectCountByExample(example);
                map.put("remark", "由当家人员联系您进行线下培训");
                map.put("countRemark", "已有<font color='#F57341'>" + count + "</font>人获得");
            }
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 更新工匠保险信息
     *
     * @param insurance
     * @return
     */
    public ServerResponse updateInsurances(Insurance insurance) {
        insurance.setModifyDate(new Date());
        insurance.setCreateDate(null);
        insuranceMapper.updateByPrimaryKeySelective(insurance);
        return ServerResponse.createBySuccess("保存成功", insurance.getId());
    }

    /**
     * 新增工匠保险信息
     *
     * @param userToken
     * @return
     */
    public ServerResponse addInsurances(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        String insuranceMoney = configUtil.getValue(SysConfig.INSURANCE_MONEY, String.class);
        insuranceMoney = CommonUtil.isEmpty(insuranceMoney) ? "100" : insuranceMoney;
        Member operator = (Member) object;
        Example example = new Example(Insurance.class);
        example.createCriteria().andEqualTo(Insurance.WORKER_ID, operator.getId()).andIsNotNull(Insurance.END_DATE);
        example.orderBy(Insurance.END_DATE).desc();
        List<Insurance> insurances = insuranceMapper.selectByExample(example);
        example = new Example(Insurance.class);
        example.createCriteria().andEqualTo(Insurance.WORKER_ID, operator.getId());
        List<Insurance> insurances2 = insuranceMapper.selectByExample(example);
        Insurance insurance;
        if (insurances2.size() > 0) {
            insurance = insurances2.get(0);
        } else {
            insurance = new Insurance();
        }
        insurance.setWorkerId(operator.getId());
        insurance.setWorkerMobile(operator.getMobile());
        insurance.setWorkerName(operator.getName());
        insurance.setMoney(new BigDecimal(insuranceMoney));
        if (insurances.size() == 0) {
            insurance.setType("0");
        } else {
            insurance.setType("1");
        }
        if (insurances2.size() > 0) {
            insuranceMapper.updateByPrimaryKeySelective(insurance);
        } else {
            insuranceMapper.insert(insurance);
        }
        return ServerResponse.createBySuccess("ok", insurance.getId());
    }

    /**
     * 获取工匠保险信息
     *
     * @param type      保险类型 0=首保 1=续保
     * @param searchKey 工人名称或电话
     * @return
     */
    public ServerResponse queryInsurances(String type, String searchKey, PageDTO pageDTO) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<Map<String, Object>> datas = new ArrayList<>();
        Example example = new Example(Insurance.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNotNull(Insurance.END_DATE);
        if (!CommonUtil.isEmpty(type)) {
            criteria.andEqualTo(Insurance.TYPE, type);
        }
        if (!CommonUtil.isEmpty(searchKey)) {
            criteria.andCondition(" CONCAT(worker_mobile,worker_name) like CONCAT('%','" + searchKey + "','%')");
        }
        example.orderBy(Insurance.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Insurance> infos = insuranceMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(infos);
        if (infos != null && infos.size() > 0) {
            for (Insurance info : infos) {
                Map<String, Object> map = BeanUtils.beanToMap(info);
                map.put(Insurance.HEAD, Utils.getImageAddress(imageAddress, info.getHead()));
                map.put("surDay", 0);
                if (info.getEndDate() != null) {
                    Integer daynum = DateUtil.daysofTwo(new Date(), info.getEndDate());
                    if (daynum > 0) {
                        map.put("surDay", daynum);
                    }
                }
                datas.add(map);
            }
        }
        if (infos.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无该用户");
        }
        pageResult.setList(datas);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 获取我的保险信息
     *
     * @param userToken
     * @param pageDTO   页码
     * @return
     */
    public ServerResponse myInsurances(String userToken, PageDTO pageDTO) {

        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        List<Map<String, Object>> datas = new ArrayList<>();
        Example example = new Example(Insurance.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Insurance.WORKER_ID, operator);
        example.orderBy(Insurance.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Insurance> infos = insuranceMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(infos);
        if (infos != null && infos.size() > 0) {
            for (Insurance info : infos) {
                Map<String, Object> map = BeanUtils.beanToMap(info);
                map.put("surDay", 0);
                if (info.getEndDate() != null) {
                    Integer daynum = DateUtil.daysofTwo(new Date(), info.getEndDate());
                    if (daynum > 0) {
                        map.put("surDay", daynum);
                    }
                }
                datas.add(map);
            }
        }
        if (infos.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无该用户");
        }
        pageResult.setList(datas);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


    /**
     * 推广列表
     *
     * @param userToken
     * @param pageDTO
     * @return
     */
    public ServerResponse promotionList(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo(Member.OTHERS_INVITATION_CODE, member.getInvitationCode());
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Member> members = memberMapper.selectByExample(example);
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (Member member1 : members) {
            member1.setVisitState(houseMapper.queryPromotionListHouse(member1.getId()).getVisitState());
            member1.setHead(address + member1.getHead());
        }
        PageInfo pageResult = new PageInfo(members);
        if (members.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


    /**
     * 我的界面
     *
     * @param userToken
     * @return
     */
    public ServerResponse queryMember(String userToken,String houseId,String cityId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            member.initPath(imageAddress);
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setHead(member.getHead());
            memberDTO.setMobile(member.getMobile());
            Example example = new Example(WorkerBankCard.class);
            example.createCriteria().andEqualTo(WorkerBankCard.DATA_STATUS, 0)
                    .andEqualTo(WorkerBankCard.WORKER_ID, member.getId());
            memberDTO.setBankCardCount(iWorkerBankCardMapper.selectCountByExample(example));//银行卡数量
            memberDTO.setDiscountCouponCount(activityRedPackRecordMapper.queryActivityRedCount(member.getId(), null));//优惠券数量(有效的）


            //待付款
            memberDTO.setObligationCount(iOrderMapper.queryDeliverOrderObligation(member.getId(),houseId));

            //待发货
            memberDTO.setDeliverCount(iOrderMapper.queryAppHairOrderList(cityId,houseId,member.getId()));

            //待收货
            memberDTO.setReceiveCount(iOrderMapper.queryAppOrderList(cityId,member.getId(),houseId));

            memberDTO.setSurplusMoney(member.getSurplusMoney());
            return ServerResponse.createBySuccess("查询成功", memberDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
