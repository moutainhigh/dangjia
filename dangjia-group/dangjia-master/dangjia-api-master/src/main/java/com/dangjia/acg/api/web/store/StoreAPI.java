package com.dangjia.acg.api.web.store;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.Store;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:15
 */
@FeignClient("dangjia-service-master")
@Api(value = "门店管理接口", description = "门店管理接口")
public interface StoreAPI {

    @PostMapping("/web/store/addStore")
    @ApiOperation(value = "创建门店", notes = "创建门店")
    ServerResponse addStore(@RequestParam("store") Store store);


    @PostMapping("/web/store/queryStore")
    @ApiOperation(value = "查询门店", notes = "查询门店")
    ServerResponse queryStore(@RequestParam("cityId") String cityId,@RequestParam("storeName") String storeName);

    @PostMapping("/web/store/updateStore")
    @ApiOperation(value = "编辑门店", notes = "编辑门店")
    ServerResponse updateStore(@RequestParam("store") Store store);

    @PostMapping("/web/store/delStore")
    @ApiOperation(value = "删除门店", notes = "删除门店")
    ServerResponse delStore(@RequestParam("id") String id);

    @PostMapping("/web/store/queryStoreSubscribe")
    @ApiOperation(value = "查询门店预约记录", notes = "查询门店预约记录")
    ServerResponse queryStoreSubscribe(@RequestParam("searchKey") String searchKey);


}
