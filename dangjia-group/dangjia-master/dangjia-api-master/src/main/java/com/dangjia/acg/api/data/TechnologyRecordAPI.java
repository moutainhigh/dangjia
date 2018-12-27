package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/2 0002
 * Time: 17:24
 */
@FeignClient("dangjia-service-master")
@Api(value = "验收工艺节点相关", description = "验收工艺节点相关")
public interface TechnologyRecordAPI {


    @PostMapping("/data/technologyRecord/uploadingImageList")
    @ApiOperation(value = "获取上传图片列表", notes = "获取上传图片列表")
    ServerResponse uploadingImageList(@RequestParam("userToken")String userToken,@RequestParam("nodeArr")String nodeArr);

    @PostMapping("/data/technologyRecord/technologyRecordList")
    @ApiOperation(value = "查询节点", notes = "查询节点")
    ServerResponse technologyRecordList(@RequestParam("userToken")String userToken, @RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("/data/technologyRecord/addTechnologyRecord")
    @ApiOperation(value = "添加工艺验收节点", notes = "添加工艺验收节点")
    void addTechnologyRecord(@RequestParam("technologyRecord") TechnologyRecord technologyRecord);
}
