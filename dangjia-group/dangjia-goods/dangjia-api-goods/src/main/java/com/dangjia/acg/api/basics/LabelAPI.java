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
 * @类 名： LabelController.java
 * @功能描述：
 * @作者信息： ysl
 * @创建时间： 2018-12-11下午1:55:12
 */
@Api(description = "label管理接口")
@FeignClient("dangjia-service-goods")
public interface LabelAPI {
    /**
     * 查询所有商品标签
     *
     * @return
     */
    @PostMapping("/basics/label/getAllLabel")
    @ApiOperation(value = "查询所有商品标签", notes = "查询所有商品标签")
    ServerResponse<PageInfo> getAllLabel(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改商品标签
     *
     * @param labelId   标签ID
     * @param labelName 标签名称
     * @return 接口
     */
    @PostMapping("/basics/label/updateLabel")
    @ApiOperation(value = "修改商品标签", notes = "修改商品标签")
    ServerResponse updateLabel(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("labelId") String labelId,
                               @RequestParam("labelName") String labelName);

    /**
     * 新增商品标签
     *
     * @param labelName 标签名称
     * @return
     */
    @PostMapping("/basics/label/insertLabel")
    @ApiOperation(value = "新增商品标签", notes = "新增商品标签")
    ServerResponse insertLabel(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("labelName") String labelName);

    /**
     * 根据id查询标签对象
     *
     * @param labelId
     * @return
     */
    @PostMapping("/basics/label/selectLabelById")
    @ApiOperation(value = "根据id查询标签对象", notes = "根据id查询标签对象")
    ServerResponse selectLabelById(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("labelId") String labelId);

    /**
     * 根据ID删除商品标签
     *
     * @param labelId
     * @return
     */
    @PostMapping("/basics/label/deleteById")
    @ApiOperation(value = "根据ID删除商品标签", notes = "根据ID删除商品标签")
    ServerResponse deleteById(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("labelId") String labelId);

    /**
     * 批量添加/修改货品标签
     *
     * @param request
     * @param productLabeList
     * @return
     */
    @PostMapping("/basics/label/saveProductLabelList")
    @ApiOperation(value = "批量添加/修改货品标签", notes = "批量添加/修改货品标签")
    ServerResponse saveProductLabelList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("productLabeList") String productLabeList);
}
