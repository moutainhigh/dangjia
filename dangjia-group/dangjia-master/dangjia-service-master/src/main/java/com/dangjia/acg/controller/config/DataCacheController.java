package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.DataCacheAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.config.DataCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 前端数据临时缓存
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:19 PM
 */
@RestController
public class DataCacheController implements DataCacheAPI {

    @Autowired
    private DataCacheService dataCacheService;

    @Override
    @ApiMethod
    public ServerResponse addDataCache(HttpServletRequest request, String publicKey, Integer type, String dataJson) {
        return dataCacheService.addDataCache(publicKey, type, dataJson);
    }

    @Override
    @ApiMethod
    public ServerResponse getDataCache(HttpServletRequest request, String publicKey, Integer type) {
        return dataCacheService.getDataCache(publicKey, type);
    }
}
