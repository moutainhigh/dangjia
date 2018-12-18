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
 * 
 * 
   * @类 名： UnitController.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-13下午3:55:12
 */
@Api(description = "单位管理接口")
@FeignClient("dangjia-service-goods")
public interface UnitAPI {
    /**
     * 查询所有商品单位
     * @return
     */
    @PostMapping("/basics/unit/getAllUnit")
    @ApiOperation(value = "查询所有商品单位", notes = "查询所有商品单位")
    public ServerResponse<PageInfo> getAllUnit(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO);
    
    /**
     * 修改商品单位
     * @param unitId 单位ID
     * @param unitName	单位名称
     * @return	接口
     */
    @PostMapping("/basics/unit/updateUnit")
    @ApiOperation(value = "修改商品单位", notes = "修改商品单位")
    public ServerResponse updateUnit(@RequestParam("request") HttpServletRequest request,@RequestParam("unitId") String unitId, @RequestParam("unitName") String unitName);
    /**
     * 新增商品单位
     * @param unitName 单位名称
     * @return
     */
    @PostMapping("/basics/unit/insertUnit")
    @ApiOperation(value = "新增商品单位", notes = "新增商品单位")
    public ServerResponse insertUnit(@RequestParam("request") HttpServletRequest request,@RequestParam("unitName") String unitName);

    /**
     * 根据id查询单位对象
     * @param unitId
     * @return
     */
    @PostMapping("/basics/unit/selectunitById")
    @ApiOperation(value = "根据id查询单位对象", notes = "根据id查询单位对象")
    public ServerResponse selectunitById(@RequestParam("request") HttpServletRequest request,@RequestParam("unitId") String unitId);
    /**
     * 根据ID删除商品单位
     * @param unitId
     * @return
     */
    @PostMapping("/basics/unit/deleteById")
    @ApiOperation(value = "根据ID删除商品单位", notes = "根据ID删除商品单位")
    public ServerResponse deleteById(@RequestParam("request") HttpServletRequest request,@RequestParam("unitId") String unitId);
}
