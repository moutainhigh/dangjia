package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

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
    ServerResponse partSplitDeliver(@RequestParam("userToken") String userToken,@RequestParam("splitDeliverId") String splitDeliverId,
                                    @RequestParam("image") String image,
                                    @RequestParam("splitItemList") String splitItemList,
                                    @RequestParam("userRole")Integer userRole);

    /**
     * 确认收货
     */
    @PostMapping("app/deliver/splitDeliver/affirmSplitDeliver")
    @ApiOperation(value = "确认收货", notes = "确认收货")
    ServerResponse affirmSplitDeliver(@RequestParam("userToken") String userToken,@RequestParam("splitDeliverId") String splitDeliverId,
                                      @RequestParam("image") String image,
                                      @RequestParam("splitItemList") String splitItemList,
                                      @RequestParam("userRole")Integer userRole);

    /**
     * 委托大管家收货
     */
    @PostMapping("app/deliver/splitDeliver/supState")
    @ApiOperation(value = "委托大管家收货", notes = "委托大管家收货")
    ServerResponse supState(@RequestParam("splitDeliverId") String splitDeliverId);

    /**
     * 发货单明细
     */
    @PostMapping("app/deliver/splitDeliver/splitDeliverDetail")
    @ApiOperation(value = "发货单明细", notes = "发货单明细")
    ServerResponse splitDeliverDetail(@RequestParam("splitDeliverId") String splitDeliverId);

    @GetMapping("/web/deliver/splitDeliver/export")
    @ApiOperation(value = "发货单明细-导出", notes = "发货单明细-导出", produces = "*/*,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/octet-stream")
    ServerResponse exportDeliverDetail(HttpServletResponse response, Integer deliverType, String splitDeliverId);
    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    @PostMapping("app/deliver/splitDeliver/splitDeliverList")
    @ApiOperation(value = "收货列表", notes = "收货列表")
    ServerResponse splitDeliverList( @RequestParam("pageDTO")PageDTO pageDTO,
                                     @RequestParam("houseId") String houseId,
            @RequestParam("shipState") Integer shipState);



    /**
     * 确认安装
     * @param splitDeliverId
     * @param userToken
     * @return
     */
    @PostMapping("app/deliver/order/confirmInstallation")
    @ApiOperation(value = "确认安装", notes = "确认安装")
    ServerResponse confirmInstallation(@RequestParam("userToken") String userToken,@RequestParam("splitDeliverId") String splitDeliverId);



}
