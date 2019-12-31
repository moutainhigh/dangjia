package com.dangjia.acg.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.AES;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.modle.member.AccessToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截器：检查用户是否登录……
 *
 * @author 作者 qiyuxiang
 */
@Component
@Aspect
public class UserToKenAspect {

    @Autowired
    private RedisClient redisClient;

    // 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.*.acg.controller..*(..))")
    public void aspect() {
    }

    // 配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String userToken = request.getParameter(Constants.USER_TOKEY);
        String uuidKey = request.getParameter("uuidKey");
        if(!CommonUtil.isEmpty(uuidKey)){
            try{
                byte[] dec = AES.decrypt(Hex.decode(uuidKey), Constants.DANGJIA_SESSION_KEY.getBytes(), Constants.DANGJIA_IV.getBytes());
                if(!CommonUtil.isEmpty(dec)){
                    JSONObject json = JSON.parseObject(new String(dec));
                    if(!CommonUtil.isEmpty(json.getString(Constants.USER_TOKEY))){
                        userToken = json.getString(Constants.USER_TOKEY);
                    }
                }

            }catch (Exception e){

            }
        }
        if (!CommonUtil.isEmpty(userToken) && !request.getServletPath().equals("/member/login") && !request.getServletPath().equals("/config/adverts/list")) {
            try {
                AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
                if (accessToken == null) {//无效的token
                    return ServerResponse.createbyUserTokenError();
                }
            } catch (Exception e) {
                return ServerResponse.createbyUserTokenError();
            }
        }
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.WRONG_PARAM.getCode(), ServerCode.WRONG_PARAM.getDesc());
        }
        return result;
    }

}
