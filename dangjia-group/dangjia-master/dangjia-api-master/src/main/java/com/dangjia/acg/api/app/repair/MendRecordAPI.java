package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 14:00
 */
@FeignClient("dangjia-service-master")
@Api(value = "要补退记录", description = "要补退记录")
public interface MendRecordAPI {

    @PostMapping(value = "app/repair/mendRecord/mendOrderDetail")
    @ApiOperation(value = "补退明细", notes = "补退明细")
    ServerResponse mendOrderDetail(@RequestParam("mendOrderId")String mendOrderId,@RequestParam("type")Integer type);

    @PostMapping(value = "app/repair/mendRecord/recordList")
    @ApiOperation(value = "记录列表", notes = "记录列表")
    ServerResponse recordList(@RequestParam("houseId")String houseId,@RequestParam("type")Integer type);

    @PostMapping(value = "app/repair/mendRecord/mendList")
    @ApiOperation(value = "要补退记录", notes = "要补退记录")
    ServerResponse mendList(@RequestParam("houseId")String houseId);
}
