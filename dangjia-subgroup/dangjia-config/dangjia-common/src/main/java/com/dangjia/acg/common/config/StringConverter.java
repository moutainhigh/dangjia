package com.dangjia.acg.common.config;

import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
public class StringConverter extends StringHttpMessageConverter {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    public StringConverter(Charset defaultCharset) {
        super(defaultCharset);
    }

    @Override
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        super.setSupportedMediaTypes(supportedMediaTypes);
    }
}
