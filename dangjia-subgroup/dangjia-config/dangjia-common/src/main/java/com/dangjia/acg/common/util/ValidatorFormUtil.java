package com.dangjia.acg.common.util;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @author Qiyuxiang
 * @date 2018/5/25
 */
public class ValidatorFormUtil {

    /**
     * 获取验证参数是否错误
     * @param bindingResult 验证参数对象
     * @throws  BaseException 参数错误异常
     */
    public static void validParamErrors(BindingResult bindingResult) throws BaseException{
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<ObjectError> errorList = bindingResult.getAllErrors();
            for (ObjectError error : errorList) {
                errorMessage.append(error.getDefaultMessage()).append(",");
            }
            throw new BaseException(ServerCode.WRONG_PARAM, errorMessage.toString());
        }
    }
}
