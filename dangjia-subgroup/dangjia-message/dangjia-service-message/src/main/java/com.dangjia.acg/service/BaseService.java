package com.dangjia.acg.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


/**
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Component
public class BaseService {
    @Value("${spring.data.jmessage.zhuangxiu.appkey}")
    private String zx_appkey;

    @Value("${spring.data.jmessage.zhuangxiu.masterSecret}")
    private String zx_masterSecret;

    @Value("${spring.data.jmessage.gongjiang.appkey}")
    private String gj_appkey;

    @Value("${spring.data.jmessage.gongjiang.masterSecret}")
    private String gj_masterSecret;

    @Value("${spring.data.jmessage.sale.appkey}")
    private String sale_appkey;

    @Value("${spring.data.jmessage.sale.masterSecret}")
    private String sale_masterSecret;


    public Map<String, String> map = new HashMap<String, String>();
    @Value("${spring.profiles.active}")
    private String active;

    public String getUserTag(String userid) {
        if (!("pre".equals(active))) {
            return "test_" + userid;
        } else {
            return userid;
        }
    }

    public String[] getUserTags(String[] userids) {
        if (userids != null && userids.length > 0) {
            for (int i = 0; i < userids.length; i++) {
                userids[i] = getUserTag(userids[i]);
            }
        }
        return userids;
    }

    @PostConstruct //加上该注解表明该方法会在bean初始化后调用
    public void init() {
        map.put("zx_appkey", zx_appkey);
        map.put("zx_masterSecret", zx_masterSecret);
        map.put("gj_appkey", gj_appkey);
        map.put("gj_masterSecret", gj_masterSecret);
        map.put("sale_appkey", sale_appkey);
        map.put("sale_masterSecret", sale_masterSecret);
    }

    public String getAppkey(String appType) {
        return map.get(appType + "_appkey");
    }

    public String getMasterSecret(String appType) {
        return map.get(appType + "_masterSecret");
    }

    public final Logger LOG = LoggerFactory.getLogger(BaseService.class);
}

