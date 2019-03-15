package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.repair.MendMateriel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 16:55
 */
@FeignClient("dangjia-service-master")
@Api(value = "获取商品端精算的所需数据", description = "获取商品端精算的所需数据")
public interface GetForBudgetAPI {

    @PostMapping("/data/getForBudget/askAndQuit")
    @ApiOperation(value = "要退查询补记录", notes = "要退查询补记录")
    List<MendMateriel>  askAndQuit(@RequestParam("workerTypeId") String workerTypeId, @RequestParam("houseId") String houseId,
                                   @RequestParam("categoryId") String categoryId, @RequestParam("name")  String name);

    @PostMapping("/data/getForBudget/getStyleByName")
    @ApiOperation(value = "获取风格", notes = "获取风格")
    HouseStyleType getStyleByName(@RequestParam("style") String style);

    @PostMapping("/data/getForBudget/getFlowList")
    @ApiOperation(value = "根据houseId查出所有工序", notes = "根据houseId查出所有工序")
    List<Map<String, String>> getFlowList(@RequestParam("houseId") String houseId);

    @PostMapping("/data/workerType/actuarialForBudget")
    @ApiOperation(value = "根据参数生成houseFlow", notes = "根据参数生成houseFlow")
    ServerResponse actuarialForBudget(@RequestParam("houseId") String houseId,
                                      @RequestParam("workerTypeId") String workerTypeId);
}
