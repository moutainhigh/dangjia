package com.dangjia.acg.common.http;

import java.io.Serializable;

/**
 * 公共Service的Json返回值类
 *
 * @author QiYuXiang
 */
public class JsonResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3098782250498641298L;

    /***
     * 编码
     */
    public int res;

    /***
     * msg
     */
    public Object msg;

    public String platform;



    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public JsonResponse() {
        this.res = 0;
    }

    public JsonResponse(int res) {
        this.res = res;
    }

    public JsonResponse(Object msg) {
        this.res = 0;
        this.msg = msg;
    }

    public JsonResponse(int res, Object msg) {
        this.res = res;
        this.msg = msg;
    }

    public JsonResponse(int res, Object msg, String platform) {
        this.res = res;
        this.msg = msg;
        this.platform = platform;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }


}
