package com.dangjia.acg.api.config;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ServiceType;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务类型接口
 */
@Api(description = "商品3.0服务类型配置接口")
@FeignClient("dangjia-service-goods")
public interface ServiceTypeAPI {

    /**
     * 根据ID找对应配置类型
     *
     * @param id
     * @return
     */
    @PostMapping("/web/config/serviceType/getServiceTypeById")
    @ApiOperation(value = "根据ID查询对应的装修类型", notes = "根据ID查询对应的装修类型")
    ServiceType getServiceTypeById(@RequestParam("cityId") String cityId,
                                      @RequestParam("id") String id);
    /**
     * 根据id找到服务类型
     *
     * @param id
     */
    @PostMapping("/web/config/serviceType/selectServiceTypeById")
    @ApiOperation(value = "根据id找到服务类型", notes = "根据id找到服务类型")
    ServerResponse selectServiceTypeById(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id);
    /**
     * 查找到所有服务类型
     *
     * @return
     */
    @PostMapping("/web/config/serviceType/selectServiceTypeList")
    @ApiOperation(value = "查找到所有服务类型", notes = "查找到所有服务类型")
    ServerResponse<PageInfo> selectServiceTypeList(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改服务类型信息
     *
     * @return
     */
    @PostMapping("/web/config/serviceType/updateServiceType")
    @ApiOperation(value = "修改服务类型信息", notes = "修改服务类型信息")
    ServerResponse updateServiceType(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id,
                               @RequestParam("name") String name,
                               @RequestParam("image") String image);

    /**
     * 新增服务类型
     *
     * @return
     */
    @PostMapping("/web/config/serviceType/insertServiceType")
    @ApiOperation(value = "新增服务类型", notes = "新增服务类型")
    ServerResponse insertServiceType(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("name") String name,
                               @RequestParam("image") String image);

    /**
     * 删除服务类型
     *
     * @return
     */
    @PostMapping("/web/config/serviceType/deleteServiceType")
    @ApiOperation(value = "删除服务类型", notes = "删除服务类型")
    ServerResponse deleteServiceType(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id);
}
