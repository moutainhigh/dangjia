package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户信息
 */
@FeignClient("dangjia-service-master")
@Api(value = "用户信息接口", description = "用户信息接口")
public interface MemberAPI {

    /**
     * showdoc
     *
     * @param id       必选 string 来源ID
     * @param idType   必选 string 来源类型：1=房屋ID, 2=用户ID
     * @param userRole 必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":"13788885555","resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 获取用户手机资料
     * @description 获取用户手机资料
     * @method POST
     * @url master/member/mobile
     * @return_param resultObj string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:28 PM
     */
    @RequestMapping(value = "member/mobile", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户手机资料", notes = "获取用户手机资料")
    ServerResponse getMemberMobile(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id,
                                   @RequestParam("idType") String idType);


    @RequestMapping(value = "member/sms", method = RequestMethod.GET)
    @ApiOperation(value = "查询验证码", notes = "查询验证码")
    ServerResponse getSmsCode(@RequestParam("phone") String phone);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param userRole  必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 获取用户详细资料
     * @description 获取用户详细资料
     * @method POST
     * @url master/member/info
     * @return_param userId string 用户id
     * @return_param phone string 用户电话
     * @return_param userType string 工匠类型(0:大管家；1：普通工匠;2:业主)
     * @return_param workerTypeName string 工匠类型名称
     * @return_param timestamp string 时间戳
     * @return_param userToken string userToken
     * @return_param member Object 用户信息
     * @return_param member_id string ID
     * @return_param member_userName string 用户名
     * @return_param member_password string 密码
     * @return_param member_nickName string 昵称
     * @return_param member_name string 真实姓名
     * @return_param member_mobile string 手机
     * @return_param member_qrcode string 二维码
     * @return_param member_superiorId string 上级用户id
     * @return_param member_inviteNum int 存放邀约人数
     * @return_param member_gift int 礼品领取 1为已领取 2为领奖时间过期
     * @return_param member_invitationCode string 自己的邀请码
     * @return_param member_othersInvitationCode string 他人邀请码
     * @return_param member_userRole int 用户角色 1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return_param member_head string 头像
     * @return_param member_idcaoda string 身份证正面
     * @return_param member_idcaodb string 身份证反面
     * @return_param member_idcaodall string 半身照
     * @return_param member_workerTypeId string 工种类型的id
     * @return_param member_workerType int 工种类型：【1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装】
     * @return_param member_idnumber string 身份证号
     * @return_param member_workerPrice BigDecimal 总工钱(所有订单工钱整体完工相加)
     * @return_param member_haveMoney BigDecimal 已获钱,可取余额加上押金
     * @return_param member_surplusMoney BigDecimal 可取余额,已获钱减押金
     * @return_param member_retentionMoney BigDecimal 实际滞留金
     * @return_param member_deposit BigDecimal 保证金，初始默认最多押金100000元,以后根据工匠等级设计
     * @return_param member_checkType int 审核状态 0审核中，1审核未通过不能抢单不能发申请, 2审核已通过 可抢单可发申请, 3账户已禁用 不能抢单不能发申请, 4账户冻结可发申请 不能抢单,5未提交资料
     * @return_param member_realNameState int 实名认证状态:0:未提交，1:认证中，2:认证被驳回，3:认证通过
     * @return_param member_praiseRate BigDecimal 好评率
     * @return_param member_volume BigDecimal 成交量
     * @return_param member_evaluationScore BigDecimal 评价积分,70分以下押金按每单总额百分之五收取,铜牌工匠70.1-80上限为2000元,银牌工匠80.1-90上限为1500元,金牌工匠90.1~上限为500元
     * @return_param member_isCrowned int 是否是皇冠，0不是，1是
     * @return_param member_smscode int 验证码
     * @return_param member_paycode int 提现验证码
     * @return_param member_referrals string 推荐人ID
     * @return_param member_workyears string 工作年限
     * @return_param member_nativeplace string 籍贯
     * @return_param member_address string 现居地址
     * @return_param member_selfAssessment string 自我评价
     * @return_param member_specialty string 擅长工作
     * @return_param member_checkDescribe string 工匠审核描述
     * @return_param member_realNameDescribe string 实名认证描述
     * @return_param member_createDate Date 创建时间
     * @return_param member_modifyDate Date 修改时间
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:33 PM
     */
    @RequestMapping(value = "member/info", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户详细资料", notes = "获取用户详细资料")
    ServerResponse getMemberInfo(@RequestParam("userToken") String userToken,
                                 @RequestParam("userRole") Integer userRole);

    /**
     * showdoc
     *
     * @param phone    必选 string 用户名
     * @param password 必选 string 密码
     * @param userRole 必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 用户登录
     * @description 用户登录
     * @method POST
     * @url master/member/login
     * @return_param userId string 用户id
     * @return_param phone string 用户电话
     * @return_param userType string 工匠类型(0:大管家；1：普通工匠;2:业主)
     * @return_param workerTypeName string 工匠类型名称
     * @return_param timestamp string 时间戳
     * @return_param userToken string userToken
     * @return_param member Object 用户信息
     * @return_param member_id string ID
     * @return_param member_userName string 用户名
     * @return_param member_password string 密码
     * @return_param member_nickName string 昵称
     * @return_param member_name string 真实姓名
     * @return_param member_mobile string 手机
     * @return_param member_qrcode string 二维码
     * @return_param member_superiorId string 上级用户id
     * @return_param member_inviteNum int 存放邀约人数
     * @return_param member_gift int 礼品领取 1为已领取 2为领奖时间过期
     * @return_param member_invitationCode string 自己的邀请码
     * @return_param member_othersInvitationCode string 他人邀请码
     * @return_param member_userRole int 用户角色 1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return_param member_head string 头像
     * @return_param member_idcaoda string 身份证正面
     * @return_param member_idcaodb string 身份证反面
     * @return_param member_idcaodall string 半身照
     * @return_param member_workerTypeId string 工种类型的id
     * @return_param member_workerType int 工种类型：【1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装】
     * @return_param member_idnumber string 身份证号
     * @return_param member_workerPrice BigDecimal 总工钱(所有订单工钱整体完工相加)
     * @return_param member_haveMoney BigDecimal 已获钱,可取余额加上押金
     * @return_param member_surplusMoney BigDecimal 可取余额,已获钱减押金
     * @return_param member_retentionMoney BigDecimal 实际滞留金
     * @return_param member_deposit BigDecimal 保证金，初始默认最多押金100000元,以后根据工匠等级设计
     * @return_param member_checkType int 审核状态 0审核中，1审核未通过不能抢单不能发申请, 2审核已通过 可抢单可发申请, 3账户已禁用 不能抢单不能发申请, 4账户冻结可发申请 不能抢单,5未提交资料
     * @return_param member_realNameState int 实名认证状态:0:未提交，1:认证中，2:认证被驳回，3:认证通过
     * @return_param member_praiseRate BigDecimal 好评率
     * @return_param member_volume BigDecimal 成交量
     * @return_param member_evaluationScore BigDecimal 评价积分,70分以下押金按每单总额百分之五收取,铜牌工匠70.1-80上限为2000元,银牌工匠80.1-90上限为1500元,金牌工匠90.1~上限为500元
     * @return_param member_isCrowned int 是否是皇冠，0不是，1是
     * @return_param member_smscode int 验证码
     * @return_param member_paycode int 提现验证码
     * @return_param member_referrals string 推荐人ID
     * @return_param member_workyears string 工作年限
     * @return_param member_nativeplace string 籍贯
     * @return_param member_address string 现居地址
     * @return_param member_selfAssessment string 自我评价
     * @return_param member_specialty string 擅长工作
     * @return_param member_checkDescribe string 工匠审核描述
     * @return_param member_realNameDescribe string 实名认证描述
     * @return_param member_createDate Date 创建时间
     * @return_param member_modifyDate Date 修改时间
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:34 PM
     */
    @RequestMapping(value = "member/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录", notes = "用户登录")
    ServerResponse login(@RequestParam("phone") String phone,
                         @RequestParam("password") String password,
                         @RequestParam("userRole") Integer userRole);

    /**
     * showdoc
     *
     * @param phone 必选 string 手机号
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 注册获取验证码
     * @description 注册获取验证码
     * @method POST
     * @url master/member/registerCode
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:36 PM
     */
    @RequestMapping(value = "member/registerCode", method = RequestMethod.POST)
    @ApiOperation(value = "注册获取验证码", notes = "注册获取验证码")
    ServerResponse registerCode(@RequestParam("phone") String phone);

    /**
     * showdoc
     *
     * @param phone          必选 string 手机号
     * @param password       必选 string 密码
     * @param smscode        必选 string 验证码Code
     * @param invitationCode 必选 string 邀请码
     * @param userRole       必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 用户注册
     * @description 用户注册
     * @method POST
     * @url master/member/checkRegister
     * @return_param userId string 用户id
     * @return_param phone string 用户电话
     * @return_param userType string 工匠类型(0:大管家；1：普通工匠;2:业主)
     * @return_param workerTypeName string 工匠类型名称
     * @return_param timestamp string 时间戳
     * @return_param userToken string userToken
     * @return_param member Object 用户信息
     * @return_param member_id string ID
     * @return_param member_userName string 用户名
     * @return_param member_password string 密码
     * @return_param member_nickName string 昵称
     * @return_param member_name string 真实姓名
     * @return_param member_mobile string 手机
     * @return_param member_qrcode string 二维码
     * @return_param member_superiorId string 上级用户id
     * @return_param member_inviteNum int 存放邀约人数
     * @return_param member_gift int 礼品领取 1为已领取 2为领奖时间过期
     * @return_param member_invitationCode string 自己的邀请码
     * @return_param member_othersInvitationCode string 他人邀请码
     * @return_param member_userRole int 用户角色 1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
     * @return_param member_head string 头像
     * @return_param member_idcaoda string 身份证正面
     * @return_param member_idcaodb string 身份证反面
     * @return_param member_idcaodall string 半身照
     * @return_param member_workerTypeId string 工种类型的id
     * @return_param member_workerType int 工种类型：【1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装】
     * @return_param member_idnumber string 身份证号
     * @return_param member_workerPrice BigDecimal 总工钱(所有订单工钱整体完工相加)
     * @return_param member_haveMoney BigDecimal 已获钱,可取余额加上押金
     * @return_param member_surplusMoney BigDecimal 可取余额,已获钱减押金
     * @return_param member_retentionMoney BigDecimal 实际滞留金
     * @return_param member_deposit BigDecimal 保证金，初始默认最多押金100000元,以后根据工匠等级设计
     * @return_param member_checkType int 审核状态 0审核中，1审核未通过不能抢单不能发申请, 2审核已通过 可抢单可发申请, 3账户已禁用 不能抢单不能发申请, 4账户冻结可发申请 不能抢单,5未提交资料
     * @return_param member_realNameState int 实名认证状态:0:未提交，1:认证中，2:认证被驳回，3:认证通过
     * @return_param member_praiseRate BigDecimal 好评率
     * @return_param member_volume BigDecimal 成交量
     * @return_param member_evaluationScore BigDecimal 评价积分,70分以下押金按每单总额百分之五收取,铜牌工匠70.1-80上限为2000元,银牌工匠80.1-90上限为1500元,金牌工匠90.1~上限为500元
     * @return_param member_isCrowned int 是否是皇冠，0不是，1是
     * @return_param member_smscode int 验证码
     * @return_param member_paycode int 提现验证码
     * @return_param member_referrals string 推荐人ID
     * @return_param member_workyears string 工作年限
     * @return_param member_nativeplace string 籍贯
     * @return_param member_address string 现居地址
     * @return_param member_selfAssessment string 自我评价
     * @return_param member_specialty string 擅长工作
     * @return_param member_checkDescribe string 工匠审核描述
     * @return_param member_realNameDescribe string 实名认证描述
     * @return_param member_createDate Date 创建时间
     * @return_param member_modifyDate Date 修改时间
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:38 PM
     */
    @RequestMapping(value = "member/checkRegister", method = RequestMethod.POST)
    @ApiOperation(value = "用户注册", notes = "用户注册")
    ServerResponse checkRegister(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("password") String password,
                                 @RequestParam("smscode") int smscode,
                                 @RequestParam("invitationCode") String invitationCode,
                                 @RequestParam("userRole") Integer userRole,
                                 @RequestParam("longitude") String longitude,
                                 @RequestParam("latitude") String latitude);

    /**
     * showdoc
     *
     * @param userToken      必选 string 用户Token
     * @param mobile         可选 string 手机
     * @param nickName       可选 string 昵称
     * @param name           可选 string 真实姓名
     * @param head           可选 string 头像
     * @param idcaoda        可选 string 身份证正面
     * @param idcaodb        可选 string 身份证反面
     * @param idcaodall      可选 string 半身照
     * @param workerTypeId   可选 string 工种类型的id
     * @param idnumber       可选 string 身份证号
     * @param referrals      可选 String 推荐人ID
     * @param nativeplace    可选 String 籍贯
     * @param address        可选 String 现居地址
     * @param selfAssessment 可选 String 自我评价
     * @param specialty      可选 String 擅长工作
     * @param userRole       必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 工匠提交详细资料
     * @description 工匠提交详细资料
     * @method POST
     * @url master/member/updateWokerRegister
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:50 PM
     */
    @RequestMapping(value = "member/updateWokerRegister", method = RequestMethod.POST)
    @ApiOperation(value = "工匠提交详细资料", notes = "工匠提交详细资料")
    ServerResponse updateWokerRegister(@RequestParam("member") Member user,
                                       @RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param name      必选 string 真实姓名
     * @param idcaoda   必选 string 身份证正面
     * @param idcaodb   必选 string 身份证反面
     * @param idcaodall 必选 string 半身照
     * @param idnumber  必选 string 身份证号
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 实名认证提交资料
     * @description 实名认证提交资料
     * @method POST
     * @url master/member/certification
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:55 PM
     */
    @RequestMapping(value = "member/certification", method = RequestMethod.POST)
    @ApiOperation(value = "实名认证提交资料", notes = "实名认证提交资料")
    ServerResponse certification(@RequestParam("userToken") String userToken,
                                 @RequestParam("name") String name,
                                 @RequestParam("idcaoda") String idcaoda,
                                 @RequestParam("idcaodb") String idcaodb,
                                 @RequestParam("idcaodall") String idcaodall,
                                 @RequestParam("idnumber") String idnumber);

    /**
     * showdoc
     *
     * @param userToken    必选/可选 string 用户Token
     * @param workerTypeId 必选/可选 string 工种类型的id
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 工种认证提交申请
     * @description 工种认证提交申请
     * @method POST
     * @url master/member/certificationWorkerType
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 8
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:57 PM
     */
    @RequestMapping(value = "member/certificationWorkerType", method = RequestMethod.POST)
    @ApiOperation(value = "工种认证提交申请", notes = "工种认证提交申请")
    ServerResponse certificationWorkerType(@RequestParam("userToken") String userToken,
                                           @RequestParam("workerTypeId") String workerTypeId);

    /**
     * showdoc
     *
     * @param phone 必选 string 手机号
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 找回密码获取验证码
     * @description 找回密码获取验证码
     * @method POST
     * @url master/member/forgotPasswordCode
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 9
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 4:00 PM
     */
    @RequestMapping(value = "member/forgotPasswordCode", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码获取验证码", notes = "找回密码获取验证码")
    ServerResponse forgotPasswordCode(@RequestParam("phone") String phone);

    /**
     * showdoc
     *
     * @param phone   必选string 手机号
     * @param smscode 必选 string 验证码
     * @return {"res":1000,"msg":{"resultObj":"token","resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 找回密码校验验证码
     * @description 找回密码校验验证码
     * @method POST
     * @url master/member/checkForgotPasswordCode
     * @return_param resultObj string 临时Token
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 10
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 4:03 PM
     */
    @RequestMapping(value = "member/checkForgotPasswordCode", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码校验验证码", notes = "找回密码校验验证码")
    ServerResponse checkForgotPasswordCode(@RequestParam("phone") String phone,
                                           @RequestParam("smscode") int smscode);

    /**
     * showdoc
     *
     * @param phone    必选 string 手机号
     * @param password 必选 string 密码
     * @param token    必选 string checkForgotPasswordCode返回的Token
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 找回密码更新密码
     * @description 找回密码更新密码
     * @method POST
     * @url master/member/updateForgotPassword
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 11
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 4:06 PM
     */
    @RequestMapping(value = "member/updateForgotPassword", method = RequestMethod.POST)
    @ApiOperation(value = "找回密码更新密码", notes = "找回密码更新密码")
    ServerResponse updateForgotPassword(@RequestParam("phone") String phone,
                                        @RequestParam("password") String password,
                                        @RequestParam("token") String token);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param userRole  必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 我的邀请码
     * @description 我的邀请码
     * @method POST
     * @url master/member/getMyInvitation
     * @return_param invitationCode string 邀请码
     * @return_param invitationNum int 已邀请数
     * @return_param id string 用户ID
     * @return_param codeData string 二维码内容（目前只有销售端有）
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 12
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 4:11 PM
     */
    @RequestMapping(value = "member/getMyInvitation", method = RequestMethod.POST)
    @ApiOperation(value = "我的邀请码", notes = "我的邀请码")
    ServerResponse getMyInvitation(@RequestParam("userToken") String userToken,
                                   @RequestParam("userRole") Integer userRole);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param memberId  可选 string 用户ID,memberId/phone必须有一个有值
     * @param phone     可选 string 用户手机号,memberId/phone必须有一个有值
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 通过手机号/ID获取用户信息
     * @description 通过手机号/ID获取用户信息
     * @method POST
     * @url master/member/getMembers
     * @return_param memberType int 0业主，1工匠，2销售
     * @return_param id string ID
     * @return_param nickName string 昵称
     * @return_param name string 昵称
     * @return_param mobile string 手机
     * @return_param head string 头像
     * @return_param workerTypeId string 工种类型的id
     * @return_param workerName string 工种类型的名称
     * @return_param appKey string appKey
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 13
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 4:14 PM
     */
    @RequestMapping(value = "member/getMembers", method = RequestMethod.POST)
    @ApiOperation(value = "通过手机号/ID获取用户信息", notes = "通过手机号/ID获取用户信息")
    ServerResponse getMembers(@RequestParam("userToken") String userToken,
                              @RequestParam("memberId") String memberId,
                              @RequestParam("phone") String phone);


    /**
     * showdoc
     *
     * @param userToken 可选 string userToken
     * @param userRole  必选 string 1为业主应用，2为工匠应用，3为销售应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/用户信息
     * @title 获取我的界面
     * @description 获取我的界面
     * @method POST
     * @url master/app/core/houseWorker/getMyHomePage
     * @return_param evaluation BigDecimal 积分
     * @return_param favorable string 好评率
     * @return_param gradeName string 工匠等级别称
     * @return_param ioflow string 工匠头像
     * @return_param workerId string 工匠ID
     * @return_param workerName string 工匠名称
     * @return_param list List<ListBean> 菜单
     * @return_param list_imageUrl string 菜单图片地址
     * @return_param list_name string 菜单名称
     * @return_param list_url string 点击URL
     * @return_param list_type int 0:跳转URL，1:获取定位后跳转URL，2:量房，3：传平面图，4：传施工图，5：跳转我的钱包，6：跳转我的优惠券
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 14
     * @Author: Ruking 18075121944
     * @Date: 2019/8/7 10:26 AM
     */
    @PostMapping("app/core/houseWorker/getMyHomePage")
    @ApiOperation(value = "获取我的界面", notes = "获取我的界面")
    ServerResponse getMyHomePage(@RequestParam("userToken") String userToken,
                                 @RequestParam("userRole") Integer userRole);

    @RequestMapping(value = "member/getMember", method = RequestMethod.POST)
    @ApiOperation(value = "其他项目获取登录信息", notes = "其他项目获取登录信息")
    Object getMember(@RequestParam("userToken") String userToken);

    @RequestMapping(value = "member/insurances/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户保险单信息", notes = "添加用户保险单信息")
    ServerResponse  addInsurances(String userToken);

    @RequestMapping(value = "member/insurances/my", method = RequestMethod.POST)
    @ApiOperation(value = "我的保险单信息", notes = "我的保险单信息")
    ServerResponse  myInsurances(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("pageDTO") PageDTO pageDTO);

    @RequestMapping(value = "member/insurances/promotionList")
    @ApiOperation(value = "推广列表", notes = "推广列表")
    ServerResponse  promotionList(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("pageDTO") PageDTO pageDTO);
}

