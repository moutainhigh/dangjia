package com.dangjia.acg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.http.JsonResponse;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.SerializeUtils;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * qiyuxiang 2018/3/19
 */
public class ResponseFilter extends SendResponseFilter {

    public ResponseFilter() {
        super();
    }

    @Override
    public String filterType() {
        return super.filterType();
    }

    @Override
    public int filterOrder() {
        return -998;
    }

    @Override
    public boolean shouldFilter() {
        return super.shouldFilter();
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        System.out.printf("状态"+context.getResponse().getStatus());
        if(context.getResponse().getStatus() == HttpStatus.OK.value()) {
            System.out.printf("" + context.getResponse().getHeader("Content-Type"));
            System.out.printf("" + context.getRequest().getContentType());
            System.out.printf("" + context.getRequest().getHeader("Content-Type"));
            InputStream inputStream = context.getResponseDataStream();
            try {


                Object obj = JSON.parseObject(inputStream, Object.class, Feature.AutoCloseSource);
                JsonResponse jsonResponse =null;
                if(obj!=null) {
                    jsonResponse = new JsonResponse(ServerCode.SUCCESS.getCode(), obj);
                }else{
                    ServerResponse serverResponse= ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),EventStatus.NO_DATA.getDesc());
                    jsonResponse = new JsonResponse(ServerCode.NO_DATA.getCode(), serverResponse);
                }
                byte b[] = SerializeUtils.serialize(jsonResponse);
                InputStream stream = new ByteArrayInputStream(b);
                context.setResponseDataStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  }

        InputStream obj =  context.getResponseDataStream();
        super.run();
        return null;
    }



}
