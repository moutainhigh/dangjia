package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:18
 * APP直接调用
 */
@Api(description = "精算操作")
@FeignClient("dangjia-service-goods")
public interface ActuaryOperationAPI {

    /**
     * 精算商品详情
     *gId="+bm.getGoodsId()+"&cityId="+cityId+"&type="+3+"&title=服务商品详情"
     */
    @PostMapping("/actuary/actuaryOperation/getCommo")
    @ApiOperation(value = "精算商品详情", notes = "精算商品详情")
    ServerResponse getCommo(@RequestParam("gId")String gId,@RequestParam("cityId")String cityId,@RequestParam("type")int type);

    /**
     * 工序明细
     * userToken houseId workerTypeId type cityId
     */
    @PostMapping("/actuary/actuaryOperation/confirmActuaryDetail")
    @ApiOperation(value = "工序明细", notes = "工序明细")
    ServerResponse confirmActuaryDetail(@RequestParam("userToken")String userToken, @RequestParam("houseId")String houseId
            ,@RequestParam("workerTypeId")String workerTypeId,@RequestParam("type")int type,@RequestParam("cityId")String cityId);

    /**
     * 精算详情
     */
    @PostMapping("/actuary/actuaryOperation/confirmActuary")
    @ApiOperation(value = "精算详情", notes = "精算详情")
    ServerResponse confirmActuary(@RequestParam("userToken")String userToken, @RequestParam("houseId")String houseId
    ,@RequestParam("cityId")String cityId);

}
