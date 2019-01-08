package com.dangjia.acg.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by qiyuxiang
 * Date 2018/1/16
 * Description:配置文件配置项
 */
@Component
public class ConstantProperties implements InitializingBean {

    @Value("${huaWei.obs.endPoint}")
    private String huawei_file_endpoint;

    @Value("${huaWei.obs.keyId}")
    private String huawei_file_keyid;

    @Value("${huaWei.obs.keySecret}")
    private String huawei_file_keysecret;

    @Value("${huaWei.obs.bucketName}")
    private String huawei_file_bucketname;


    public static String HUAWEI_END_POINT;
    public static String HUAWEI_ACCESS_KEY_ID;
    public static String HUAWEI_ACCESS_KEY_SECRET;
    public static String HUAWEI_BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        HUAWEI_END_POINT = huawei_file_endpoint;
        HUAWEI_ACCESS_KEY_ID = huawei_file_keyid;
        HUAWEI_ACCESS_KEY_SECRET = huawei_file_keysecret;
        HUAWEI_BUCKET_NAME = huawei_file_bucketname;
    }
}
