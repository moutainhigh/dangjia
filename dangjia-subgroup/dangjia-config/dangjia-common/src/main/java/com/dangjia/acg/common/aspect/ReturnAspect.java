package com.dangjia.acg.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.request.ParameterRequestWrapper;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static com.dangjia.acg.common.util.AES.decrypt;

/**
 * 类说明:系统服务组件Aspect切面Bean
 *
 */
@Component
@Aspect
public class ReturnAspect {

    private static final Logger log = Logger.getLogger(ReturnAspect.class);

    // 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.*.acg.controller..*(..))")
    public void aspect() {
    }

    /*
     * 配置前置通知,使用在方法aspect()上注册的切入点 同时接受JoinPoint切入点对象,可以没有该参数
     */
    @Before("aspect()")
    public void before(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();// 获得目标方法的参数
//        String name = joinPoint.getSignature().getName();// 获得目标方法名
//        log.info("<=============" + name + "方法--AOP 前置通知=============>");
//        if (args != null && args.length > 0
//                && args[0].getClass() == ShiroHttpServletRequest.class) {
//            HttpServletRequest request = (HttpServletRequest) joinPoint
//                    .getArgs()[0];
//            String requestURI = request.getRequestURI();
//            Map<String, String[]> parameterMap = request.getParameterMap();
//            StringBuilder paramStr = new StringBuilder();
//            for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
//                paramStr.append(param.getKey()).append("=")
//                        .append(param.getValue());
//            }
//            if (paramStr.length() > 0) {
//                requestURI = requestURI + "?" + paramStr.toString();
//            }
//            log.info(name + " 方法请求路径与参数：ss" + requestURI);
//        }

    }

    public Object[] setRequestParameter(String[] argNames ,Object[] args){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuilder paramStr = new StringBuilder();
            for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                paramStr.append(param.getKey());
            }
            if(CommonUtil.isEmpty(paramStr.toString())){
                return null;
            }
            byte[] dec = decrypt(Hex.decode(paramStr.toString()), Constants.DANGJIA_SESSION_KEY.getBytes(), Constants.DANGJIA_IV.getBytes());
            if(CommonUtil.isEmpty(dec)){
                return null;
            }
            JSONObject json = JSON.parseObject(new String(dec));
            for (int i = 0; i < args.length; i++) {
                if(args[i] instanceof HttpServletRequest){
                    ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, json);
                    args[i]=wrapper;
                }else if (args[i] instanceof BigDecimal ||args[i] instanceof String || args[i] instanceof Date || args[i] instanceof Boolean || args[i] instanceof Byte || args[i] instanceof Short || args[i] instanceof Integer || args[i] instanceof Long || args[i] instanceof Float
                        || args[i] instanceof Double || args[i] instanceof Enum) {
                    if(json.get(argNames[i])!=null){
                        args[i]=json.get(argNames[i]);
                    }
                }else{
                    args[i]= BeanUtils.mapToBean(args[i].getClass(),json);
                }
            }
            return args;
        } catch (Exception e) {
            return null;
        }
    }
    // 配置后置通知,使用在方法aspect()上注册的切入点
    @After("aspect()")
    public void after(JoinPoint joinPoint) {
        if (log.isInfoEnabled()) {
            String name = joinPoint.getSignature().getName();// 获得目标方法名
            log.info("<=============" + name + "方法--AOP 后置通知=============>");
        }
    }

    // 配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();// 获得目标方法名
        Object[] args = joinPoint.getArgs();
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames(); // 参数名
        args= setRequestParameter(argNames,args);
        log.info("<=============" + name + "方法--AOP 环绕通知=============>");
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            if(args!=null){
                result = joinPoint.proceed(args);
            }else{
                result = joinPoint.proceed();
            }

            long end = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                log.info("around " + joinPoint + "\tUse time : "
                        + (end - start) + " ms!");
            }
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                log.error("around " + joinPoint + "\tUse time : "
                        + (end - start) + " ms with exception : "
                        + e.getMessage(),e);
            }
            throw new BaseException(ServerCode.ERROR, "系统繁忙，请稍后再试! "+ServerCode.SERVER_UNKNOWN_ERROR.getCode());
        }
        return result;
    }

    // 配置后置返回通知,使用在方法aspect()上注册的切入点
    @AfterReturning(pointcut = "aspect()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName();// 获得目标方法名
        log.info("<=============" + name + "方法--AOP 后置返回通知=============>");
        log.info(name + "方法返回参数：" + result);
    }

    // 配置抛出异常后通知,使用在方法aspect()上注册的切入点
    @AfterThrowing(pointcut = "aspect()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        String name = joinPoint.getSignature().getName();// 获得目标方法名
        log.error("<=============" + name + "方法--AOP 异常后通知=============>");
        ex.printStackTrace();
        log.error(name + "方法抛出异常为：" + "\t" + ex.getMessage());
    }

}
