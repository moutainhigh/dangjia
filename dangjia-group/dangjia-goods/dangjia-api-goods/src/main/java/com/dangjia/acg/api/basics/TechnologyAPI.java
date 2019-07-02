package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "工艺管理接口")
@FeignClient("dangjia-service-goods")
public interface TechnologyAPI {

    @PostMapping("/basics/technology/insertTechnology")
    @ApiOperation(value = "新增工艺说明", notes = "新增工艺说明")
    ServerResponse insertTechnology(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("name") String name,
                                    @RequestParam("content") String content,
                                    @RequestParam("workerTypeId") String workerTypeId,
                                    @RequestParam("type") Integer type,
                                    @RequestParam("image") String image,
                                    @RequestParam("materialOrWorker") Integer materialOrWorker);

    @PostMapping("/basics/technology/updateTechnology")
    @ApiOperation(value = "修改工艺说明", notes = "修改工艺说明")
    ServerResponse updateTechnology(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String id,
                                    @RequestParam("name") String name,
                                    @RequestParam("content") String content,
                                    @RequestParam("type") Integer type,
                                    @RequestParam("image") String image);

    @PostMapping("/basics/technology/deleteTechnology")
    @ApiOperation(value = "删除工艺说明", notes = "删除工艺说明")
    ServerResponse deleteTechnology(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String id);

    @PostMapping("/basics/technology/queryTechnology")
    @ApiOperation(value = "查询所有工艺说明", notes = "查询所有工艺说明")
    ServerResponse<PageInfo> queryTechnology(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("pageDTO") PageDTO pageDTO,
                                             @RequestParam("workerTypeId") String workerTypeId,
                                             @RequestParam("name") String name,
                                             @RequestParam("materialOrWorker") Integer materialOrWorker);

    @PostMapping("/basics/technology/queryTechnologyByWgId")
    @ApiOperation(value = "根据商品id查询人工商品关联工艺实体", notes = "根据商品id查询人工商品关联工艺实体")
    ServerResponse queryTechnologyByWgId(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("workerGoodsId") String workerGoodsId);

}
