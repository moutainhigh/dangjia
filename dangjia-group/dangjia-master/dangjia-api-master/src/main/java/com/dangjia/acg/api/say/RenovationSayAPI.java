package com.dangjia.acg.api.say;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("dangjia-service-master")
@Api(value = "装修说接口", description = "装修说接口")
public interface RenovationSayAPI {

    @PostMapping("web/renovation/insertRenovationSay")
    @ApiOperation(value = "新增装修说", notes = "新增装修说")
    ServerResponse insertRenovationSay(@RequestParam("content") String content,
                                       @RequestParam("coverImage") String coverImage,
                                       @RequestParam("contentImage") String contentImage);

    @PostMapping("web/renovation/upDateRenovationSay")
    @ApiOperation(value = "修改装修说", notes = "修改装修说")
    ServerResponse upDateRenovationSay(@RequestParam("id") String id,
                                       @RequestParam("content") String content,
                                       @RequestParam("coverImage") String coverImage,
                                       @RequestParam("contentImage") String contentImage);


    @PostMapping("web/renovation/deleteRenovationSay")
    @ApiOperation(value = "删除装修说", notes = "删除装修说")
    ServerResponse deleteRenovationSay(@RequestParam("id") String id);

    @PostMapping("web/renovation/queryRenovationSayList")
    @ApiOperation(value = "查询装修说列表", notes = "查询装修说列表")
    ServerResponse queryRenovationSayList(@RequestParam("pageDTO")PageDTO pageDTO);

    @PostMapping("web/renovation/queryRenovationSayData")
    @ApiOperation(value = "查询装修说详情", notes = "查询装修说详情")
    ServerResponse queryRenovationSayData(@RequestParam("userToken") String userToken,
                                          @RequestParam("id")String id);

    @PostMapping("app/renovation/queryAppRenovationSayList")
    @ApiOperation(value = "app查询装修说列表", notes = "app查询装修说列表")
    ServerResponse queryAppRenovationSayList(@RequestParam("userToken") String userToken);

    @PostMapping("app/renovation/setThumbUp")
    @ApiOperation(value = "装修说点赞", notes = "装修说点赞")
    ServerResponse setThumbUp(@RequestParam("userToken") String userToken,
                              @RequestParam("id") String id);

    @PostMapping("app/renovation/setPageView")
    @ApiOperation(value = "装修说浏览量", notes = "装修说浏览量")
    ServerResponse setPageView(@RequestParam("id") String id);


}

