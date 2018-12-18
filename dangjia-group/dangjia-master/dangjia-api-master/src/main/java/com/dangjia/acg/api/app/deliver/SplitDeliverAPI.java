package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 20:11
 */
@FeignClient("dangjia-service-master")
@Api(value = "收货管理", description = "收货管理")
public interface SplitDeliverAPI {

    /**
     * 部分收货
     */
    @PostMapping("app/deliver/splitDeliver/partSplitDeliver")
    @ApiOperation(value = "部分收货", notes = "部分收货")
    ServerResponse partSplitDeliver(@RequestParam("splitDeliverId")String splitDeliverId, @RequestParam("image")String image
            , @RequestParam("splitItemList")String splitItemList);

    /**
     * 确认收货
     */
    @PostMapping("app/deliver/splitDeliver/affirmSplitDeliver")
    @ApiOperation(value = "确认收货", notes = "确认收货")
    ServerResponse affirmSplitDeliver(@RequestParam("splitDeliverId")String splitDeliverId, @RequestParam("image")String image);

    /**
     * 委托大管家收货
     */
    @PostMapping("app/deliver/splitDeliver/supState")
    @ApiOperation(value = "委托大管家收货", notes = "委托大管家收货")
    ServerResponse supState(@RequestParam("splitDeliverId")String splitDeliverId);

    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    @PostMapping("app/deliver/splitDeliver/splitDeliverList")
    @ApiOperation(value = "收货列表", notes = "收货列表")
    ServerResponse splitDeliverList(@RequestParam("houseId")String houseId, @RequestParam("shipState")int shipState);
}
