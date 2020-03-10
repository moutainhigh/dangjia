package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@Api(description = "推荐目标接口")
@FeignClient("dangjia-service-recommend")
public interface RecommendTargetAPI {

    @PostMapping("/recommend/target/list")
    @ApiOperation(value = "查询推荐目标列表", notes = "")
    ServerResponse queryRecommendTargetList(@RequestParam("itemSubId") String itemSubId,
                                            @RequestParam("targetType") Integer targetType,
                                            @RequestParam("targetName") String targetName);

    @PostMapping("/recommend/target/delete")
    @ApiOperation(value = "删除单个推荐目标", notes = "")
    ServerResponse deleteRecommendTarget(@RequestParam("id") String id);

    @PostMapping("/recommend/target/single")
    @ApiOperation(value = "查询单个推荐目标", notes = "")
    ServerResponse singleRecommendTarget(@RequestParam("id") String id);

    @PostMapping("/recommend/target/update")
    @ApiOperation(value = "设置单个推荐目标参数", notes = "")
    ServerResponse updateRecommendTarget(@RequestParam("id") String id,
                                         @RequestParam("sort") Integer sort,
                                         @RequestParam("clickNumber") Integer clickNumber);

    @PostMapping("/recommend/target/optional/list")
    @ApiOperation(value = "查询可选推荐目标列表", notes = "")
    ServerResponse queryOptionalRecommendTargetList(@RequestParam("Integer") Integer targetType,
                                                    @RequestParam("targetName") String targetName,
                                                    @RequestParam("pageDTO") PageDTO pageDTO);

//    @PostMapping("/recommend/target/addBatch")
//    @ApiOperation(value = "批量新增推荐目标", notes = "")
//    ServerResponse addBatchRecommendTarget(@RequestParam("itemSubId") String itemSubId,
//                                           @RequestParam("targetType") Integer targetType,
//                                           @RequestParam("targetList") ArrayList<RecommendTargetInfo> targetList);

    @PostMapping("/recommend/target/add")
    @ApiOperation(value = "新增推荐目标", notes = "")
    ServerResponse addRecommendTarget(@RequestParam("target") RecommendTargetInfo target);
}
