package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.RecommendItemAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.recommend.RecommendItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 推荐参考主项接口类
 * @author: luof
 * @date: 2020-3-7
 */
@RestController
public class RecommendItemController implements RecommendItemAPI {

    @Autowired
    private RecommendItemService recommendItemService;

    /**
     * @Description: 查询推荐参考主项列表
     * @author: luof
     * @date: 2020-3-7
     */
    @Override
    @ApiMethod
    public ServerResponse queryRecommendItemList(String itemName) {
        return recommendItemService.queryList(itemName);
    }
}
