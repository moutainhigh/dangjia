package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.RecommendConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.recommend.RecommendConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 推荐配置接口类
 * @author: luof
 * @date: 2020-3-9
 */
@RestController
public class RecommendConfigController implements RecommendConfigAPI {

    @Autowired
    private RecommendConfigService recommendConfigService;

    /**
     * @Description: 查询推荐配置列表
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse queryRecommendConfigList() {
        return recommendConfigService.queryList();
    }

    /**
     * @Description: 设置单个推荐配置值
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse updateRecommendConfig(String id, String configCode, Integer configValue) {
        return recommendConfigService.setSingle(id, configCode, configValue);
    }
}
