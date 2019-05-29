package com.dangjia.acg.common.response;


import com.dangjia.acg.common.enums.ServerCode.
import com.dangjia.acg.common.exception.ServerCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ruking.Cheng
 * @descrilbe 统一返回类
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/12 上午11:09
 */
//保证序列化json的时候,如果是null的对象,key也会消失
@Data
public class ServerResponse<T> implements Serializable {
    private static final long serialVersionUID = 44321346635165465L;
    private int resultCode;
    private String resultMsg;
    private T resultObj;

    //使之不在json序列化结果当中
    public boolean isSuccess() {
        return this.resultCode == ServerCode.SUCCESS.getCode();
    }

    public static <T> ServerResponse<T> createBySuccess() {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.SUCCESS.getCode());
        dataResponse.setResultMsg(ServerCode.SUCCESS.getDesc());
        return dataResponse;
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String resultMsg) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.SUCCESS.getCode());
        dataResponse.setResultMsg(resultMsg);
        return dataResponse;
    }

    public static <T> ServerResponse<T> createBySuccess(T resultObj) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.SUCCESS.getCode());
        dataResponse.setResultMsg(ServerCode.SUCCESS.getDesc());
        dataResponse.setResultObj(resultObj);
        return dataResponse;
    }

    public static <T> ServerResponse<T> createBySuccess(String resultMsg, T resultObj) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.SUCCESS.getCode());
        dataResponse.setResultMsg(resultMsg);
        dataResponse.setResultObj(resultObj);
        return dataResponse;
    }

    //------错误--------//
    public static <T> ServerResponse<T> createByError() {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.ERROR.getCode());
        dataResponse.setResultMsg(ServerCode.ERROR.getDesc());
        return dataResponse;
    }


    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.ERROR.getCode());
        dataResponse.setResultMsg(errorMessage);
        return dataResponse;
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(errorCode);
        dataResponse.setResultMsg(errorMessage);
        return dataResponse;
    }

    public static <T> ServerResponse<T> createByErrorNeedToPay(T resultObj) {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.NEED_TO_PAY.getCode());
        dataResponse.setResultMsg(ServerCode.NEED_TO_PAY.getDesc());
        dataResponse.setResultObj(resultObj);
        return dataResponse;
    }

    public static <T> ServerResponse<T> createbyUserTokenError() {
        ServerResponse<T> dataResponse = new ServerResponse<>();
        dataResponse.setResultCode(ServerCode.USER_TOKEN_ERROR.getCode());
        dataResponse.setResultMsg(ServerCode.USER_TOKEN_ERROR.getDesc());
        return dataResponse;
    }

}
