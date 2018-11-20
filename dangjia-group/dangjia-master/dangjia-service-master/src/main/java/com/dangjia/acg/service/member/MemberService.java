package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.qrcode.QRCodeUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.util.*;
import com.github.pagehelper.PageHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Service
public class MemberService {
	@Autowired
	private ConfigUtil configUtil;

	@Autowired
	private IMemberMapper memberMapper;
	@Autowired
	private ISmsMapper smsMapper;
	@Autowired
	private IWorkerTypeMapper workerTypeMapper;

	/****
	 * 注入配置
	 */
	@Autowired
	private RedisClient redisClient;
	/**
	 * 获取用户详细资料
	 */
	public ServerResponse getMemberInfo(String userToken){
		AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
		if(accessToken==null){//无效的token
			return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册！");
		}else{
			boolean flag;
			try {
				flag = TokenUtil.verifyAccessToken(accessToken.getTimestamp());
			} catch (Exception e) {
				e.printStackTrace();
				return ServerResponse.createByErrorMessage("系统错误");
			}//验证是否失效
			if(flag){//失效
				return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"token已失效,请重新登录！");
			}else{
				Member user = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());
				user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
				accessToken= TokenUtil.generateAccessToken(user);
				if(!StringUtils.isEmpty(user.getWorkerTypeId())) {
					WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
					if (wt != null) {
						accessToken.setWorkerTypeName(wt.getName());
					}
				}
				redisClient.put(accessToken.getUserToken()+ Constants.SESSIONUSERID,accessToken);
				return ServerResponse.createBySuccess("有效！",accessToken);
			}
		}
	}
	// 登录 接口
	public ServerResponse login(String phone, String password, String userRole) {

		//指定角色查询用户
		Member user = new Member();
		user.setMobile(phone);
		user.setPassword(DigestUtils.md5Hex(password));
//		user.setUserRole(userRole);
		user=memberMapper.getUser(user);
		if (user == null) {
			return ServerResponse.createByErrorMessage("电话号码或者密码错误");
		} else {
			user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
			AccessToken accessToken= TokenUtil.generateAccessToken(user);
			if(!StringUtils.isEmpty(user.getWorkerTypeId())) {
				WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
				if (wt != null) {
					accessToken.setWorkerTypeName(wt.getName());
				}
			}
			redisClient.put(accessToken.getUserToken()+ Constants.SESSIONUSERID,accessToken);
			return ServerResponse.createBySuccess("登录成功，正在跳转",accessToken);
		}
	}

	/*
	 * 接口注册获取验证码
	 */
	public ServerResponse registerCode(String phone) {
		if(!Validator.isMobileNo(phone)){
			return ServerResponse.createByErrorMessage("手机号不正确");
		}
		Member user = new Member();
		user.setMobile(phone);
		user=memberMapper.getUser(user);
		if (user != null) {
			return ServerResponse.createByErrorMessage("手机号已被注册");
		} else {
			Integer registerCode=redisClient.getCache(Constants.SMS_CODE+ phone,Integer.class);
			if(registerCode==null||registerCode==0){
				registerCode= (int) (Math.random() * 9000 + 1000);
			}
			redisClient.put(Constants.SMS_CODE+ phone,registerCode);
			JsmsUtil.SMS(registerCode, phone);
			//记录短信发送
			Sms sms=new Sms();
			sms.setCode(String.valueOf(registerCode));
			sms.setMobile(phone);
			smsMapper.insert(sms);
			return ServerResponse.createBySuccessMessage("验证码已发送");
		}
	}

	/**
	 * 校验验证码并保存密码
	 */
	public ServerResponse checkRegister(String phone, int smscode,String password,String invitationCode,Integer userRole) {
		Integer registerCode=redisClient.getCache(Constants.SMS_CODE+ phone,Integer.class);
		if (smscode != registerCode) {
			return ServerResponse.createByErrorMessage("验证码错误");
		} else {
			Member user = new Member();
			user.setMobile(phone);
			user.setPassword(DigestUtils.md5Hex(password));//验证码正确设置密码
            //生成二维码
            if(StringUtils.isEmpty(user.getQrcode())){//二维码为空 生成二维码
                //根据配置文件设置路径
                //图片放项目目录下
				String fileName=new Date().getTime()+".png";
				String visitRoot=configUtil.getValue(SysConfig.PUBLIC_DANGJIA_PATH, String.class)+configUtil.getValue(SysConfig.PUBLIC_QRCODE_PATH, String.class);
				String logoPath = visitRoot+"logo.png";
                String encoderContent =  "/app/app_invite!workRegister.action?memberid="+user.getId();
				try{
					QRCodeUtil.encode(encoderContent, logoPath, visitRoot, fileName, true);
				}catch (Exception e){
					return ServerResponse.createByErrorMessage("二维码生成错误！");
				}
				String imgurl=configUtil.getValue(SysConfig.PUBLIC_QRCODE_PATH, String.class)+"/"+fileName;
				user.setQrcode(imgurl);
            }
			user.setUserRole(0);
			user.setUserName(user.getMobile());
			user.setName(user.getMobile());
			user.setInvitationCode(invitationCode);
			user.setNickName("当家-"+ CommonUtil.randomString(6));
			memberMapper.insertSelective(user);
//			memberMapper.updateByPrimaryKeySelective(user);
			user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
			AccessToken accessToken= TokenUtil.generateAccessToken(user);
			if(!StringUtils.isEmpty(user.getWorkerTypeId())) {
				WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
				if (wt != null) {
					accessToken.setWorkerTypeName(wt.getName());
				}
			}
			redisClient.put(accessToken.getUserToken()+ Constants.SESSIONUSERID,accessToken);
			redisClient.deleteCache(Constants.SMS_CODE+ phone);
			return ServerResponse.createBySuccess("注册成功",accessToken);
		}
	}

	/**
	 * 工匠提交详细资料
	 */
	public ServerResponse updateWokerRegister(Member user, String userToken, String userRole){
//		if(StringUtils.isEmpty(user.getName())){
//			return ServerResponse.createByErrorMessage("名称不能为空");
//		}
//		if(StringUtils.isEmpty(user.getIdnumber())){
//			return ServerResponse.createByErrorMessage("身份证号不能为空");
//		}
		AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
		if(accessToken==null){//无效的token
			return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册！");
		}
		user.setId(accessToken.getMember().getId());
		if(!StringUtils.isEmpty(user.getIdnumber())) {
			String idCard = RKIDCardUtil.getIDCardValidate(user.getIdnumber());
			if (!"".equals(idCard)) {//验证身份证
				return ServerResponse.createByErrorMessage(idCard);
			}
		}
		if(user.getReferrals()!=null&&user.getReferrals().equals(user.getMobile())){
			return ServerResponse.createByErrorMessage("自己不能作为推荐人");
		}
		if(user!=null&&user.getId()!=null){//存在注册信息
            WorkerType wt = workerTypeMapper.selectByPrimaryKey(user.getWorkerTypeId());
			if (wt != null) {
				user.setWorkerTypeId(wt.getId());
				user.setWorkerType(wt.getType());
			}
			user.setVolume(new BigDecimal(0));
			user.setPraiseRate(new BigDecimal(1));
			if(!CommonUtil.isEmpty(userRole)&&Constants.USER_ROLE_GONGJIANG==Integer.parseInt(userRole)) {
				user.setCheckType(0);//提交资料，审核中
			}

			memberMapper.updateByPrimaryKeySelective(user);
			user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
			accessToken= TokenUtil.generateAccessToken(user);
			accessToken.setUserToken(accessToken.getUserToken());
			accessToken.setTimestamp(accessToken.getTimestamp());
            if (wt != null) {accessToken.setWorkerTypeName(wt.getName());}
            redisClient.put(accessToken.getUserToken()+ Constants.SESSIONUSERID,accessToken);
			return ServerResponse.createBySuccessMessage("提交资料成功！");
		}else{
			return ServerResponse.createByErrorMessage("不存在注册信息,请重新注册！");
		}
	}

	/**
	 * 找回密码 获取code
	 * @return
	 * @throws Exception
	 */
	public ServerResponse forgotPasswordCode(String phone) {
		Member user = new Member();
		user.setMobile(phone);
		user=memberMapper.getUser(user);
		if (user == null) {
			return ServerResponse.createByErrorMessage("电话号码未注册！");
		} else {
			int registerCode = (int) (Math.random() * 9000 + 1000);
			user.setSmscode(registerCode);
			memberMapper.updateByPrimaryKeySelective(user);
			JsmsUtil.SMS(registerCode, phone);
			//记录短信发送
			Sms sms=new Sms();
			sms.setCode(String.valueOf(registerCode));
			sms.setMobile(phone);
			smsMapper.insert(sms);
			return ServerResponse.createBySuccessMessage("验证码已发送");
		}
	}

	/**
	 * 找回密码校验验证码
	 * @return
	 * @throws Exception
	 */
	public ServerResponse checkForgotPasswordCode(String phone, int smscode) throws Exception {
		Member user = new Member();
		user.setMobile(phone);
		user.setSmscode(smscode);
		user=memberMapper.getUser(user);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"验证码错误！");
		} else {
			String token=TokenUtil.getRandom();
			redisClient.put(Constants.TEMP_TOKEN+phone ,token);
			return ServerResponse.createBySuccess("验证码正确",token);
		}
	}

	/**
	 * 找回密码更新密码
	 * @return
	 * @throws Exception
	 */
	public ServerResponse updateForgotPassword(String phone, String password,String token) {
		Member user = new Member();
		user.setMobile(phone);
		user=memberMapper.getUser(user);
		if (StringUtils.isEmpty(token)) {
			return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"身份认证错误,无认证参数！");
		}
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"电话号码未注册！");
		} else {
			String mytoken=redisClient.getCache(Constants.TEMP_TOKEN+phone,String.class);
			if (!token.equals(mytoken)) {
				return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"身份认证错误，身份不匹配！");
			}
			//认证通过，清除token认证
			redisClient.deleteCache(Constants.TEMP_TOKEN+phone);
			user.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
			AccessToken accessToken=TokenUtil.generateAccessToken(user);
			user.setPassword(DigestUtils.md5Hex(password));
			user.setSmscode(0);
			memberMapper.updateByPrimaryKeySelective(user);
			redisClient.put(accessToken.getUserToken()+ Constants.SESSIONUSERID,accessToken);
			return ServerResponse.createBySuccessMessage("设置密码成功，正在跳转");
		}
	}


	//根据userToken查询token记录并验证是否失效
	public ServerResponse getAccessTokenByUserToken(String userToken){
		AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);

		if(accessToken==null){//无效的token
			return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册！");
		}else{
			boolean flag;
			try {
				flag = TokenUtil.verifyAccessToken(accessToken.getTimestamp());
			} catch (Exception e) {
				e.printStackTrace();
				return ServerResponse.createByErrorMessage("系统错误");
			}//验证是否失效
			if(flag){//失效
				return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"token已失效,请重新登录！");
			}else{
				return ServerResponse.createBySuccessMessage("有效！");
			}
		}
	}

	/**
	 * 业主列表
	 */
	public ServerResponse getMemberList(HttpServletRequest request,Integer pageNum, Integer pageSize){
		if(pageNum == null){
			pageNum = 1;
		}
		if(pageSize == null){
			pageSize = 10;
		}
		PageHelper.startPage(pageNum, pageSize);
		List<Map<String,Object>> list = memberMapper.getMemberList();
		return ServerResponse.createBySuccess("查询用户列表成功", list);
	}
}
