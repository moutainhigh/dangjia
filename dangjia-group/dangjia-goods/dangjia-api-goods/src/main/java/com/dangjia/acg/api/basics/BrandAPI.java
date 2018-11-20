package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @param brandId
     */
    @PostMapping("/basics/brand/selectBrandById")
    @ApiOperation(value = "根据id找到品牌", notes = "根据id找到品牌")
    public ServerResponse selectBrandById(String brandId);

    /**
     * 根据名称找到品牌
     * @param name
     * @return
     */
    @PostMapping("/basics/brand/selectBrandByName")
    @ApiOperation(value = "根据名称找到品牌", notes = "根据名称找到品牌")
    public ServerResponse<PageInfo> selectBrandByName(@RequestParam("pageDTO") PageDTO pageDTO, @RequestParam(value = "name") String name);
    /**
     * 查找到所有品牌
     * @return
     */
    @PostMapping("/basics/brand/getAllBrand")
    @ApiOperation(value = "查找到所有品牌", notes = "查找到所有品牌")
    public ServerResponse<PageInfo> getAllBrand(@RequestParam("pageDTO") PageDTO pageDTO);
    /**
     * 修改品牌信息
     * @return
     */
    @PostMapping("/basics/brand/updateBrand")
    @ApiOperation(value = "修改品牌信息", notes = "修改品牌信息")
    public ServerResponse updateBrand(String id,String name,String brandSeriesList);
    /**
     * 添加品牌
     * @return
     */
    @PostMapping("/basics/brand/insertBrand")
    @ApiOperation(value = "添加品牌", notes = "添加品牌")
    public ServerResponse insertBrand(String brandSeriesList,String name);
    /**
     * 删除品牌
     * @return
     */
    @PostMapping("/basics/brand/deleteBrand")
    @ApiOperation(value = "删除品牌", notes = "删除品牌")
    public ServerResponse deleteBrand(String id);
}
