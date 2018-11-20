package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @类 名： serverPortAPI
 * @功能描述：
 * @作者信息： zmj
 * @创建时间： 2018-11-15上午13:35:10
 */
@Api(description = "服务化接口")
@FeignClient("dangjia-service-goods")
public interface ServerPortAPI {

    //根据内容模糊搜索
    @PostMapping("/actuary/serverPort/getSearchBox")
    @ApiOperation(value = "根据内容模糊搜索", notes = "根据内容模糊搜索")
    public ServerResponse getSearchBox(@RequestParam("content")String content);

    //查询热门搜索
    @PostMapping("/actuary/serverPort/getHeatSearchBox")
    @ApiOperation(value = "查询热门搜索", notes = "查询热门搜索")
    public ServerResponse getHeatSearchBox();

}