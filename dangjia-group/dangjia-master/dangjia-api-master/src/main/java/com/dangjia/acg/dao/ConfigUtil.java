package com.dangjia.acg.dao;

import com.dangjia.acg.api.ConfigServiceAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.ConfigBean;
import com.dangjia.acg.common.util.ProtoStuffSerializerUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by QiYuXiang on 2017/8/11.
 */
@Component
public class ConfigUtil {

    @Autowired
    private ConfigServiceAPI configServiceAPI;

    @Autowired
    private RedisClient redisClient;

    public <T> T getValue(ConfigBean<T> bean, Class<T> targetClass) {

        String str = redisClient.getCache(Constants.CONFIG_KEY + bean.name, String.class);

        if (null != str) {
            return (T) convert(str, targetClass);
        }

        byte[] byt = configServiceAPI.getValue(bean.name, bean.appType);
        Object val = null;
        if (byt == null) {
            val = bean.defaultValue;
        } else {
            String a = ProtoStuffSerializerUtil.deserialize(byt, String.class);
            val = a;
        }

        return (T) convert(val, targetClass);
    }

    private <T> Object convert(Object obj, Class<T> targetClass) {

        if (targetClass == Integer.class) {
            obj = NumberUtils.toInt(obj.toString());
        } else if (targetClass == Double.class) {
            obj = NumberUtils.toDouble(obj.toString());
        } else if (targetClass == BigDecimal.class) {
            obj = new BigDecimal(obj.toString());
        } else if (targetClass == List.class) {
            obj = Arrays.asList(obj.toString().split(","));
        }

        return obj;
    }

}
