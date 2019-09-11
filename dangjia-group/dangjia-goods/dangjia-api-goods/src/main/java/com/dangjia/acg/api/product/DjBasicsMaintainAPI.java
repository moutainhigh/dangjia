package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Api(description = "关键词维护接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsMaintainAPI {

    @PostMapping("/product/DjBasicsMaintain/addKeywords")
    @ApiOperation(value = "添加关键词", notes = "添加关键词")
    ServerResponse addKeywords(@RequestParam("keywordName") String keywordName,
                               @RequestParam("searchItem") String searchItem);

}
