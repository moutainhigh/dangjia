package com.dangjia.acg.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.AES;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.util.JdbcContextHolder;
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

import java.util.Map;

import static com.dangjia.acg.common.util.AES.decrypt;

/**
 *  拦截器：检查用户是否登录……
 *
 * @author 作者 qiyuxiang
 */
@Component
@Aspect
public class GoodsToKenAspect {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HouseAPI houseAPI;
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
        String cityId = request.getParameter(Constants.CITY_ID);
        //如果获取到了工地ID,则切换至工地所在的城市
        String houseId = request.getParameter("houseId");
        String uuidKey = request.getParameter("uuidKey");
        if(!CommonUtil.isEmpty(uuidKey)){
            try{
                byte[] dec = AES.decrypt(Hex.decode(uuidKey), Constants.DANGJIA_SESSION_KEY.getBytes(), Constants.DANGJIA_IV.getBytes());
                if(!CommonUtil.isEmpty(dec)){
                    JSONObject json = JSON.parseObject(new String(dec));
                    if(!CommonUtil.isEmpty(json.getString("houseId"))){
                        houseId = json.getString("houseId");
                    }
                    if(!CommonUtil.isEmpty(json.getString(Constants.USER_TOKEY))){
                        userToken = json.getString(Constants.USER_TOKEY);
                    }
                    if(!CommonUtil.isEmpty(json.getString(Constants.CITY_ID))){
                        cityId = json.getString(Constants.CITY_ID);
                    }
                }

            }catch (Exception e){

            }
        }
        if(!CommonUtil.isEmpty(userToken)){
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if(accessToken == null){//无效的token
                return ServerResponse.createbyUserTokenError();
            }
        }



        if(!CommonUtil.isEmpty(houseId)) {
            House house = houseAPI.getHouseById(houseId);
            cityId=house.getCityId();
        }
        //跟着工地的 城市切换数据源
        JdbcContextHolder.putDataSource(cityId);
        //拦截切换数据源，当未传输城市ID时，则默认数据源
        if(!CommonUtil.isEmpty(cityId)){
            JdbcContextHolder.putDataSource(cityId);
        }
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.WRONG_PARAM.getCode(),ServerCode.WRONG_PARAM.getDesc());
        }
        return result;
    }

}
