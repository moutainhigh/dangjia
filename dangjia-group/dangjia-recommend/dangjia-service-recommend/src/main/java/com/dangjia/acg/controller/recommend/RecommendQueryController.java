package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.RecommendQueryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.recommend.RecommendQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 推荐查询类
 * @author: luof
 * @date: 2020-3-10
 */
@RestController
public class RecommendQueryController implements RecommendQueryAPI {

    @Autowired
    private RecommendQueryService recommendQueryService;

    /**
     * @Description: 推荐查询
     * @author: luof
     * @date: 2020-3-10
     */
    @Override
    @ApiMethod
    public ServerResponse queryRecommendPage(String userToken, PageDTO pageDTO) {
        return recommendQueryService.queryRecommend(userToken, pageDTO);
    }
}
