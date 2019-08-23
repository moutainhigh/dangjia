package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseDistributionConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * author: qiyuxiang
 * Date: 2019/1/16 0001
 * Time: 17:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "验房价格配置接口", description = "验房价格配置接口")
public interface HouseDistributionConfigAPI {

    /**
     * 获取所有验房配置
     *
     * @param houseDistributionConfig
     * @return
     */
    @PostMapping("/config/distribution/list")
    @ApiOperation(value = "获取所有验房配置", notes = "获取所有验房配置")
    ServerResponse getHouseDistributionConfigs(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("houseDistributionConfig") HouseDistributionConfig houseDistributionConfig);

    /**
     * 删除验房配置
     *
     * @param id
     * @return
     */
    @PostMapping("/config/distribution/del")
    @ApiOperation(value = "删除验房配置", notes = "删除验房配置")
    ServerResponse delHouseDistributionConfig(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String id);

    /**
     * 修改验房配置
     *
     * @param houseDistributionConfig
     * @return
     */
    @PostMapping("/config/distribution/edit")
    @ApiOperation(value = "修改验房配置", notes = "修改验房配置")
    ServerResponse editHouseDistributionConfig(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("houseDistributionConfig") HouseDistributionConfig houseDistributionConfig);

    /**
     * 新增验房配置
     *
     * @param houseDistributionConfig
     * @return
     */
    @PostMapping("/config/distribution/add")
    @ApiOperation(value = "新增验房配置", notes = "新增验房配置")
    ServerResponse addHouseDistributionConfig(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseDistributionConfig") HouseDistributionConfig houseDistributionConfig);
}
