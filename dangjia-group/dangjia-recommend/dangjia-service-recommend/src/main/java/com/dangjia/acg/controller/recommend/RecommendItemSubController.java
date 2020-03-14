package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.RecommendItemSubAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.recommend.RecommendItemSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 推荐参考子项接口类
 * @author: luof
 * @date: 2020-3-9
 */
@RestController
public class RecommendItemSubController implements RecommendItemSubAPI {

    @Autowired
    private RecommendItemSubService recommendItemSubService;

    /**
     * @Description: 查询推荐参考子项列表
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse queryRecommendItemSubList(String itemId, String itemSubName) {
        return recommendItemSubService.queryList(itemId, itemSubName);
    }
}
