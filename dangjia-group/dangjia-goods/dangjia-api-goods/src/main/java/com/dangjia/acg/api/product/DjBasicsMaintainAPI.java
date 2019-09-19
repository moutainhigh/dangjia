package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Api(description = "关键词维护接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsMaintainAPI {



    @PostMapping("/product/djBasicsMaintain/addKeywords")
    @ApiOperation(value = "添加关键词", notes = "添加关键词")
    ServerResponse addKeywords(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("keywordName") String keywordName,
                               @RequestParam("searchItem") String searchItem);

    @PostMapping("/product/djBasicsMaintain/updateKeywords")
    @ApiOperation(value = "编辑关键词", notes = "编辑关键词")
    ServerResponse updateKeywords(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("id") String id,
                                  @RequestParam("keywordName") String keywordName,
                                  @RequestParam("searchItem") String searchItem);

    @PostMapping("/product/djBasicsMaintain/addRelatedTags")
    @ApiOperation(value = "关联标签", notes = "关联标签")
    ServerResponse addRelatedTags(@RequestParam("request") HttpServletRequest requestm,
                                  @RequestParam("id") String id,
                                  @RequestParam("labelIds") String labelIds);

    @PostMapping("/product/djBasicsMaintain/delKeywords")
    @ApiOperation(value = "删除关键词", notes = "删除关键词")
    ServerResponse delKeywords(@RequestParam("request") HttpServletRequest request,@RequestParam("id") String id);

    @PostMapping("/product/djBasicsMaintain/queryKeywords")
    @ApiOperation(value = "查询关键词", notes = "查询关键词")
    ServerResponse queryKeywords(@RequestParam("request") HttpServletRequest request,@RequestParam("id") String id);

    @PostMapping("/app/product/djBasicsMaintain/queryMatchWord")
    @ApiOperation(value = "查询配置关键词名称", notes = "查询配置关键词名称")
    ServerResponse queryMatchWord(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("name")String name);
}
