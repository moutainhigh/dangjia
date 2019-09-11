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

    @PostMapping("/product/DjBasicsMaintain/updateKeywords")
    @ApiOperation(value = "编辑关键词", notes = "编辑关键词")
    ServerResponse updateKeywords(@RequestParam("id") String id,
                                  @RequestParam("keywordName") String keywordName,
                                  @RequestParam("searchItem") String searchItem);

    @PostMapping("/product/DjBasicsMaintain/addRelatedTags")
    @ApiOperation(value = "关联标签", notes = "关联标签")
    ServerResponse addRelatedTags(@RequestParam("id") String id,
                                  @RequestParam("labelIds") String labelIds);

    @PostMapping("/product/DjBasicsMaintain/delKeywords")
    @ApiOperation(value = "删除关键词", notes = "删除关键词")
    ServerResponse delKeywords(@RequestParam("id") String id);

    @PostMapping("/product/DjBasicsMaintain/queryKeywords")
    @ApiOperation(value = "查询关键词", notes = "查询关键词")
    ServerResponse queryKeywords(@RequestParam("id") String id);

}
