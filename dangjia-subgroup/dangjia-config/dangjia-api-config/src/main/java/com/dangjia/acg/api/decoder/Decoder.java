package com.dangjia.acg.api.decoder;

import com.google.common.base.Charsets;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.http.JsonResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.reflect.Type;

import feign.Response;
import feign.codec.DecodeException;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
public class Decoder extends SpringDecoder {
    private static Logger logger = LoggerFactory.getLogger(Decoder.class);
    public Decoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }


    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException {
        if (response.status() == HttpStatus.ACCEPTED.value()) {
            String conflictionDetails;

            try {

                conflictionDetails = IOUtils.toString(response.body().asInputStream(), Charsets.UTF_8);
            } catch (IOException e) {
                logger.error("read conflict response body exception. {}", e.toString());
                conflictionDetails = "{}";
            }
            JsonResponse jsonResponse = JSON.parseObject(conflictionDetails, JsonResponse.class);

            if(jsonResponse.getRes() != 0 ) {
                ServerCode code = ServerCode.getInstance(jsonResponse.getRes());

                throw new BaseException(code,jsonResponse.getMsg()== null ? code.getDesc() : String.valueOf(jsonResponse.getMsg()),jsonResponse.getPlatform());
            }
        }

        return super.decode(response, type);
    }
}
