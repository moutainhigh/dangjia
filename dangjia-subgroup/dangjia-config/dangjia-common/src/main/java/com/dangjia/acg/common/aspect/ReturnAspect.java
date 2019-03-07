package com.dangjia.acg.common.aspect;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

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
        log.info("<=============" + name + "方法--AOP 环绕通知=============>");
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
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
        log.info("<=============" + name + "方法--AOP 异常后通知=============>");
        ex.printStackTrace();
        log.info(name + "方法抛出异常为：" + "\t" + ex.getMessage());
    }

}
