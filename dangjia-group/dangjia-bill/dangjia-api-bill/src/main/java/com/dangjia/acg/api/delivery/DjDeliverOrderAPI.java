package com.dangjia.acg.api.delivery;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

@Api(description = "所有订单表接口")
@FeignClient("dangjia-service-bill")
public class DjDeliverOrderAPI {


    //查询当前用户的所有订单

    //当前花费

    //录价

    //工匠详情

}
