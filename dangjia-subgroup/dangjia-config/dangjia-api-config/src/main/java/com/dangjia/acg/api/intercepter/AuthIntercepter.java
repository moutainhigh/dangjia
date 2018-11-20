package com.dangjia.acg.api.intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
public class AuthIntercepter implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        requestTemplate.header("feign-sign", new String[]{"123456"});
    }
}
