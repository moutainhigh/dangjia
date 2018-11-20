package com.dangjia.acg.api.encoder;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;

import java.lang.reflect.Type;

import feign.RequestTemplate;
import feign.codec.EncodeException;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
public class Encoder extends SpringEncoder {
    public Encoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }

    @Override
    public void encode(Object requestBody, Type bodyType, RequestTemplate request) throws EncodeException {
        request.header("feign-sign","123456");
        super.encode(requestBody, bodyType, request);
    }

}
