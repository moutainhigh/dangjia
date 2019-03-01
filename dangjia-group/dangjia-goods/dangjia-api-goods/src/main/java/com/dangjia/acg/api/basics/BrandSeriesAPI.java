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

/**
 * @类 名： BrandExpalinController.java
 * @功能描述：
 * @作者信息： hb
 * @创建时间： 2018-9-13下午5:28:18
 */
@Api(description = "品牌系列管理接口")
@FeignClient("dangjia-service-goods")
public interface BrandSeriesAPI {
    /**
     * 查询所有
     *
     * @return
     */
    @PostMapping("/basics/brandSeries/getAllBrandExplain")
    @ApiOperation(value = "查询所有", notes = "查询所有")
    ServerResponse<PageInfo> getAllBrandExplain(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改
     *
     * @param id
     * @return
     */
    @PostMapping("/basics/brandSeries/updateBrandExplain")
    @ApiOperation(value = "修改", notes = "修改")
    ServerResponse updateBrandExplain(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("id") String id,
                                      @RequestParam("name") String name,
                                      @RequestParam("content") String content);

    /**
     * 新增
     *
     * @param brandId
     * @return
     */
    @PostMapping("/basics/brandSeries/insetBrandExplain")
    @ApiOperation(value = "新增", notes = "新增")
    ServerResponse insetBrandExplain(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("name") String name,
                                     @RequestParam("content") String content,
                                     @RequestParam("brandId") String brandId);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/basics/brandSeries/deleteBrandExplain")
    @ApiOperation(value = "删除", notes = "删除")
    ServerResponse deleteBrandExplain(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("id") String id);
}
