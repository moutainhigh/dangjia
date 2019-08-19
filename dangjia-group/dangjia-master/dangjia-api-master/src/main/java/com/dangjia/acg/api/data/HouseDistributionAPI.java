package com.dangjia.acg.api.data;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.house.WebsiteVisit;
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
@Api(value = "验房分销数据接口", description = "验房分销数据接口")
public interface HouseDistributionAPI {
    /**
     * 获取所有验房分销
     *
     * @param houseDistribution
     * @return
     */
    @PostMapping("/data/house/distribution/list")
    @ApiOperation(value = "获取所有验房分销", notes = "获取所有验房分销")
    ServerResponse getHouseDistribution(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("houseDistribution") HouseDistribution houseDistribution,
                                        @RequestParam("startDate") String startDate,
                                        @RequestParam("endDate") String endDate,
                                        @RequestParam("searchKey") String searchKey);

    /**
     * 新增
     *
     * @param houseDistribution
     * @return
     */
    @PostMapping("/data/house/distribution/add")
    @ApiOperation(value = "新增验房分销", notes = "新增验房分销")
    ServerResponse addHouseDistribution(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseDistribution") HouseDistribution houseDistribution);


    /**
     * 新增路由访问数据
     *
     * @param websiteVisit
     * @return
     */
    @PostMapping("/data/website/visit")
    @ApiOperation(value = "新增路由访问数据", notes = "新增路由访问数据")
    ServerResponse addWebsiteVisit(@RequestParam("request") HttpServletRequest request, @RequestParam("websiteVisit") WebsiteVisit websiteVisit);
}
