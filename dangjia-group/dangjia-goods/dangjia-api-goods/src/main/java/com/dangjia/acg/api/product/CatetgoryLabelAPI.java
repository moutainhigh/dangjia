package com.dangjia.acg.api.product;

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
 * @类 名： CategoryLabelController.java
 * @功能描述：
 * @作者信息： fzh
 * @创建时间： 2019-09-11
 */
@Api(description = "类别标签管理接口")
@FeignClient("dangjia-service-goods")
public interface CatetgoryLabelAPI {
    /**
     * 时间询所有类别标签
     * @param request
     * @param pageDTO
     * @return
     */
    @PostMapping("/product/categoryLabel/getAllCategoryLabel")
    @ApiOperation(value = "查询所有类别标签", notes = "查询所有类别标签")
    ServerResponse<PageInfo> getAllCategoryLabel(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 查询所有的分类标签列表
     * @param request
     * @return
     */
    @PostMapping("/product/categoryLabel/getAllCategoryLabelList")
    @ApiOperation(value = "查询所有类别标签列表", notes = "查询所有类别标签列表")
    ServerResponse getAllCategoryLabelList(@RequestParam("request") HttpServletRequest request);
    /**
     * 修改类别标签
     *
     * @param labelId   标签ID
     * @param labelName 标签名称
     * @return 接口
     */
    @PostMapping("/product/categoryLabel/updateCategoryLabel")
    @ApiOperation(value = "修改类别标签", notes = "修改类别标签")
    ServerResponse updateCategoryLabel(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("labelId") String labelId,
                                       @RequestParam("labelName") String labelName,
                                       @RequestParam("sort") int sort);

    /**
     * 新增类别标签
     *
     * @param labelName 标签名称
     * @return
     */
    @PostMapping("/product/categoryLabel/insertCategoryLabel")
    @ApiOperation(value = "新增类别标签", notes = "新增类别标签")
    ServerResponse insertCategoryLabel(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("labelName") String labelName);

    /**
     * 根据id查询类别对象
     *
     * @param labelId
     * @return
     */
    @PostMapping("/product/categoryLabel/selectCategoryLabelById")
    @ApiOperation(value = "根据id查询类别标签对象", notes = "根据id查询类别标签对象")
    ServerResponse selectCategoryLabelById(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("labelId") String labelId);

    /**
     * 根据ID删除类别标签
     *
     * @param labelId
     * @return
     */
    @PostMapping("/product/categoryLabel/deleteCategoryLabelById")
    @ApiOperation(value = "根据ID删除类别标签", notes = "根据ID删除类别标签")
    ServerResponse deleteCategoryLabelById(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("labelId") String labelId);

}
