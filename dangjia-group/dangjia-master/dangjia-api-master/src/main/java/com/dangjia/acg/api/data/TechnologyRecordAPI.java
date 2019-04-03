package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.Warehouse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/2 0002
 * Time: 17:24
 */
@FeignClient("dangjia-service-master")
@Api(value = "验收工艺节点相关", description = "验收工艺节点相关")
public interface TechnologyRecordAPI {

    @PostMapping("/data/technologyRecord/nodeImageList")
    @ApiOperation(value = "节点图片列表", notes = "节点图片列表")
    ServerResponse nodeImageList(@RequestParam("userToken") String userToken,@RequestParam("nodeArr")String nodeArr,
                                 @RequestParam("applyType")Integer applyType);

    @PostMapping("/data/technologyRecord/workNodeList")
    @ApiOperation(value = "工匠今日完工节点列表", notes = "工匠今日完工节点列表")
    ServerResponse workNodeList(@RequestParam("userToken") String userToken, @RequestParam("houseFlowId") String houseFlowId
                                    ,@RequestParam("applyType")Integer applyType);

    @PostMapping("/data/technologyRecord/uploadingImageList")
    @ApiOperation(value = "获取上传图片列表", notes = "获取上传图片列表")
    ServerResponse uploadingImageList(@RequestParam("userToken") String userToken,
                                      @RequestParam("nodeArr") String nodeArr);

    @PostMapping("/data/technologyRecord/technologyRecordList")
    @ApiOperation(value = "查询节点", notes = "查询节点")
    ServerResponse technologyRecordList(@RequestParam("userToken") String userToken,
                                        @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("/data/technologyRecord/unfinishedFlow")
    @ApiOperation(value = "已进场未完工", notes = "已进场未完工")
    List<HouseFlow> unfinishedFlow(@RequestParam("houseId") String houseId);

    @PostMapping("/data/technologyRecord/warehouseList")
    @ApiOperation(value = "已购买材料", notes = "已购买材料")
    List<Warehouse> warehouseList(@RequestParam("houseId") String houseId);

    @PostMapping("/data/technologyRecord/getByProductId")
    @ApiOperation(value = "查仓库", notes = "查仓库")
    ServerResponse getByProductId(@RequestParam("productId") String productId,
                             @RequestParam("houseId") String houseId);
}
