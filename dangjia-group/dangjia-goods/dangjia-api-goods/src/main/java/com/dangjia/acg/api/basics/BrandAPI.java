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
 * 品牌
 */
@Api(description = "品牌管理接口")
@FeignClient("dangjia-service-goods")
public interface BrandAPI {

    /**
     * 根据id找到品牌
     *
     * @param brandId
     */
    @PostMapping("/basics/brand/selectBrandById")
    @ApiOperation(value = "根据id找到品牌", notes = "根据id找到品牌")
    ServerResponse selectBrandById(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("brandId") String brandId);

    /**
     * 根据名称找到品牌
     *
     * @param name
     * @return
     */
    @PostMapping("/basics/brand/selectBrandByName")
    @ApiOperation(value = "根据名称找到品牌", notes = "根据名称找到品牌")
    ServerResponse<PageInfo> selectBrandByName(@RequestParam("request") HttpServletRequest request,
                                               @RequestParam("pageDTO") PageDTO pageDTO,
                                               @RequestParam(value = "name") String name);

    /**
     * 查找到所有品牌
     *
     * @return
     */
    @PostMapping("/basics/brand/getAllBrand")
    @ApiOperation(value = "查找到所有品牌", notes = "查找到所有品牌")
    ServerResponse<PageInfo> getAllBrand(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改品牌信息
     *
     * @return
     */
    @PostMapping("/basics/brand/updateBrand")
    @ApiOperation(value = "修改品牌信息", notes = "修改品牌信息")
    ServerResponse updateBrand(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id,
                               @RequestParam("name") String name,
                               @RequestParam("brandSeriesList") String brandSeriesList);

    /**
     * 添加品牌
     *
     * @return
     */
    @PostMapping("/basics/brand/insertBrand")
    @ApiOperation(value = "添加品牌", notes = "添加品牌")
    ServerResponse insertBrand(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("brandSeriesList") String brandSeriesList,
                               @RequestParam("name") String name);

    /**
     * 删除品牌
     *
     * @return
     */
    @PostMapping("/basics/brand/deleteBrand")
    @ApiOperation(value = "删除品牌", notes = "删除品牌")
    ServerResponse deleteBrand(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id);
}
