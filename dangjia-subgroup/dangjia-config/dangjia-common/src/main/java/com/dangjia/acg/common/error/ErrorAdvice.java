package com.dangjia.acg.common.error;


import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.IBaseEnum;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.http.JsonResponse;
import com.dangjia.acg.common.util.AES;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.netflix.client.ClientException;
import feign.codec.DecodeException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.session.ExpiredSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

import static com.dangjia.acg.common.util.AES.encrypt;


/**
 * @author
 */
@ControllerAdvice
public class ErrorAdvice {
  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorAdvice.class);
  private static final ImmutableMap<Class<? extends Throwable>, IBaseEnum> EXCEPTION_MAPPINGS;

  @Value("${spring.application.name}")
  private String platform;
  static {
    final ImmutableMap.Builder<Class<? extends Throwable>, IBaseEnum> builder = ImmutableMap.builder();
    // SpringMVC中参数类型转换异常，常见于String找不到对应的ENUM而抛出的异常
    builder.put(MethodArgumentTypeMismatchException.class, ServerCode.INVALID_PARAMS_CONVERSION);
    builder.put(UnsatisfiedServletRequestParameterException.class, ServerCode.INVALID_PARAMS_CONVERSION);
    // HTTP Request Method不存在
    builder.put(HttpRequestMethodNotSupportedException.class, ServerCode.REQUEST_METHOD_NOT_SUPPORTED);
    // 要求有RequestBody的地方却传入了NULL
    builder.put(HttpMessageNotReadableException.class, ServerCode.HTTP_MESSAGE_NOT_READABLE);
    builder.put(IllegalArgumentException.class, ServerCode.ILLEGAL_ARGUMENT_ERROR);
    builder.put(ClientException.class, ServerCode.SERVER_UPGRADE);
    builder.put(TimeoutException.class,ServerCode.READ_TIMEOUT);
    // 其他未被发现的异常
    builder.put(Exception.class, ServerCode.SERVER_UNKNOWN_ERROR);
    EXCEPTION_MAPPINGS = builder.build();
  }

  /**
   * <strong>Request域取出对应错误信息</strong>, 封装成实体ErrorEntity后转换成JSON输出
   *
   * @param e       {@code StatusCode}异常
   * @param request HttpServletRequest
   * @return ErrorEntity
   */
  @ResponseBody
  @ExceptionHandler(value = {BaseException.class})
  public Object baseException(BaseException e, HttpServletRequest request,HttpServletResponse response)  {

    response.setStatus(HttpStatus.ACCEPTED.value());
    String pm = e.getPlatform() == null ? platform : e.getPlatform();
    Gson gson = new Gson();
    JsonResponse jsonResponse = new JsonResponse(e.getCode(),e.getAttachment() == null ? e.getDesc() : e.getAttachment(),pm);
    String toString = gson.toJson(jsonResponse);
    return  AES.encrypt(toString, Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

  @ResponseBody
  @ExceptionHandler(value = {ClientException.class})
  public Object clientException(ClientException e, HttpServletRequest request,HttpServletResponse response) {

    response.setStatus(HttpStatus.ACCEPTED.value());
    Gson gson = new Gson();
    JsonResponse jsonResponse = new JsonResponse(ServerCode.CLIENT_EXCEPTION.getCode() ,ServerCode.CLIENT_EXCEPTION.getDesc(),platform);
    String toString = gson.toJson(jsonResponse);
    return  encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

  @ResponseBody
  @ExceptionHandler(value = {IllegalArgumentException.class})
  public Object argumentException(IllegalArgumentException e, HttpServletRequest request,HttpServletResponse response) {

    response.setStatus(HttpStatus.ACCEPTED.value());

    Gson gson = new Gson();
    JsonResponse jsonResponse = new JsonResponse(ServerCode.ILLEGAL_ARGUMENT_ERROR.getCode(), e.getMessage(),platform);
    String toString = gson.toJson(jsonResponse);
    return  encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }



  @ResponseBody
  @ExceptionHandler(value = {DecodeException.class})
  public Object decodeException(DecodeException e, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpStatus.ACCEPTED.value());
    Throwable able = e.getCause();
    if(able instanceof BaseException){
      BaseException ex = (BaseException)able;
      String pm = ex.getPlatform() == null ? platform : ex.getPlatform();
      return new JsonResponse(ex.getCode(),ex.getAttachment() == null ? ex.getMessage() : ex.getAttachment(),pm);
    }
    //        else if(able instanceof UnauthenticatedException || able instanceof AuthorizationException){
    //            return JsonResponse();
    //        }
    else {
      LOGGER.error("exception: {}",e);
      Gson gson = new Gson();
      JsonResponse jsonResponse = new JsonResponse(-1,"访问异常,请重新登录");
      String toString = gson.toJson(jsonResponse);
      return encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
    }


  }

  //UnauthenticatedException
  @ResponseBody
  @ExceptionHandler(value = {UnauthenticatedException.class,ExpiredSessionException.class})
  public Object unException(Exception e, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpStatus.ACCEPTED.value());
    Gson gson = new Gson();
    JsonResponse jsonResponse = new JsonResponse(ServerCode.PERMISSION_UNLOGIN.getCode(),"未登录");
    String toString = gson.toJson(jsonResponse);
    return encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

  @ResponseBody
  @ExceptionHandler(value = {AuthorizationException.class})
  public Object authorException(Exception e, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpStatus.ACCEPTED.value());
    JsonResponse jsonResponse = new JsonResponse(ServerCode.PERMISSION_DENIED.getCode(),"授权失败");
    Gson gson = new Gson();
    String toString = gson.toJson(jsonResponse);
    return encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  public Object runtimeException(Exception e, HttpServletRequest request) {
    request.getContextPath();
    request.getMethod();
    request.getRequestURI();
    LOGGER.error("request id: {}\r\nexception: {}", request.getAttribute(""), e);
    IBaseEnum status = null;

    if(e.getCause() != null ){
      status = EXCEPTION_MAPPINGS.get( e.getCause().getClass());
    }else{
      status = EXCEPTION_MAPPINGS.get( e.getClass());
    }

    final JsonResponse error;
    if (status != null) {
      error = new JsonResponse(status.getCode(),status.getDesc(),platform);
    } else {
      error = new JsonResponse(ServerCode.SERVER_UNKNOWN_ERROR.getCode(),ServerCode.SERVER_UNKNOWN_ERROR.getDesc(),platform);
    }
    Gson gson = new Gson();
    String toString = gson.toJson(error);
    return encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  public Object exception(Exception e, HttpServletRequest request) {
    request.getContextPath();
    request.getMethod();
    request.getRequestURI();
    LOGGER.error("request id: {}\r\nexception: {}","d", e);
    final IBaseEnum status = EXCEPTION_MAPPINGS.get(e.getClass());
    final JsonResponse error;
    if (status != null) {
      error = new JsonResponse(status.getCode(),status.getDesc(),platform);
    } else {
      error = new JsonResponse(ServerCode.SERVER_UNKNOWN_ERROR.getCode(),ServerCode.SERVER_UNKNOWN_ERROR.getDesc(),platform);
    }
    Gson gson = new Gson();
    String toString = gson.toJson(error);
    return encrypt(toString,Constants.DANGJIA_SESSION_KEY,Constants.DANGJIA_IV);
  }

}
