package com.dangjia.acg.controller;

import com.dangjia.acg.api.ConfigServiceAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by QiYuXiang on 2017/8/3.
 */
@RestController
public class ConfigController implements ConfigServiceAPI {

    /****
     * 注入系统配置service
     */
    @Autowired
    private ConfigService configService;

    @Override
    @ApiMethod
    public byte[] getValue( String name ,Integer appType) {
        return configService.getValue(name,appType);
    }

    @Override
    @ApiMethod
    public void updateValue(@RequestParam("name") String name, @RequestParam("appType") Integer appType, @RequestParam("value") String value) {
        configService.updateValue(name, appType, value);
    }

}
