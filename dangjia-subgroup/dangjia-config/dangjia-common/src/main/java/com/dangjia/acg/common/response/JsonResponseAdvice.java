package com.dangjia.acg.common.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.http.JsonResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.dangjia.acg.common.util.AES.decrypt;
import static com.dangjia.acg.common.util.AES.encrypt;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
@ControllerAdvice
public class JsonResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        ApiMethod api = methodParameter.getMethodAnnotation(ApiMethod.class);
        if (null != api) {
            String header = request.getHeader("feign-sign");
            if (request.getAttribute("information") != null && !"".equals(request.getAttribute("information"))) {
                return o;
            }
            if (StringUtils.isEmpty(header)) {
                try {
                    String isShow=null;
                    if(!CommonUtil.isEmpty(request.getParameter("uuidKey"))){
                        byte[] dec = decrypt(Hex.decode(request.getParameter("uuidKey")), Constants.DANGJIA_SESSION_KEY.getBytes(), Constants.DANGJIA_IV.getBytes());
                        if(!CommonUtil.isEmpty(dec)){
                            JSONObject json = JSON.parseObject(new String(dec));
                            isShow=json.getString("isShow");
                        }
                    }else {
                        isShow=CommonUtil.isEmpty(request.getAttribute("isShow"))?request.getParameter("isShow"):(String)request.getAttribute("isShow");
                    }
                    Gson gson = new Gson();
                    if (o != null) {
                        o = BeanUtils.beanToMap(o,isShow);
                    }
                    JsonResponse jsonResponse = new JsonResponse(ServerCode.SUCCESS.getCode(), o);
                    String toString = gson.toJson(jsonResponse);

                    return encrypt(toString, Constants.DANGJIA_SESSION_KEY, Constants.DANGJIA_IV);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return o;
    }
}
