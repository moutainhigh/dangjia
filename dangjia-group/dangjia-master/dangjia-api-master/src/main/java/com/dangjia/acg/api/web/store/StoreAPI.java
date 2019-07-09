package com.dangjia.acg.api.web.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.Store;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:15
 */
@FeignClient("dangjia-service-master")
@Api(value = "门店管理接口", description = "门店管理接口")
public interface StoreAPI {
    @PostMapping("/web/store/precinctVillage")
    @ApiOperation(value = "门店所管辖的小区集合", notes = "门店所管辖的小区集合")
    ServerResponse getStorePrecinctVillage(@RequestParam("request") HttpServletRequest request, @RequestParam("storeId") String storeId);

    @PostMapping("/web/store/addStore")
    @ApiOperation(value = "创建门店", notes = "创建门店")
    ServerResponse addStore(@RequestParam("store") Store store);


    @PostMapping("/web/store/queryStore")
    @ApiOperation(value = "查询门店", notes = "查询门店")
    ServerResponse queryStore(@RequestParam("cityId") String cityId,@RequestParam("storeName") String storeName,@RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/web/store/updateStore")
    @ApiOperation(value = "编辑门店", notes = "编辑门店")
    ServerResponse updateStore(@RequestParam("store") Store store);

    @PostMapping("/web/store/delStore")
    @ApiOperation(value = "删除门店", notes = "删除门店")
    ServerResponse delStore(@RequestParam("id") String id);

    @PostMapping("/web/store/queryStoreSubscribe")
    @ApiOperation(value = "查询门店预约记录", notes = "查询门店预约记录")
    ServerResponse queryStoreSubscribe(@RequestParam("searchKey") String searchKey,@RequestParam("houseChoiceCase") PageDTO pageDTO);

    /**
     * 门店预约插入
     * @param storeId 门店ID
     * @param storeName 门店名称
     * @param customerName 客户名称
     * @param customerPhone 客户电话
     * @param modifyDate 预约时间
     * @return
     */
    @PostMapping("/app/store/subscribe")
    @ApiOperation(value = "门店预约", notes = "门店预约")
     ServerResponse storeSubscribe(@RequestParam("storeId") String storeId,
                                   @RequestParam("storeName") String storeName,
                                   @RequestParam("customerName") String customerName,
                                   @RequestParam("customerPhone") String customerPhone,
                                   @RequestParam("modifyDate") Date modifyDate);


    @PostMapping("/app/store/distance")
    @ApiOperation(value = "查询门店(按距离)", notes = "查询门店(按距离)")
    ServerResponse queryStoreDistance(@RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("storeName") String storeName);



    @PostMapping("/app/store/IndexqueryStore")
    @ApiOperation(value = "首页查询门店", notes = "首页查询门店")
    ServerResponse IndexqueryStore(@RequestParam("cityId") String cityId,
                                   @RequestParam("latitude") String latitude,
                                   @RequestParam("longitude") String longitude);

    @PostMapping("web/store/getStoreProfitList")
    @ApiOperation(value = "门店利润列表（利润统计）", notes = "门店利润列表（利润统计）")
    ServerResponse getStoreProfitList(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("pageDTO")PageDTO pageDTO,
                                      @RequestParam("searchKey")String searchKey);


    @PostMapping("/app/store/Callback")
    @ApiOperation(value = "回访记录", notes = "回访记录")
    ServerResponse Callback(@RequestParam("storeSubscribeId") String storeSubscribeId,@RequestParam("info") String info);



}
