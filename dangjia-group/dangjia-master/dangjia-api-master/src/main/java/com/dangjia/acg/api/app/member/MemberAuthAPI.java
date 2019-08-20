package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.MemberAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "当家用户第三方认证接口", description = "当家用户第三方认证接口")
public interface MemberAuthAPI {

    /**
     * showdoc
     *
     * @param openType 必选/可选 string 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param unionid  必选 string 第三方认证ID
     * @param userRole 必选 string app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 当家用户第三方认证登录
     * @description 当家用户第三方认证登录
     * @method POST
     * @url master/memberAuth/authLogin
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
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:08 PM
     */
    @RequestMapping(value = "memberAuth/authLogin", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户第三方认证登录", notes = "当家用户第三方认证登录")
    ServerResponse authLogin(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("openType") Integer openType,
                             @RequestParam("unionid") String unionid,
                             @RequestParam("userRole") Integer userRole);

    /**
     * showdoc
     *
     * @param phone       必选 String 手机号
     * @param password    必选 String 密码
     * @param openType    必选 Integer 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param openid      必选 string 第三方认证ID
     * @param accessToken 必选 string 第三方接口调用凭证
     * @param unionid     必选 string 只有微信在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param uid         必选 string 第三方uid
     * @param name        必选 string 第三方所用的昵称
     * @param iconurl     必选 string 第三方所使用的头像
     * @param userRole    必选 integer app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 第三方认证登录绑定当家老用户
     * @description 第三方认证登录绑定当家老用户
     * @method POST
     * @url master/memberAuth/oldUserBinding
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
     * @Date: 2019/6/24 3:13 PM
     */
    @RequestMapping(value = "memberAuth/oldUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家老用户", notes = "第三方认证登录绑定当家老用户")
    ServerResponse oldUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("memberAuth") MemberAuth memberAuth);

    /**
     * showdoc
     *
     * @param phone          必选 String 手机号
     * @param password       必选 String 密码
     * @param smscode        必选 int 验证码Code
     * @param invitationCode 可选 String 邀请码
     * @param openType       必选 Integer 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param openid         必选 string 第三方认证ID
     * @param accessToken    必选 string 第三方接口调用凭证
     * @param unionid        必选 string 只有微信在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param uid            必选 string 第三方uid
     * @param name           必选 string 第三方所用的昵称
     * @param iconurl        必选 string 第三方所使用的头像
     * @param userRole       可选 integer app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 第三方认证登录绑定当家新用户
     * @description 第三方认证登录绑定当家新用户
     * @method POST
     * @url master/memberAuth/newUserBinding
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
     * @Date: 2019/6/24 3:16 PM
     */
    @RequestMapping(value = "memberAuth/newUserBinding", method = RequestMethod.POST)
    @ApiOperation(value = "第三方认证登录绑定当家新用户", notes = "第三方认证登录绑定当家新用户")
    ServerResponse newUserBinding(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password") String password,
                                  @RequestParam("smscode") int smscode,
                                  @RequestParam("invitationCode") String invitationCode,
                                  @RequestParam("memberAuth") MemberAuth memberAuth,
                                  @RequestParam("longitude") String longitude,
                                  @RequestParam("latitude") String latitude);

    /**
     * showdoc
     *
     * @param userToken   必选 String userToken
     * @param openType    必选 Integer 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param openid      必选 string 第三方认证ID
     * @param accessToken 必选 string 第三方接口调用凭证
     * @param unionid     必选 string 只有微信在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param uid         必选 string 第三方uid
     * @param name        必选 string 第三方所用的昵称
     * @param iconurl     必选 string 第三方所使用的头像
     * @param userRole    可选 integer app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 当家用户绑定第三方认证
     * @description 当家用户绑定第三方认证
     * @method POST
     * @url master/memberAuth/bindingThirdParties
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:19 PM
     */
    @RequestMapping(value = "memberAuth/bindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户绑定第三方认证", notes = "当家用户绑定第三方认证")
    ServerResponse bindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("memberAuth") MemberAuth memberAuth);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param openType  必选 Integer 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param password  必选 string 密码
     * @param userRole  可选 Integer app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 当家用户取消绑定第三方认证
     * @description 当家用户取消绑定第三方认证
     * @method POST
     * @url master/memberAuth/cancelBindingThirdParties
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:22 PM
     */
    @RequestMapping(value = "memberAuth/cancelBindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户取消绑定第三方认证", notes = "当家用户取消绑定第三方认证")
    ServerResponse cancelBindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("userToken") String userToken,
                                             @RequestParam("openType") Integer openType,
                                             @RequestParam("password") String password,
                                             @RequestParam("userRole") Integer userRole);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param openType  必选 Integer 认证类型 1:微信，2：QQ，3:新浪，4:支付宝
     * @param userRole  可选 Integer app应用 1为业主应用，2为工匠应用
     * @return {"res":1000,"msg":{"resultObj":0/1,"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/用户模块/第三方认证
     * @title 当家用户判断是否绑定第三方认证
     * @description 当家用户判断是否绑定第三方认证
     * @method POST
     * @url master/memberAuth/isBindingThirdParties
     * @return_param resultObj int 0:未绑定，1:已经绑定
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 6
     * @Author: Ruking 18075121944
     * @Date: 2019/6/24 3:24 PM
     */
    @RequestMapping(value = "memberAuth/isBindingThirdParties", method = RequestMethod.POST)
    @ApiOperation(value = "当家用户判断是否绑定第三方认证", notes = "当家用户判断是否绑定第三方认证")
    ServerResponse isBindingThirdParties(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("userToken") String userToken,
                                         @RequestParam("openType") Integer openType,
                                         @RequestParam("userRole") Integer userRole);


}

