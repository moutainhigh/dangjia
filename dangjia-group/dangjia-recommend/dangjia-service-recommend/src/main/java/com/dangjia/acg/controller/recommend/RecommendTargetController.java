package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.RecommendTargetAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.dangjia.acg.service.recommend.RecommendTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 推荐目标接口类
 * @author: luof
 * @date: 2020-3-9
 */
@RestController
public class RecommendTargetController implements RecommendTargetAPI {

    @Autowired
    private RecommendTargetService recommendTargetService;

    /**
     * @Description: 查询推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse queryRecommendTargetList(String itemSubId, Integer targetType, String targetName) {
        return recommendTargetService.queryList(itemSubId, targetType, targetName);
    }

    /**
     * @Description: 删除单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse deleteRecommendTarget(String id){
        return recommendTargetService.deleteSingle(id);
    }

    /**
     * @Description: 查询单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse singleRecommendTarget(String id){
        return recommendTargetService.querySingle(id);
    }

    /**
     * @Description: 设置单个推荐目标参数
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse updateRecommendTarget(String id, Integer sort, Integer clickNumber){
        return recommendTargetService.setSingle(id, sort, clickNumber);
    }

    /**
     * @Description: 查询可选推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    @Override
    @ApiMethod
    public ServerResponse queryOptionalRecommendTargetList(Integer targetType, String targetName, PageDTO pageDTO) {
        return recommendTargetService.queryOptionalList(targetType, targetName, pageDTO);
    }

    /**
     * @Description: 批量新增推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
//    @Override
//    @ApiMethod
//    public ServerResponse addBatchRecommendTarget(String itemSubId, Integer targetType, ArrayList<RecommendTargetInfo> targetList) {
//        return recommendTargetService.addBatch(itemSubId, targetType, targetList);
//    }
    @Override
    @ApiMethod
    public ServerResponse addRecommendTarget(RecommendTargetInfo target) {
        return recommendTargetService.add(target);
    }
}
